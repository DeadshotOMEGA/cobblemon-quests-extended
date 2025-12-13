package cobblemonquestsextended.cobblemon_quests_extended.preview;

import cobblemonquestsextended.cobblemon_quests_extended.domain.CobblemonTaskModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates natural language quest descriptions from task configuration.
 *
 * Converts a CobblemonTaskModel into human-readable quest text like:
 * "Catch 5 shiny Pikachu or Raichu using a Premier Ball in the Plains biome during daytime."
 *
 * The generator follows a structured approach:
 * 1. Action phrase (what to do)
 * 2. Target phrase (amount, attributes, Pokemon)
 * 3. Condition phrases (how, where, when)
 */
public class NaturalLanguageGenerator {

    /**
     * Maps action identifiers to their display verbs.
     * Each action has a specific verb that best describes the player's action.
     */
    private static final Map<String, String> ACTION_VERBS = Map.ofEntries(
        Map.entry("catch", "Catch"),
        Map.entry("defeat", "Defeat"),
        Map.entry("defeat_npc", "Defeat"),
        Map.entry("defeat_player", "Defeat in battle"),
        Map.entry("evolve", "Evolve"),
        Map.entry("evolve_into", "Evolve into"),
        Map.entry("hatch_egg", "Hatch"),
        Map.entry("level_up", "Level up"),
        Map.entry("level_up_to", "Level up to level"),
        Map.entry("mega_evolve", "Mega Evolve"),
        Map.entry("obtain", "Obtain"),
        Map.entry("register", "Register"),
        Map.entry("have_registered", "Have registered"),
        Map.entry("scan", "Scan"),
        Map.entry("terastallize", "Terastallize"),
        Map.entry("trade_away", "Trade away"),
        Map.entry("trade_for", "Trade for"),
        Map.entry("reel", "Fish up"),
        Map.entry("select_starter", "Choose as starter"),
        Map.entry("kill", "Defeat (KO)"),
        Map.entry("faint_pokemon", "Faint"),
        Map.entry("change_form", "Change form of"),
        Map.entry("revive_fossil", "Revive from fossil"),
        Map.entry("use_z_move", "Use Z-Move with"),
        Map.entry("dynamax", "Dynamax"),
        Map.entry("gigantamax", "Gigantamax"),
        Map.entry("ultra_burst", "Ultra Burst"),
        Map.entry("release", "Release"),
        Map.entry("throw_ball", "Throw ball at"),
        Map.entry("send_out", "Send out"),
        Map.entry("give_held_item", "Give held item to"),
        Map.entry("heal", "Heal")
    );

    /**
     * Actions where the amount represents individual events (catch 5x = catch 5 times)
     */
    private static final List<String> COUNT_BASED_ACTIONS = List.of(
        "catch", "defeat", "defeat_npc", "defeat_player", "evolve", "evolve_into",
        "hatch_egg", "obtain", "register", "scan", "trade_away", "trade_for",
        "reel", "kill", "faint_pokemon", "revive_fossil", "release", "throw_ball",
        "send_out", "give_held_item", "heal", "mega_evolve", "terastallize",
        "use_z_move", "dynamax", "gigantamax", "ultra_burst", "change_form"
    );

    /**
     * Actions where the amount represents a target level/count (level up to 50)
     */
    private static final List<String> TARGET_BASED_ACTIONS = List.of(
        "level_up_to", "have_registered"
    );

    /**
     * Actions that use the dex progress condition
     */
    private static final List<String> DEX_ACTIONS = List.of(
        "register", "have_registered", "scan"
    );

    /**
     * Generates a complete quest description from a task model.
     *
     * @param model The task model containing all quest configuration
     * @return A Component containing the human-readable quest description
     */
    public Component generate(CobblemonTaskModel model) {
        if (model == null || model.getActions().isEmpty()) {
            return Component.literal("No task configured");
        }

        StringBuilder description = new StringBuilder();

        // 1. Generate action phrase
        description.append(generateActionPhrase(model));

        // 2. Generate target phrase
        String targetPhrase = generateTargetPhrase(model);
        if (!targetPhrase.isEmpty()) {
            description.append(" ").append(targetPhrase);
        }

        // 3. Generate condition phrases
        List<String> conditions = generateConditionPhrases(model);
        for (String condition : conditions) {
            if (!condition.isEmpty()) {
                description.append(" ").append(condition);
            }
        }

        // 4. Finalize with period
        String result = description.toString().trim();
        if (!result.endsWith(".")) {
            result += ".";
        }

        return Component.literal(result);
    }

