package cobblemonquestsextended.cobblemon_quests_extended.registry;

import dev.ftb.mods.ftblibrary.icon.Color4I;

/**
 * Category enum for Pokemon forms/aspects.
 * Used for grouping forms in selector screens.
 */
public enum FormCategory {
    REGIONAL("regional", 0xF1C40F),
    MEGA("mega", 0xE91E63),
    DYNAMAX("dynamax", 0xE74C3C),
    LEGENDARY("legendary", 0x9B59B6),
    OTHER("other", 0x808080);

    private final String translationKey;
    private final int color;

    FormCategory(String name, int color) {
        this.translationKey = "cobblemon_quests_extended.form_category." + name;
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

    /**
     * Categorizes a form name into its category.
     *
     * @param form the form name
     * @return the category
     */
    public static FormCategory fromForm(String form) {
        if (form == null) return OTHER;
        String lower = form.toLowerCase();

        if (lower.contains("alolan") || lower.contains("galarian") ||
                lower.contains("hisuian") || lower.contains("paldean")) {
            return REGIONAL;
        }
        if (lower.contains("mega") || lower.contains("primal")) {
            return MEGA;
        }
        if (lower.contains("gmax") || lower.contains("gigantamax")) {
            return DYNAMAX;
        }
        if (lower.contains("origin") || lower.contains("altered") ||
                lower.contains("therian") || lower.contains("incarnate") ||
                lower.contains("crowned") || lower.contains("unbound")) {
            return LEGENDARY;
        }
        return OTHER;
    }
}
