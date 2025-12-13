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
         * The translation key used for the action's description.
         * Provides detailed information about what the action does.
         */
        String descriptionKey,

        /**
         * The translation key used for usage examples.
         * Shows how to use this action in quest configuration.
         */
        String exampleKey,

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
     * @param descriptionKey  the translation key for description
     * @param exampleKey      the translation key for usage example
     * @param requiresPokemon whether a Pokemon parameter is required
     * @param category        the action category
     */
    public ActionDefinition {
        Objects.requireNonNull(id, "Action ID cannot be null");
        Objects.requireNonNull(translationKey, "Translation key cannot be null");
        Objects.requireNonNull(descriptionKey, "Description key cannot be null");
        Objects.requireNonNull(exampleKey, "Example key cannot be null");
        Objects.requireNonNull(category, "Category cannot be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException("Action ID cannot be blank");
        }
    }

    /**
     * Creates an ActionDefinition with default translation keys based on the action ID.
     * Translation key formats:
     * <ul>
     *   <li>Name: "cobblemon_quests.actions.{id}"</li>
     *   <li>Description: "cobblemon_quests.actions.{id}.desc"</li>
     *   <li>Example: "cobblemon_quests.actions.{id}.example"</li>
     * </ul>
     *
     * @param id              the action identifier
     * @param requiresPokemon whether a Pokemon parameter is required
     * @param category        the action category
     * @return a new ActionDefinition instance
     */
    public static ActionDefinition of(String id, boolean requiresPokemon, ActionCategory category) {
        String baseKey = "cobblemon_quests_extended.actions." + id;
        return new ActionDefinition(
                id,
                baseKey,
                baseKey + ".desc",
                baseKey + ".example",
                requiresPokemon,
                category
        );
    }

    /**
     * Creates an ActionDefinition with custom translation keys.
     * Description and example keys are derived from the provided translation key.
     *
     * @param id              the action identifier
     * @param translationKey  the custom translation key for the name
     * @param requiresPokemon whether a Pokemon parameter is required
     * @param category        the action category
     * @return a new ActionDefinition instance
     */
    public static ActionDefinition withTranslationKey(String id, String translationKey, boolean requiresPokemon, ActionCategory category) {
        return new ActionDefinition(
                id,
                translationKey,
                translationKey + ".desc",
                translationKey + ".example",
                requiresPokemon,
                category
        );
    }

    /**
     * Creates an ActionDefinition with fully custom translation keys.
     *
     * @param id              the action identifier
     * @param translationKey  the custom translation key for the name
     * @param descriptionKey  the custom translation key for the description
     * @param exampleKey      the custom translation key for the example
     * @param requiresPokemon whether a Pokemon parameter is required
     * @param category        the action category
     * @return a new ActionDefinition instance
     */
    public static ActionDefinition withCustomKeys(
            String id,
            String translationKey,
            String descriptionKey,
            String exampleKey,
            boolean requiresPokemon,
            ActionCategory category
    ) {
        return new ActionDefinition(id, translationKey, descriptionKey, exampleKey, requiresPokemon, category);
    }
}
