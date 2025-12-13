package cobblemonquestsextended.cobblemon_quests_extended.validation;

import cobblemonquestsextended.cobblemon_quests_extended.model.CobblemonTaskModel;

import java.util.List;
import java.util.Set;

/**
 * Validates {@link CobblemonTaskModel} instances for consistency and correctness.
 *
 * <p>This validator checks for:</p>
 * <ul>
 *   <li><b>Errors</b> - Critical issues that prevent saving:
 *     <ul>
 *       <li>No action selected</li>
 *       <li>mega_evolve without megaForms</li>
 *       <li>use_z_move without zCrystals</li>
 *       <li>minLevel greater than maxLevel (when both non-zero)</li>
 *     </ul>
 *   </li>
 *   <li><b>Warnings</b> - Potential issues that allow saving:
 *     <ul>
 *       <li>terastallize without teraTypes</li>
 *       <li>dynamax/gigantamax/ultra_burst without dynamaxTypes</li>
 *       <li>defeat_npc/defeat_player with Pokemon filters</li>
 *       <li>pokeBallsUsed on non-catch/reel actions</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * CobblemonTaskModel model = CobblemonTaskModel.builder()
 *     .actions(List.of("mega_evolve"))
 *     .build();
 *
 * ValidationResult result = TaskValidator.validate(model);
 * if (!result.isValid()) {
 *     // Handle errors
 *     result.errors().forEach(error -> System.out.println(error.getTranslationKey()));
 * }
 * }</pre>
 */
public final class TaskValidator {

    // ===== Action Category Sets =====

    /**
     * Actions that involve battle gimmicks (Mega Evolution, Terastallization, Z-Moves, Dynamax).
     */
    private static final Set<String> GIMMICK_ACTIONS = Set.of(
            "mega_evolve", "terastallize", "use_z_move", "dynamax", "gigantamax", "ultra_burst"
    );

    /**
     * Actions that don't involve Pokemon filters (NPC and player battles).
     */
    private static final Set<String> NON_POKEMON_ACTIONS = Set.of(
            "defeat_npc", "defeat_player"
    );

    /**
     * Actions where Poke Ball selection is relevant.
     */
    private static final Set<String> POKEBALL_ACTIONS = Set.of(
            "catch", "reel"
    );

    /**
     * Actions that require dynamaxTypes when used.
     */
    private static final Set<String> DYNAMAX_ACTIONS = Set.of(
            "dynamax", "gigantamax", "ultra_burst"
    );

    private TaskValidator() {
        // Utility class - no instantiation
    }

    /**
     * Validates a task model and returns a result with any errors or warnings.
     *
     * @param model the task model to validate
     * @return a ValidationResult containing any errors or warnings found
     * @throws IllegalArgumentException if model is null
     */
    public static ValidationResult validate(CobblemonTaskModel model) {
        if (model == null) {
            throw new IllegalArgumentException("model cannot be null");
        }

        ValidationResult.Builder builder = ValidationResult.builder();

        // Validate errors (prevent save)
        validateErrors(model, builder);

        // Validate warnings (allow save but inform user)
        validateWarnings(model, builder);

        return builder.build();
    }

    /**
     * Validates only for errors (items that prevent saving).
     *
     * @param model the task model to validate
     * @return a ValidationResult containing only errors
     */
    public static ValidationResult validateErrors(CobblemonTaskModel model) {
        if (model == null) {
            throw new IllegalArgumentException("model cannot be null");
        }

        ValidationResult.Builder builder = ValidationResult.builder();
        validateErrors(model, builder);
        return builder.build();
    }

    /**
     * Validates only for warnings (items that don't prevent saving).
     *
     * @param model the task model to validate
     * @return a ValidationResult containing only warnings
     */
    public static ValidationResult validateWarnings(CobblemonTaskModel model) {
        if (model == null) {
            throw new IllegalArgumentException("model cannot be null");
        }

        ValidationResult.Builder builder = ValidationResult.builder();
        validateWarnings(model, builder);
        return builder.build();
    }

    // ===== Private Validation Methods =====

    private static void validateErrors(CobblemonTaskModel model, ValidationResult.Builder builder) {
        List<String> actions = model.getActions();

        // Error: No action selected
        if (actions.isEmpty()) {
            builder.addError("actions", "no_action_selected");
        }

        // Error: mega_evolve requires megaForms
        if (actions.contains("mega_evolve") && model.getMegaForms().isEmpty()) {
            builder.addError("megaForms", "mega_evolve_requires_mega_forms");
        }

        // Error: use_z_move requires zCrystals
        if (actions.contains("use_z_move") && model.getZCrystals().isEmpty()) {
            builder.addError("zCrystals", "use_z_move_requires_z_crystals");
        }

        // Error: minLevel > maxLevel when both are non-zero
        int minLevel = model.getMinLevel();
        int maxLevel = model.getMaxLevel();
        if (minLevel > 0 && maxLevel > 0 && minLevel > maxLevel) {
            builder.addError("minLevel", "min_level_exceeds_max_level", minLevel, maxLevel);
        }
    }

