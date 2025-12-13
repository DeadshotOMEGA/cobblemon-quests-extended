package cobblemonquestsextended.cobblemon_quests_extended.domain.validation;

/**
 * Represents a single validation issue with field, message, and severity.
 */
public record ValidationIssue(
    String field,
    String messageKey,
    ValidationSeverity severity,
    Object... messageArgs
) {
    public ValidationIssue(String field, String messageKey, ValidationSeverity severity) {
        this(field, messageKey, severity, new Object[0]);
    }

    public static ValidationIssue error(String field, String messageKey, Object... args) {
        return new ValidationIssue(field, messageKey, ValidationSeverity.ERROR, args);
    }

    public static ValidationIssue warning(String field, String messageKey, Object... args) {
        return new ValidationIssue(field, messageKey, ValidationSeverity.WARNING, args);
    }

    public static ValidationIssue info(String field, String messageKey, Object... args) {
        return new ValidationIssue(field, messageKey, ValidationSeverity.INFO, args);
    }
}
