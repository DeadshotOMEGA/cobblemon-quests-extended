package cobblemonquestsextended.cobblemon_quests_extended.registry;

import dev.ftb.mods.ftblibrary.icon.Color4I;

/**
 * Simple single-value category enum for flat Pokemon list display.
 * Used when no grouping is desired - all Pokemon in one alphabetical list.
 */
public enum PokemonListCategory {
    ALL("all", 0x5DADE2);

    private final String translationKey;
    private final int color;

    PokemonListCategory(String name, int color) {
        this.translationKey = "cobblemon_quests_extended.pokemon_list." + name;
        this.color = color;
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
}
