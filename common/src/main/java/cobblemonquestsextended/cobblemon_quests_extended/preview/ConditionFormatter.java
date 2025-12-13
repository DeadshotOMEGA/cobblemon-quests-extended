package cobblemonquestsextended.cobblemon_quests_extended.preview;

import java.util.List;

/**
 * Helper class for formatting individual conditions into human-readable text.
 * Provides utilities for list formatting, name transformation, and time formatting.
 */
public final class ConditionFormatter {

    private ConditionFormatter() {
        // Utility class - prevent instantiation
    }

    /**
     * Formats a list with "or" conjunction: "A, B, or C"
     * Single item returns just that item.
     * Two items: "A or B"
     * Three+ items: "A, B, or C"
     *
     * @param items List of items to format
     * @return Formatted string with proper conjunction
     */
    public static String formatOrList(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        if (items.size() == 2) {
            return items.get(0) + " or " + items.get(1);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            if (i == items.size() - 1) {
                sb.append("or ");
            }
            sb.append(items.get(i));
        }
        return sb.toString();
    }

    /**
     * Formats a list with "and" conjunction: "A, B, and C"
     * Single item returns just that item.
     * Two items: "A and B"
     * Three+ items: "A, B, and C"
     *
     * @param items List of items to format
     * @return Formatted string with proper conjunction
     */
    public static String formatAndList(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        if (items.size() == 2) {
            return items.get(0) + " and " + items.get(1);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            if (i == items.size() - 1) {
                sb.append("and ");
            }
            sb.append(items.get(i));
        }
        return sb.toString();
    }

    /**
     * Formats a pokemon species name from identifier to display name.
     * "cobblemon:pikachu" -> "Pikachu"
     * "pikachu" -> "Pikachu"
     *
     * @param species The species identifier (may include namespace)
     * @return Capitalized species name
     */
    public static String formatPokemonName(String species) {
        if (species == null || species.isEmpty()) {
            return "";
        }
        // Remove namespace if present (e.g., "cobblemon:pikachu" -> "pikachu")
        String name = species.contains(":") ? species.split(":")[1] : species;
        // Capitalize first letter
        return capitalize(name);
    }

    /**
     * Formats a type name for display.
     * "fire" -> "Fire"
     *
     * @param type The type identifier
     * @return Capitalized type name
     */
    public static String formatTypeName(String type) {
        if (type == null || type.isEmpty()) {
            return "";
        }
        return capitalize(type);
    }

    /**
     * Formats a region/generation identifier to display name.
     * "gen1" -> "Kanto"
     * "gen2" -> "Johto"
     *
     * @param region The region identifier
     * @return Human-readable region name
     */
    public static String formatRegion(String region) {
        if (region == null || region.isEmpty()) {
            return "";
        }
        return switch (region.toLowerCase()) {
            case "gen1" -> "Kanto";
            case "gen2" -> "Johto";
            case "gen3" -> "Hoenn";
            case "gen4" -> "Sinnoh";
            case "gen5" -> "Unova";
            case "gen6" -> "Kalos";
            case "gen7" -> "Alola";
            case "gen8" -> "Galar";
            case "gen9" -> "Paldea";
            default -> capitalize(region);
        };
    }