    /**
     * Generates the action phrase ("Catch", "Defeat", "Evolve into", etc.)
     * Handles multiple actions by joining with "or".
     *
     * @param model The task model
     * @return The action phrase text
     */
    private String generateActionPhrase(CobblemonTaskModel model) {
        List<String> actions = model.getActions();

        if (actions.size() == 1) {
            return ACTION_VERBS.getOrDefault(actions.get(0), capitalize(actions.get(0)));
        }

        // Multiple actions - join with "or"
        List<String> actionVerbs = actions.stream()
            .map(a -> ACTION_VERBS.getOrDefault(a, capitalize(a)))
            .distinct()
            .collect(Collectors.toList());

        return ConditionFormatter.formatOrList(actionVerbs);
    }

    /**
     * Generates the target phrase describing what/how many Pokemon.
     * Example: "5 shiny Fire-type Pikachu or Raichu"
     *
     * @param model The task model
     * @return The target phrase text
     */
    private String generateTargetPhrase(CobblemonTaskModel model) {
        StringBuilder target = new StringBuilder();
        List<String> actions = model.getActions();

        // Handle special cases for certain actions
        if (actions.contains("defeat_player")) {
            if (model.getAmount() > 1) {
                target.append(model.getAmount()).append(" players");
            } else {
                target.append("a player");
            }
            return target.toString();
        }

        if (actions.contains("defeat_npc")) {
            if (model.getAmount() > 1) {
                target.append(model.getAmount()).append(" NPC trainers");
            } else {
                target.append("an NPC trainer");
            }
            // Check if specific NPCs are targeted via forms field
            if (!model.getForms().isEmpty()) {
                List<String> npcNames = model.getForms().stream()
                    .map(ConditionFormatter::formatFormName)
                    .collect(Collectors.toList());
                target.append(" (").append(ConditionFormatter.formatOrList(npcNames)).append(")");
            }
            return target.toString();
        }

        // For level_up_to, the amount IS the target level
        if (actions.contains("level_up_to")) {
            target.append(model.getAmount());
            return target.toString();
        }

        // For have_registered, the amount is the count needed
        if (actions.contains("have_registered")) {
            target.append(model.getAmount());
            return target.toString();
        }

        // For level_up, amount is number of level-ups
        if (actions.contains("level_up")) {
            if (model.getAmount() > 1) {
                target.append(model.getAmount()).append(" levels");
            } else {
                target.append("1 level");
            }
            return target.toString();
        }

        // Standard Pokemon target phrase
        // Amount
        if (model.getAmount() > 1) {
            target.append(model.getAmount()).append(" ");
        } else if (!model.getPokemons().isEmpty()) {
            target.append("a ");
        }

        // Shiny modifier
        if (model.isShiny()) {
            target.append("shiny ");
        }

        // Gender modifier
        if (!model.getGenders().isEmpty()) {
            List<String> formattedGenders = model.getGenders().stream()
                .map(ConditionFormatter::formatGender)
                .collect(Collectors.toList());
            target.append(ConditionFormatter.formatOrList(formattedGenders)).append(" ");
        }

        // Form modifier
        if (!model.getForms().isEmpty() && !actions.contains("defeat_npc")) {
            List<String> formattedForms = model.getForms().stream()
                .filter(f -> !f.equals("normal"))
                .map(ConditionFormatter::formatFormName)
                .collect(Collectors.toList());
            if (!formattedForms.isEmpty()) {
                target.append(ConditionFormatter.formatOrList(formattedForms)).append(" ");
            }
        }

        // Region modifier
        if (!model.getRegions().isEmpty()) {
            List<String> formattedRegions = model.getRegions().stream()
                .map(ConditionFormatter::formatRegion)
                .collect(Collectors.toList());
            target.append(ConditionFormatter.formatOrList(formattedRegions)).append(" ");
        }

        // Type modifier
        if (!model.getPokemonTypes().isEmpty()) {
            List<String> formattedTypes = model.getPokemonTypes().stream()
                .map(t -> ConditionFormatter.formatTypeName(t) + "-type")
                .collect(Collectors.toList());
            target.append(ConditionFormatter.formatOrList(formattedTypes)).append(" ");
        }

        // Pokemon species
        if (model.getPokemons().isEmpty()) {
            // No specific Pokemon - use generic term
            if (model.getAmount() > 1) {
                target.append("Pokemon");
            } else {
                target.append("Pokemon");
            }
        } else {
            List<String> formattedPokemon = model.getPokemons().stream()
                .map(ConditionFormatter::formatPokemonName)
                .collect(Collectors.toList());
            target.append(ConditionFormatter.formatOrList(formattedPokemon));
        }

        return target.toString().trim();
    }