    private static void validateWarnings(CobblemonTaskModel model, ValidationResult.Builder builder) {
        List<String> actions = model.getActions();

        // Warning: terastallize without teraTypes
        if (actions.contains("terastallize") && model.getTeraTypes().isEmpty()) {
            builder.addWarning("teraTypes", "terastallize_without_tera_types");
        }

        // Warning: dynamax/gigantamax/ultra_burst without dynamaxTypes
        boolean hasDynamaxAction = actions.stream().anyMatch(DYNAMAX_ACTIONS::contains);
        if (hasDynamaxAction && model.getDynamaxTypes().isEmpty()) {
            builder.addWarning("dynamaxTypes", "dynamax_without_dynamax_types");
        }

        // Warning: defeat_npc with Pokemon filters
        if (actions.contains("defeat_npc")) {
            checkPokemonFiltersOnNonPokemonAction(model, "defeat_npc", builder);
        }

        // Warning: defeat_player with Pokemon filters
        if (actions.contains("defeat_player")) {
            checkPokemonFiltersOnNonPokemonAction(model, "defeat_player", builder);
        }

        // Warning: pokeBallsUsed on non-pokeball actions
        if (!model.getPokeBallsUsed().isEmpty()) {
            boolean hasPokeballAction = actions.stream().anyMatch(POKEBALL_ACTIONS::contains);
            if (!hasPokeballAction && !actions.isEmpty()) {
                builder.addWarning("pokeBallsUsed", "pokeballs_ignored_for_action");
            }
        }
    }

    /**
     * Checks if Pokemon filters are set on actions that don't use them.
     */
    private static void checkPokemonFiltersOnNonPokemonAction(
            CobblemonTaskModel model,
            String actionName,
            ValidationResult.Builder builder) {

        // Check each Pokemon filter and warn if set
        if (!model.getPokemons().isEmpty()) {
            builder.addWarning("pokemons", "pokemon_filter_ignored", actionName);
        }
        if (!model.getPokemonTypes().isEmpty()) {
            builder.addWarning("pokemonTypes", "pokemon_filter_ignored", actionName);
        }
        if (model.isShiny()) {
            builder.addWarning("shiny", "pokemon_filter_ignored", actionName);
        }
        if (!model.getNatures().isEmpty()) {
            builder.addWarning("natures", "pokemon_filter_ignored", actionName);
        }
        if (!model.getRegions().isEmpty()) {
            builder.addWarning("regions", "pokemon_filter_ignored", actionName);
        }
        if (!model.getGenders().isEmpty()) {
            builder.addWarning("genders", "pokemon_filter_ignored", actionName);
        }
    }

    // ===== Accessor Methods for Action Sets =====

    /**
     * Returns the set of gimmick actions.
     *
     * @return an immutable set of gimmick action names
     */
    public static Set<String> getGimmickActions() {
        return GIMMICK_ACTIONS;
    }

    /**
     * Returns the set of non-Pokemon actions.
     *
     * @return an immutable set of action names that don't involve Pokemon
     */
    public static Set<String> getNonPokemonActions() {
        return NON_POKEMON_ACTIONS;
    }

    /**
     * Returns the set of Pokeball-relevant actions.
     *
     * @return an immutable set of action names where Pokeball selection matters
     */
    public static Set<String> getPokeballActions() {
        return POKEBALL_ACTIONS;
    }

    /**
     * Returns the set of Dynamax-family actions.
     *
     * @return an immutable set of dynamax-related action names
     */
    public static Set<String> getDynamaxActions() {
        return DYNAMAX_ACTIONS;
    }

    /**
     * Checks if the given action is a gimmick action.
     *
     * @param action the action name to check
     * @return true if the action is a gimmick action
     */
    public static boolean isGimmickAction(String action) {
        return action != null && GIMMICK_ACTIONS.contains(action);
    }

    /**
     * Checks if the given action does not involve Pokemon filters.
     *
     * @param action the action name to check
     * @return true if the action does not use Pokemon filters
     */
    public static boolean isNonPokemonAction(String action) {
        return action != null && NON_POKEMON_ACTIONS.contains(action);
    }

    /**
     * Checks if the given action uses Pokeball selection.
     *
     * @param action the action name to check
     * @return true if the action uses Pokeball selection
     */
    public static boolean isPokeballAction(String action) {
        return action != null && POKEBALL_ACTIONS.contains(action);
    }
}
