package cobblemonquestsextended.cobblemon_quests_extended.registry;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import net.minecraft.resources.ResourceLocation;

/**
 * Categories for grouping biomes by their environmental characteristics.
 */
public enum BiomeCategory {
    /**
     * Forest biomes: forests, groves, woods
     */
    FOREST("forest", 0x228B22),

    /**
     * Plains biomes: plains, meadows, savannas
     */
    PLAINS("plains", 0x7CFC00),

    /**
     * Desert biomes: deserts, badlands
     */
    DESERT("desert", 0xF4A460),

    /**
     * Ocean biomes: oceans, beaches, rivers
     */
    OCEAN("ocean", 0x1E90FF),

    /**
     * Mountain biomes: mountains, peaks, hills
     */
    MOUNTAIN("mountain", 0x696969),

    /**
     * Cave biomes: caves, deep dark, dripstone
     */
    CAVE("cave", 0x4A4A4A),

    /**
     * Jungle biomes: jungles, bamboo forests
     */
    JUNGLE("jungle", 0x32CD32),

    /**
     * Swamp biomes: swamps, marshes, mangrove
     */
    SWAMP("swamp", 0x556B2F),

    /**
     * Tundra biomes: snowy, frozen, icy, taiga
     */
    TUNDRA("tundra", 0xB0E0E6),

    /**
     * Nether biomes: all nether variants
     */
    NETHER("nether", 0x8B0000),

    /**
     * End biomes: end, void
     */
    END("end", 0x4B0082),

    /**
     * Other biomes that don't fit standard categories
     */
    OTHER("other", 0x808080);

    private final String id;
    private final int color;

    BiomeCategory(String id, int color) {
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
        return "cobblemon_quests_extended.biome_category." + id;
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
     * Categorizes a biome based on its resource location.
     *
     * @param biome the biome resource location to categorize
     * @return the matching BiomeCategory
     */
    public static BiomeCategory categorize(ResourceLocation biome) {
        String path = biome.getPath().toLowerCase();

        if (path.contains("forest") || path.contains("grove") || path.contains("woods")) return FOREST;
        if (path.contains("plains") || path.contains("meadow") || path.contains("savanna")) return PLAINS;
        if (path.contains("desert") || path.contains("badlands")) return DESERT;
        if (path.contains("ocean") || path.contains("beach") || path.contains("river")) return OCEAN;
        if (path.contains("mountain") || path.contains("peak") || path.contains("hill")) return MOUNTAIN;
        if (path.contains("cave") || path.contains("deep_dark") || path.contains("dripstone")) return CAVE;
        if (path.contains("jungle") || path.contains("bamboo")) return JUNGLE;
        if (path.contains("swamp") || path.contains("marsh") || path.contains("mangrove")) return SWAMP;
        if (path.contains("snowy") || path.contains("frozen") || path.contains("ice") || path.contains("taiga")) return TUNDRA;
        if (biome.getNamespace().equals("minecraft") && path.startsWith("nether")) return NETHER;
        if (path.contains("nether") || path.contains("soul") || path.contains("basalt") || path.contains("crimson") || path.contains("warped")) return NETHER;
        if (path.contains("end") || path.contains("void")) return END;

        return OTHER;
    }
}