    /**
     * Generates all applicable condition phrases.
     * Returns a list of phrases like "using a Premier Ball", "in the Plains biome".
     *
     * @param model The task model
     * @return List of condition phrase strings
     */
    private List<String> generateConditionPhrases(CobblemonTaskModel model) {
        List<String> conditions = new ArrayList<>();
        List<String> actions = model.getActions();

        // Poke Ball condition
        if (!model.getPokeBallsUsed().isEmpty()) {
            List<String> formattedBalls = model.getPokeBallsUsed().stream()
                .map(ConditionFormatter::formatPokeBall)
                .collect(Collectors.toList());
            conditions.add("using a " + ConditionFormatter.formatOrList(formattedBalls));
        }

        // Nature condition
        if (!model.getNatures().isEmpty()) {
            List<String> formattedNatures = model.getNatures().stream()
                .map(ConditionFormatter::formatNature)
                .collect(Collectors.toList());
            conditions.add("with " + ConditionFormatter.formatOrList(formattedNatures) + " nature");
        }

        // Biome condition
        if (!model.getBiomes().isEmpty()) {
            List<String> formattedBiomes = model.getBiomes().stream()
                .map(ConditionFormatter::formatBiome)
                .collect(Collectors.toList());
            if (formattedBiomes.size() == 1) {
                conditions.add("in a " + formattedBiomes.get(0) + " biome");
            } else {
                conditions.add("in a " + ConditionFormatter.formatOrList(formattedBiomes) + " biome");
            }
        }

        // Dimension condition
        if (!model.getDimensions().isEmpty()) {
            List<String> formattedDimensions = model.getDimensions().stream()
                .map(ConditionFormatter::formatDimension)
                .collect(Collectors.toList());
            conditions.add("in " + ConditionFormatter.formatOrList(formattedDimensions));
        }

        // Time condition
        String timeCondition = ConditionFormatter.formatTimeRange(model.getTimeMin(), model.getTimeMax());
        if (!timeCondition.isEmpty()) {
            conditions.add(timeCondition);
        }

        // Level condition (for level_up actions, level is in target phrase)
        if (!actions.contains("level_up") && !actions.contains("level_up_to")) {
            String levelCondition = ConditionFormatter.formatLevelRange(model.getMinLevel(), model.getMaxLevel());
            if (!levelCondition.isEmpty()) {
                conditions.add(levelCondition);
            }
        }

        // Dex progress condition
        if (actions.stream().anyMatch(DEX_ACTIONS::contains)) {
            String dexProgress = ConditionFormatter.formatDexProgress(model.getDexProgress());
            conditions.add("(" + dexProgress + ")");
        }

        // Mega form condition
        if (!model.getMegaForms().isEmpty() && actions.contains("mega_evolve")) {
            List<String> formattedMegaForms = model.getMegaForms().stream()
                .map(ConditionFormatter::formatMegaForm)
                .collect(Collectors.toList());
            conditions.add("into " + ConditionFormatter.formatOrList(formattedMegaForms) + " form");
        }

        // Tera type condition
        if (!model.getTeraTypes().isEmpty() && actions.contains("terastallize")) {
            List<String> formattedTeraTypes = model.getTeraTypes().stream()
                .map(ConditionFormatter::formatTypeName)
                .collect(Collectors.toList());
            conditions.add("as " + ConditionFormatter.formatOrList(formattedTeraTypes) + " Tera Type");
        }

        // Z-Crystal condition
        if (!model.getZCrystals().isEmpty() && actions.contains("use_z_move")) {
            List<String> formattedZCrystals = model.getZCrystals().stream()
                .map(ConditionFormatter::formatZCrystal)
                .collect(Collectors.toList());
            conditions.add("using " + ConditionFormatter.formatOrList(formattedZCrystals));
        }

        // Dynamax type condition
        if (!model.getDynamaxTypes().isEmpty()) {
            List<String> formattedDynamax = model.getDynamaxTypes().stream()
                .map(ConditionFormatter::formatDynamaxType)
                .collect(Collectors.toList());
            if (actions.contains("dynamax") || actions.contains("gigantamax") || actions.contains("ultra_burst")) {
                conditions.add("(" + ConditionFormatter.formatOrList(formattedDynamax) + ")");
            }
        }

        return conditions;
    }

    /**
     * Capitalizes the first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        // Replace underscores with spaces
        str = str.replace("_", " ");
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
