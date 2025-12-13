package cobblemonquestsextended.cobblemon_quests_extended.validation;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a validation error that prevents saving a task configuration.
 * Errors indicate critical issues that must be resolved before the task can be saved.
 *
 * <p>The {@code errorKey} should be a translation key following the pattern:
 * {@code cobblemon_quests_extended.validation.error.<key>}</p>
 *
 * @param field the field name that has the validation error
 * @param errorKey the translation key for the error message
 * @param args optional arguments for message formatting
 */
public record ValidationError(String field, String errorKey, Object... args) {

    /**
     * Creates a new validation error.
     *
     * @param field the field name that has the validation error
     * @param errorKey the translation key for the error message
     * @param args optional arguments for message formatting
     */
    public ValidationError {
        Objects.requireNonNull(field, "field cannot be null");
        Objects.requireNonNull(errorKey, "errorKey cannot be null");
        if (args == null) {
            args = new Object[0];
        }
    }

    /**
     * Returns the full translation key for this error.
     *
     * @return the complete translation key
     */
    public String getTranslationKey() {
        return "cobblemon_quests_extended.validation.error." + errorKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationError that = (ValidationError) o;
        return Objects.equals(field, that.field)
                && Objects.equals(errorKey, that.errorKey)
                && Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(field, errorKey);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "field='" + field + '\'' +
                ", errorKey='" + errorKey + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
