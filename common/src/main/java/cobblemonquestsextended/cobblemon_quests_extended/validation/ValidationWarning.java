package cobblemonquestsextended.cobblemon_quests_extended.validation;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a validation warning that does not prevent saving but indicates
 * potential issues with the task configuration.
 *
 * <p>Warnings are informational and allow the user to save the task, but they
 * indicate configurations that may not work as expected.</p>
 *
 * <p>The {@code warningKey} should be a translation key following the pattern:
 * {@code cobblemon_quests_extended.validation.warning.<key>}</p>
 *
 * @param field the field name that has the validation warning
 * @param warningKey the translation key for the warning message
 * @param args optional arguments for message formatting
 */
public record ValidationWarning(String field, String warningKey, Object... args) {

    /**
     * Creates a new validation warning.
     *
     * @param field the field name that has the validation warning
     * @param warningKey the translation key for the warning message
     * @param args optional arguments for message formatting
     */
    public ValidationWarning {
        Objects.requireNonNull(field, "field cannot be null");
        Objects.requireNonNull(warningKey, "warningKey cannot be null");
        if (args == null) {
            args = new Object[0];
        }
    }

    /**
     * Returns the full translation key for this warning.
     *
     * @return the complete translation key
     */
    public String getTranslationKey() {
        return "cobblemon_quests_extended.validation.warning." + warningKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationWarning that = (ValidationWarning) o;
        return Objects.equals(field, that.field)
                && Objects.equals(warningKey, that.warningKey)
                && Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(field, warningKey);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }

    @Override
    public String toString() {
        return "ValidationWarning{" +
                "field='" + field + '\'' +
                ", warningKey='" + warningKey + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
