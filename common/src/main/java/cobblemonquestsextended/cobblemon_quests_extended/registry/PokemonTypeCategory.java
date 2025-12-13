package cobblemonquestsextended.cobblemon_quests_extended.registry;

import dev.ftb.mods.ftblibrary.icon.Color4I;

/**
 * Categories for grouping Pokemon types by their combat style.
 */
public enum PokemonTypeCategory {
    /**
     * Physical attack types: normal, fighting, flying, poison, ground, rock, bug, ghost, steel
     */
    PHYSICAL("physical", 0xB8A038),

    /**
     * Special attack types: fire, water, grass, electric, psychic, ice, dragon, dark, fairy
     */
    SPECIAL("special", 0x6890F0);

    private final String id;
    private final int color;

    PokemonTypeCategory(String id, int color) {
        this.id = id;
        this.color = color;
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
        return "cobblemon_quests_extended.type_category." + id;
    }

    /**
     * Gets the color for this category.
     *
     * @return the RGB color
     */
    public Color4I getColor() {
        return Color4I.rgb(color);
    }

    /**
     * Determines the category for a given Pokemon type.
     *
     * @param type the type name to categorize
     * @return the matching PokemonTypeCategory
     */
    public static PokemonTypeCategory fromType(String type) {
        return switch (type.toLowerCase()) {
            case "fire", "water", "grass", "electric", "psychic",
                 "ice", "dragon", "dark", "fairy" -> SPECIAL;
            default -> PHYSICAL;
        };
    }
}
