package cobblemonquestsextended.cobblemon_quests_extended.domain.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Result of validating a CobblemonTaskModel.
 * Contains all validation issues organized by severity.
 */
public class ValidationResult {
    private final List<ValidationIssue> issues;

    private ValidationResult(List<ValidationIssue> issues) {
        this.issues = Collections.unmodifiableList(new ArrayList<>(issues));
    }

    public static ValidationResult of(List<ValidationIssue> issues) {
        return new ValidationResult(issues);
    }

    public static ValidationResult valid() {
        return new ValidationResult(Collections.emptyList());
    }

    public boolean isValid() {
        return getErrors().isEmpty();
    }

    public boolean hasWarnings() {
        return !getWarnings().isEmpty();
    }

    public boolean hasIssues() {
        return !issues.isEmpty();
    }

    public List<ValidationIssue> getAll() {
        return issues;
    }

    public List<ValidationIssue> getErrors() {
        return issues.stream()
            .filter(i -> i.severity() == ValidationSeverity.ERROR)
            .collect(Collectors.toList());
    }

    public List<ValidationIssue> getWarnings() {
        return issues.stream()
            .filter(i -> i.severity() == ValidationSeverity.WARNING)
            .collect(Collectors.toList());
    }

    public List<ValidationIssue> getInfos() {
        return issues.stream()
            .filter(i -> i.severity() == ValidationSeverity.INFO)
            .collect(Collectors.toList());
    }

    public List<ValidationIssue> getIssuesForField(String field) {
        return issues.stream()
            .filter(i -> field.equals(i.field()))
            .collect(Collectors.toList());
    }

    public int getErrorCount() {
        return getErrors().size();
    }

    public int getWarningCount() {
        return getWarnings().size();
    }
}
