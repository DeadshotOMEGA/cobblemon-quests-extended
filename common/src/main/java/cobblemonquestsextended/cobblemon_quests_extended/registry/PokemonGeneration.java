package cobblemonquestsextended.cobblemon_quests_extended.registry;

import dev.ftb.mods.ftblibrary.icon.Color4I;

/**
 * Pokemon generation enum for grouping Pokemon by their origin generation.
 * Used for categorizing Pokemon in the selector screen.
 */
public enum PokemonGeneration {
    GEN_1("gen_1", 0xE74C3C, 1, 151, "Kanto"),
    GEN_2("gen_2", 0xF1C40F, 152, 251, "Johto"),
    GEN_3("gen_3", 0x27AE60, 252, 386, "Hoenn"),
    GEN_4("gen_4", 0x3498DB, 387, 493, "Sinnoh"),
    GEN_5("gen_5", 0x2C3E50, 494, 649, "Unova"),
    GEN_6("gen_6", 0x9B59B6, 650, 721, "Kalos"),
    GEN_7("gen_7", 0xF39C12, 722, 809, "Alola"),
    GEN_8("gen_8", 0xE91E63, 810, 905, "Galar"),
    GEN_9("gen_9", 0x1ABC9C, 906, 1025, "Paldea");

    private final String translationKey;
    private final int color;
    private final int minDex;
    private final int maxDex;
    private final String regionName;

    PokemonGeneration(String name, int color, int minDex, int maxDex, String regionName) {
        this.translationKey = "cobblemon_quests_extended.generation." + name;
        this.color = color;
        this.minDex = minDex;
        this.maxDex = maxDex;
        this.regionName = regionName;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public int getColor() {
        return color;
    }

    public Color4I getColor4I() {
        return Color4I.rgb(color);
    }

    public int getMinDex() {
        return minDex;
    }

    public int getMaxDex() {
        return maxDex;
    }

    public String getRegionName() {
        return regionName;
    }

    public int getGenerationNumber() {
        return ordinal() + 1;
    }

    /**
     * Gets the generation for a given national dex number.
     *
     * @param nationalDex the national Pokedex number
     * @return the generation, or null if out of range
     */
    public static PokemonGeneration fromDexNumber(int nationalDex) {
        for (PokemonGeneration gen : values()) {
            if (nationalDex >= gen.minDex && nationalDex <= gen.maxDex) {
                return gen;
            }
        }
        return null;
    }
}