    /**
     * Formats a form name for display.
     * "alolan" -> "Alolan"
     * "mega-x" -> "Mega X"
     *
     * @param form The form identifier
     * @return Human-readable form name
     */
    public static String formatFormName(String form) {
        if (form == null || form.isEmpty()) {
            return "";
        }
        // Handle special form names
        return switch (form.toLowerCase()) {
            case "alolan" -> "Alolan";
            case "galarian" -> "Galarian";
            case "hisuian" -> "Hisuian";
            case "paldean" -> "Paldean";
            case "mega" -> "Mega";
            case "mega-x" -> "Mega X";
            case "mega-y" -> "Mega Y";
            case "primal" -> "Primal";
            case "normal" -> "Normal";
            default -> {
                // Handle hyphenated names like "magikarp-jump-apricot-stripes"
                String[] parts = form.split("-");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < parts.length; i++) {
                    if (i > 0) sb.append(" ");
                    sb.append(capitalize(parts[i]));
                }
                yield sb.toString();
            }
        };
    }

    /**
     * Formats a biome identifier for display.
     * "minecraft:plains" -> "Plains"
     * "minecraft:dark_forest" -> "Dark Forest"
     *
     * @param biome The biome identifier
     * @return Human-readable biome name
     */
    public static String formatBiome(String biome) {
        if (biome == null || biome.isEmpty()) {
            return "";
        }
        // Remove namespace
        String name = biome.contains(":") ? biome.split(":")[1] : biome;
        // Replace underscores with spaces and capitalize each word
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(capitalize(parts[i]));
        }
        return sb.toString();
    }

    /**
     * Formats a dimension identifier for display.
     * "minecraft:overworld" -> "Overworld"
     * "minecraft:the_nether" -> "the Nether"
     *
     * @param dimension The dimension identifier
     * @return Human-readable dimension name
     */
    public static String formatDimension(String dimension) {
        if (dimension == null || dimension.isEmpty()) {
            return "";
        }
        // Remove namespace
        String name = dimension.contains(":") ? dimension.split(":")[1] : dimension;

        // Special handling for common dimensions
        return switch (name.toLowerCase()) {
            case "overworld" -> "the Overworld";
            case "the_nether" -> "the Nether";
            case "the_end" -> "the End";
            default -> {
                // Generic formatting
                String[] parts = name.split("_");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < parts.length; i++) {
                    if (i > 0) sb.append(" ");
                    sb.append(capitalize(parts[i]));
                }
                yield sb.toString();
            }
        };
    }

    /**
     * Formats a Poke Ball name for display.
     * "cobblemon:poke_ball" -> "Poke Ball"
     * "cobblemon:premier_ball" -> "Premier Ball"
     *
     * @param pokeBall The Poke Ball identifier
     * @return Human-readable Poke Ball name
     */
    public static String formatPokeBall(String pokeBall) {
        if (pokeBall == null || pokeBall.isEmpty()) {
            return "";
        }
        // Remove namespace
        String name = pokeBall.contains(":") ? pokeBall.split(":")[1] : pokeBall;
        // Replace underscores with spaces and capitalize each word
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(capitalize(parts[i]));
        }
        return sb.toString();
    }

    /**
     * Formats time range into human-readable description.
     * 0-12000: "during daytime"
     * 12000-24000: "at night"
     * Custom range: "between time X and Y"
     *
     * @param min Minimum time (0-24000)
     * @param max Maximum time (0-24000)
     * @return Human-readable time description
     */
    public static String formatTimeRange(long min, long max) {
        // Full day - no time restriction
        if (min == 0 && max == 24000) {
            return "";
        }

        // Common time periods
        if (min == 0 && max == 12000) {
            return "during daytime";
        }
        if (min == 12000 && max == 24000) {
            return "at night";
        }
        if (min == 0 && max == 6000) {
            return "during early morning";
        }
        if (min == 6000 && max == 12000) {
            return "during late morning";
        }
        if (min == 12000 && max == 18000) {
            return "during evening";
        }
        if (min == 18000 && max == 24000) {
            return "during late night";
        }

        // Dusk and dawn
        if (min >= 11500 && max <= 12500) {
            return "at dusk";
        }
        if ((min >= 23000 || min == 0) && max <= 500) {
            return "at dawn";
        }

        // Custom range
        return String.format("between time %d and %d", min, max);
    }

    /**
     * Formats a level range into human-readable text.
     * min=50, max=50: "at level 50"
     * min=10, max=20: "between level 10 and 20"
     * min=0, max=50: "up to level 50"
     * min=50, max=0: "at level 50 or higher"
     *
     * @param minLevel Minimum level
     * @param maxLevel Maximum level (0 = no limit)
     * @return Human-readable level description
     */
    public static String formatLevelRange(int minLevel, int maxLevel) {
        // No level restriction
        if (minLevel == 0 && maxLevel == 0) {
            return "";
        }

        // Exact level
        if (minLevel == maxLevel && maxLevel != 0) {
            return "at level " + minLevel;
        }

        // Only max specified
        if (minLevel == 0 && maxLevel != 0) {
            return "up to level " + maxLevel;
        }

        // Only min specified (no upper bound)
        if (minLevel != 0 && maxLevel == 0) {
            return "at level " + minLevel + " or higher";
        }

        // Range
        return "between level " + minLevel + " and " + maxLevel;
    }

    /**
     * Formats a gender for display.
     * "male" -> "Male"
     * "genderless" -> "Genderless"
     *
     * @param gender The gender identifier
     * @return Human-readable gender name
     */
    public static String formatGender(String gender) {
        if (gender == null || gender.isEmpty()) {
            return "";
        }
        return capitalize(gender);
    }

    /**
     * Formats a nature for display.
     * Most natures are already title-cased, but ensure consistency.
     *
     * @param nature The nature name
     * @return Human-readable nature name
     */
    public static String formatNature(String nature) {
        if (nature == null || nature.isEmpty()) {
            return "";
        }
        return capitalize(nature);
    }

    /**
     * Formats dex progress for display.
     * "seen" -> "seen"
     * "caught" -> "caught"
     *
     * @param progress The dex progress value
     * @return Human-readable progress description
     */
    public static String formatDexProgress(String progress) {
        if (progress == null || progress.isEmpty()) {
            return "seen";
        }
        return progress.toLowerCase();
    }

    /**
     * Formats a mega form for display.
     * "mega" -> "Mega"
     * "mega-x" -> "Mega X"
     *
     * @param megaForm The mega form identifier
     * @return Human-readable mega form name
     */
    public static String formatMegaForm(String megaForm) {
        if (megaForm == null || megaForm.isEmpty()) {
            return "";
        }
        return switch (megaForm.toLowerCase()) {
            case "mega" -> "Mega";
            case "mega-x" -> "Mega X";
            case "mega-y" -> "Mega Y";
            case "primal" -> "Primal";
            default -> capitalize(megaForm);
        };
    }

    /**
     * Formats a Z-Crystal for display.
     * "firium-z" -> "Firium Z"
     *
     * @param zCrystal The Z-Crystal identifier
     * @return Human-readable Z-Crystal name
     */
    public static String formatZCrystal(String zCrystal) {
        if (zCrystal == null || zCrystal.isEmpty()) {
            return "";
        }
        // Handle the "-z" suffix
        if (zCrystal.toLowerCase().endsWith("-z")) {
            String base = zCrystal.substring(0, zCrystal.length() - 2);
            return capitalize(base) + " Z";
        }
        return capitalize(zCrystal);
    }

    /**
     * Formats a dynamax type for display.
     * "dynamax" -> "Dynamax"
     * "gigantamax" -> "Gigantamax"
     *
     * @param dynamaxType The dynamax type identifier
     * @return Human-readable dynamax type name
     */
    public static String formatDynamaxType(String dynamaxType) {
        if (dynamaxType == null || dynamaxType.isEmpty()) {
            return "";
        }
        return capitalize(dynamaxType);
    }

    /**
     * Capitalizes the first letter of a string.
     *
     * @param str The string to capitalize
     * @return The string with first letter capitalized
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
