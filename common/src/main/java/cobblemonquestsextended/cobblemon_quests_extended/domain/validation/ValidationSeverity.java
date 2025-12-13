package cobblemonquestsextended.cobblemon_quests_extended.domain.validation;

/**
 * Severity levels for validation issues.
 */
public enum ValidationSeverity {
    ERROR,   // Task cannot be saved
    WARNING, // Task can be saved but has potential issues
    INFO     // Informational suggestion
}
