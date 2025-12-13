package cobblemonquestsextended.cobblemon_quests_extended.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the result of validating a task configuration.
 *
 * <p>A validation result contains:</p>
 * <ul>
 *   <li>{@code isValid} - whether the task can be saved (no errors)</li>
 *   <li>{@code errors} - list of critical issues that prevent saving</li>
 *   <li>{@code warnings} - list of potential issues that allow saving</li>
 * </ul>
 *
 * <p>Use the static factory methods to create instances:</p>
 * <pre>{@code
 * ValidationResult.valid()                          // No errors or warnings
 * ValidationResult.withErrors(errorList)            // Has errors, invalid
 * ValidationResult.withWarnings(warningList)        // Has warnings, still valid
 * ValidationResult.of(errorList, warningList)       // Custom combination
 * }</pre>
 *
 * @param isValid whether the task configuration is valid (can be saved)
 * @param errors list of validation errors (immutable)
 * @param warnings list of validation warnings (immutable)
 */
public record ValidationResult(boolean isValid, List<ValidationError> errors, List<ValidationWarning> warnings) {

    /**
     * Creates a new validation result.
     *
     * @param isValid whether the task configuration is valid
     * @param errors list of validation errors
     * @param warnings list of validation warnings
     */
    public ValidationResult {
        errors = errors != null ? List.copyOf(errors) : List.of();
        warnings = warnings != null ? List.copyOf(warnings) : List.of();
    }

    /**
     * Creates a valid result with no errors or warnings.
     *
     * @return a valid ValidationResult
     */
    public static ValidationResult valid() {
        return new ValidationResult(true, List.of(), List.of());
    }

    /**
     * Creates an invalid result with the specified errors.
     *
     * @param errors the list of validation errors
     * @return an invalid ValidationResult containing the errors
     */
    public static ValidationResult withErrors(List<ValidationError> errors) {
        Objects.requireNonNull(errors, "errors cannot be null");
        return new ValidationResult(errors.isEmpty(), errors, List.of());
    }

    /**
     * Creates a valid result with the specified warnings.
     *
     * @param warnings the list of validation warnings
     * @return a valid ValidationResult containing the warnings
     */
    public static ValidationResult withWarnings(List<ValidationWarning> warnings) {
        Objects.requireNonNull(warnings, "warnings cannot be null");
        return new ValidationResult(true, List.of(), warnings);
    }

    /**
     * Creates a validation result with both errors and warnings.
     *
     * @param errors the list of validation errors
     * @param warnings the list of validation warnings
     * @return a ValidationResult (invalid if errors present, valid otherwise)
     */
    public static ValidationResult of(List<ValidationError> errors, List<ValidationWarning> warnings) {
        List<ValidationError> safeErrors = errors != null ? errors : List.of();
        List<ValidationWarning> safeWarnings = warnings != null ? warnings : List.of();
        return new ValidationResult(safeErrors.isEmpty(), safeErrors, safeWarnings);
    }

    /**
     * Returns whether there are any errors.
     *
     * @return true if there is at least one error
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Returns whether there are any warnings.
     *
     * @return true if there is at least one warning
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Merges this result with another, combining all errors and warnings.
     *
     * @param other the other validation result to merge
     * @return a new ValidationResult containing all errors and warnings from both
     */
    public ValidationResult merge(ValidationResult other) {
        if (other == null) {
            return this;
        }

        List<ValidationError> combinedErrors = new ArrayList<>(this.errors);
        combinedErrors.addAll(other.errors);

        List<ValidationWarning> combinedWarnings = new ArrayList<>(this.warnings);
        combinedWarnings.addAll(other.warnings);

        return ValidationResult.of(combinedErrors, combinedWarnings);
    }

    /**
     * Creates a builder for constructing validation results incrementally.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing ValidationResult instances incrementally.
     */
    public static final class Builder {
        private final List<ValidationError> errors = new ArrayList<>();
        private final List<ValidationWarning> warnings = new ArrayList<>();

        private Builder() {
        }

        /**
         * Adds an error to the result.
         *
         * @param error the validation error to add
         * @return this builder
         */
        public Builder addError(ValidationError error) {
            if (error != null) {
                errors.add(error);
            }
            return this;
        }

        /**
         * Adds an error to the result using field and key.
         *
         * @param field the field name
         * @param errorKey the error key
         * @param args optional formatting arguments
         * @return this builder
         */
        public Builder addError(String field, String errorKey, Object... args) {
            errors.add(new ValidationError(field, errorKey, args));
            return this;
        }

        /**
         * Adds a warning to the result.
         *
         * @param warning the validation warning to add
         * @return this builder
         */
        public Builder addWarning(ValidationWarning warning) {
            if (warning != null) {
                warnings.add(warning);
            }
            return this;
        }

        /**
         * Adds a warning to the result using field and key.
         *
         * @param field the field name
         * @param warningKey the warning key
         * @param args optional formatting arguments
         * @return this builder
         */
        public Builder addWarning(String field, String warningKey, Object... args) {
            warnings.add(new ValidationWarning(field, warningKey, args));
            return this;
        }

        /**
         * Adds all errors from a list.
         *
         * @param errors the errors to add
         * @return this builder
         */
        public Builder addErrors(List<ValidationError> errors) {
            if (errors != null) {
                this.errors.addAll(errors);
            }
            return this;
        }

        /**
         * Adds all warnings from a list.
         *
         * @param warnings the warnings to add
         * @return this builder
         */
        public Builder addWarnings(List<ValidationWarning> warnings) {
            if (warnings != null) {
                this.warnings.addAll(warnings);
            }
            return this;
        }

        /**
         * Builds the ValidationResult.
         *
         * @return a new ValidationResult instance
         */
        public ValidationResult build() {
            return ValidationResult.of(errors, warnings);
        }
    }
}
