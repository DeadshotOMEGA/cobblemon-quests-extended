package cobblemonquestsextended.cobblemon_quests_extended.registry;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import java.util.Map;

/**
 * Categories for grouping Pokemon natures by their stat modifiers.
 */
public enum NatureCategory {
    /**
     * Neutral natures with no stat modifiers
     */
    NEUTRAL("neutral", 0x808080, null, null),

    /**
     * Natures that boost Attack stat
     */
    ATTACK_UP("attack_up", 0xFF4444, "attack", "special_attack"),

    /**
     * Natures that boost Defense stat
     */
    DEFENSE_UP("defense_up", 0x4444FF, "defense", "speed"),

    /**
     * Natures that boost Special Attack stat
     */
    SP_ATK_UP("sp_atk_up", 0xFF44FF, "special_attack", "attack"),

    /**
     * Natures that boost Special Defense stat
     */
    SP_DEF_UP("sp_def_up", 0x44FF44, "special_defense", "defense"),

    /**
     * Natures that boost Speed stat
     */
    SPEED_UP("speed_up", 0xFFFF44, "speed", "special_defense");

    private final String id;
    private final int color;
    private final String statBoost;
    private final String statPenalty;

    NatureCategory(String id, int color, String statBoost, String statPenalty) {
        this.id = id;
        this.color = color;
        this.statBoost = statBoost;
        this.statPenalty = statPenalty;
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
        return "cobblemon_quests_extended.nature_category." + id;
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
     * Gets the stat that is boosted by this nature category.
     *
     * @return the boosted stat name, or null if neutral
     */
    public String getStatBoost() {
        return statBoost;
    }

    /**
     * Gets the stat that is penalized by this nature category.
     *
     * @return the penalized stat name, or null if neutral
     */
    public String getStatPenalty() {
        return statPenalty;
    }

    // Map of all 25 natures to their category
    private static final Map<String, NatureCategory> NATURE_MAP = Map.ofEntries(
        // Neutral (5) - same stat boost/penalty
        Map.entry("hardy", NEUTRAL),
        Map.entry("docile", NEUTRAL),
        Map.entry("serious", NEUTRAL),
        Map.entry("bashful", NEUTRAL),
        Map.entry("quirky", NEUTRAL),

        // Attack up (4)
        Map.entry("lonely", ATTACK_UP),    // -Def
        Map.entry("brave", ATTACK_UP),     // -Speed
        Map.entry("adamant", ATTACK_UP),   // -SpAtk
        Map.entry("naughty", ATTACK_UP),   // -SpDef

        // Defense up (4)
        Map.entry("bold", DEFENSE_UP),     // -Atk
        Map.entry("relaxed", DEFENSE_UP),  // -Speed
        Map.entry("impish", DEFENSE_UP),   // -SpAtk
        Map.entry("lax", DEFENSE_UP),      // -SpDef

        // Special Attack up (4)
        Map.entry("modest", SP_ATK_UP),    // -Atk
        Map.entry("mild", SP_ATK_UP),      // -Def
        Map.entry("quiet", SP_ATK_UP),     // -Speed
        Map.entry("rash", SP_ATK_UP),      // -SpDef

        // Special Defense up (4)
        Map.entry("calm", SP_DEF_UP),      // -Atk
        Map.entry("gentle", SP_DEF_UP),    // -Def
        Map.entry("sassy", SP_DEF_UP),     // -Speed
        Map.entry("careful", SP_DEF_UP),   // -SpAtk

        // Speed up (4)
        Map.entry("timid", SPEED_UP),      // -Atk
        Map.entry("hasty", SPEED_UP),      // -Def
        Map.entry("jolly", SPEED_UP),      // -SpAtk
        Map.entry("naive", SPEED_UP)       // -SpDef
    );

    /**
     * Determines the category for a given Pokemon nature.
     *
     * @param nature the nature name to categorize
     * @return the matching NatureCategory, or NEUTRAL if not found
     */
    public static NatureCategory fromNature(String nature) {
        return NATURE_MAP.getOrDefault(nature.toLowerCase(), NEUTRAL);
    }
}
