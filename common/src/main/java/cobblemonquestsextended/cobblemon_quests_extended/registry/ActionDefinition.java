package cobblemonquestsextended.cobblemon_quests_extended.registry;

import java.util.Objects;

/**
 * Defines the metadata for a quest action type.
 * This record encapsulates all information needed to describe an action
 * that can be used in quest tasks.
 */
public record ActionDefinition(
        /**
         * The unique identifier for this action (e.g., "catch", "defeat").
         */
        String id,

        /**
         * The translation key used for localization of the action's display name.
         */
        String translationKey,

        /**
         * Whether this action requires a Pokemon parameter to be specified.
         * For example, "catch" requires a Pokemon, but "send_out" might not.
         */
        boolean requiresPokemon,

        /**
         * The category this action belongs to for organizational purposes.
         */
        ActionCategory category
) {
    /**
     * Creates an ActionDefinition with validation.
     *
     * @param id              the action identifier
     * @param translationKey  the translation key for localization
     * @param requiresPokemon whether a Pokemon parameter is required
     * @param category        the action category
     */
    public ActionDefinition {
        Objects.requireNonNull(id, "Action ID cannot be null");
        Objects.requireNonNull(translationKey, "Translation key cannot be null");
        Objects.requireNonNull(category, "Category cannot be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException("Action ID cannot be blank");
        }
    }

    /**
     * Creates an ActionDefinition with a default translation key based on the action ID.
     * The translation key format is: "cobblemon_quests.actions.{id}"
     *
     * @param id              the action identifier
     * @param requiresPokemon whether a Pokemon parameter is required
     * @param category        the action category
     * @return a new ActionDefinition instance
     */
    public static ActionDefinition of(String id, boolean requiresPokemon, ActionCategory category) {
        return new ActionDefinition(
                id,
                "cobblemon_quests.actions." + id,
                requiresPokemon,
                category
        );
    }

    /**
     * Creates an ActionDefinition with a custom translation key.
     *
     * @param id              the action identifier
     * @param translationKey  the custom translation key
     * @param requiresPokemon whether a Pokemon parameter is required
     * @param category        the action category
     * @return a new ActionDefinition instance
     */
    public static ActionDefinition withTranslationKey(String id, String translationKey, boolean requiresPokemon, ActionCategory category) {
        return new ActionDefinition(id, translationKey, requiresPokemon, category);
    }
}
