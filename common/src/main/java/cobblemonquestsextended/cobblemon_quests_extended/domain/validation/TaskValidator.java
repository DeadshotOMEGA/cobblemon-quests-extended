package cobblemonquestsextended.cobblemon_quests_extended.domain.validation;

import cobblemonquestsextended.cobblemon_quests_extended.domain.CobblemonTaskModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validates CobblemonTaskModel instances.
 * Checks for errors (invalid config) and warnings (potential issues).
 */
public class TaskValidator {

    private static final String MOD_ID = "cobblemon_quests_extended";

    private static final Set<String> CATCH_BATTLE_ACTIONS = Set.of(
        "catch", "obtain", "revive_fossil", "reel", "hatch_egg", "select_starter",
        "defeat", "defeat_player", "defeat_npc", "kill", "faint_pokemon"
    );

    private static final Set<String> LEVEL_ACTIONS = Set.of("level_up", "level_up_to");

    private static final Set<String> DEX_ACTIONS = Set.of("register", "have_registered", "scan");

    private static final Set<String> VALID_DEX_PROGRESS = Set.of("seen", "caught");

    /**
     * Validates the given task model and returns all issues found.
     */
    public ValidationResult validate(CobblemonTaskModel model) {
        List<ValidationIssue> issues = new ArrayList<>();

        // Required field validations (errors)
        validateActions(model, issues);
        validateAmount(model, issues);
        validateTimeRange(model, issues);
        validateLevelRange(model, issues);
        validateDexProgress(model, issues);

        // Warning validations
        validateUnusedFields(model, issues);
        validatePotentialMisconfigurations(model, issues);

        return ValidationResult.of(issues);
    }

    private void validateActions(CobblemonTaskModel model, List<ValidationIssue> issues) {
        if (model.getActions().isEmpty()) {
            issues.add(ValidationIssue.error("actions", MOD_ID + ".validation.actions_required"));
        }
    }

    private void validateAmount(CobblemonTaskModel model, List<ValidationIssue> issues) {
        if (model.getAmount() < 1) {
            issues.add(ValidationIssue.error("amount", MOD_ID + ".validation.amount_positive"));
        }
    }

    private void validateTimeRange(CobblemonTaskModel model, List<ValidationIssue> issues) {
        if (model.getTimeMin() < 0 || model.getTimeMin() > 24000) {
            issues.add(ValidationIssue.error("time_min", MOD_ID + ".validation.time_range"));
        }
        if (model.getTimeMax() < 0 || model.getTimeMax() > 24000) {
            issues.add(ValidationIssue.error("time_max", MOD_ID + ".validation.time_range"));
        }
    }

    private void validateLevelRange(CobblemonTaskModel model, List<ValidationIssue> issues) {
        if (model.getMinLevel() < 0) {
            issues.add(ValidationIssue.error("min_level", MOD_ID + ".validation.level_positive"));
        }
        if (model.getMaxLevel() < 0) {
            issues.add(ValidationIssue.error("max_level", MOD_ID + ".validation.level_positive"));
        }
        if (model.getMaxLevel() > 0 && model.getMinLevel() > model.getMaxLevel()) {
            issues.add(ValidationIssue.error("min_level", MOD_ID + ".validation.level_min_max"));
        }
    }

    private void validateDexProgress(CobblemonTaskModel model, List<ValidationIssue> issues) {
        if (!VALID_DEX_PROGRESS.contains(model.getDexProgress())) {
            issues.add(ValidationIssue.error("dex_progress", MOD_ID + ".validation.dex_progress_invalid"));
        }
    }

    private void validateUnusedFields(CobblemonTaskModel model, List<ValidationIssue> issues) {
        List<String> actions = model.getActions();

        // Time/location fields only apply to catch/battle actions
        boolean hasCatchBattle = actions.stream().anyMatch(CATCH_BATTLE_ACTIONS::contains);
        if (!hasCatchBattle) {
            if (!model.getBiomes().isEmpty()) {
                issues.add(ValidationIssue.warning("biomes", MOD_ID + ".validation.biomes_unused"));
            }
            if (!model.getDimensions().isEmpty()) {
                issues.add(ValidationIssue.warning("dimensions", MOD_ID + ".validation.dimensions_unused"));
            }
            if (model.getTimeMin() != 0 || model.getTimeMax() != 24000) {
                issues.add(ValidationIssue.warning("time_min", MOD_ID + ".validation.time_unused"));
            }
        }

        // Level fields only apply to level_up actions
        boolean hasLevelAction = actions.stream().anyMatch(LEVEL_ACTIONS::contains);
        if (!hasLevelAction && (model.getMinLevel() != 0 || model.getMaxLevel() != 0)) {
            issues.add(ValidationIssue.warning("min_level", MOD_ID + ".validation.level_unused"));
        }

        // Dex progress only applies to dex actions
        boolean hasDexAction = actions.stream().anyMatch(DEX_ACTIONS::contains);
        if (!hasDexAction && !"seen".equals(model.getDexProgress())) {
            issues.add(ValidationIssue.warning("dex_progress", MOD_ID + ".validation.dex_progress_unused"));
        }

        // Gimmick fields
        if (!actions.contains("mega_evolve") && !model.getMegaForms().isEmpty()) {
            issues.add(ValidationIssue.warning("mega_forms", MOD_ID + ".validation.mega_forms_unused"));
        }
        if (!actions.contains("terastallize") && !model.getTeraTypes().isEmpty()) {
            issues.add(ValidationIssue.warning("tera_types", MOD_ID + ".validation.tera_types_unused"));
        }
        if (!actions.contains("use_z_move") && !model.getZCrystals().isEmpty()) {
            issues.add(ValidationIssue.warning("z_crystals", MOD_ID + ".validation.z_crystals_unused"));
        }
        boolean hasDynamax = actions.contains("dynamax") || actions.contains("gigantamax") || actions.contains("ultra_burst");
        if (!hasDynamax && !model.getDynamaxTypes().isEmpty()) {
            issues.add(ValidationIssue.warning("dynamax_types", MOD_ID + ".validation.dynamax_unused"));
        }
    }

    private void validatePotentialMisconfigurations(CobblemonTaskModel model, List<ValidationIssue> issues) {
        // Warn if amount is very high
        if (model.getAmount() > 1000) {
            issues.add(ValidationIssue.warning("amount", MOD_ID + ".validation.amount_high"));
        }

        // Warn if multiple conflicting filters that might make task impossible
        if (model.getPokemons().size() > 10 && !model.getPokemonTypes().isEmpty()) {
            issues.add(ValidationIssue.info("pokemons", MOD_ID + ".validation.many_filters"));
        }
    }
}
