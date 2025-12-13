package cobblemonquestsextended.cobblemon_quests_extended.registry;

/**
 * Categories for grouping quest actions by their type.
 */
public enum ActionCategory {
    /**
     * Actions related to obtaining Pokemon (catch, hatch, etc.)
     */
    CATCH("catch"),

    /**
     * Actions related to combat (defeat, faint, etc.)
     */
    BATTLE("battle"),

    /**
     * Actions related to evolution (evolve, change form, etc.)
     */
    EVOLUTION("evolution"),

    /**
     * Actions related to trading Pokemon
     */
    TRADE("trade"),

    /**
     * Actions related to Pokedex completion
     */
    POKEDEX("pokedex"),

    /**
     * Actions related to battle gimmicks (mega evolution, terastallization, z-moves, dynamax, etc.)
     */
    GIMMICK("gimmick"),

    /**
     * Miscellaneous actions that don't fit other categories
     */
    OTHER("other");

    private final String id;

    ActionCategory(String id) {
        this.id = id;
    }

    /**
     * Gets the string identifier for this category.
     *
     * @return the category ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the translation key for this category's display name.
     *
     * @return the translation key
     */
    public String getTranslationKey() {
        return "cobblemon_quests_extended.category." + id;
    }

    /**
     * Finds a category by its string ID.
     *
     * @param id the category ID to search for
     * @return the matching ActionCategory, or OTHER if not found
     */
    public static ActionCategory fromId(String id) {
        for (ActionCategory category : values()) {
            if (category.id.equals(id)) {
                return category;
            }
        }
        return OTHER;
    }
}
