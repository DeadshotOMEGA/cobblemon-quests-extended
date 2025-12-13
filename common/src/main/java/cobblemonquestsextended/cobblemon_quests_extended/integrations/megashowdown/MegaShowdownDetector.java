package cobblemonquestsextended.cobblemon_quests_extended.integrations.megashowdown;

import cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests;

/**
 * Detects whether the Mega Showdown mod is present at runtime.
 * Uses class detection to avoid hard dependency.
 */
public final class MegaShowdownDetector {

    private static Boolean cachedResult = null;
    private static final String MEGA_SHOWDOWN_MARKER_CLASS = "com.github.yajatkaul.mega_showdown.api.event.DynamaxStartCallback";

    private MegaShowdownDetector() {
        // Utility class
    }

    /**
     * Checks if Mega Showdown mod is present.
     * Result is cached after first check.
     *
     * @return true if Mega Showdown is loaded
     */
    public static boolean isLoaded() {
        if (cachedResult == null) {
            cachedResult = detectMegaShowdown();
        }
        return cachedResult;
    }

    private static boolean detectMegaShowdown() {
        try {
            Class.forName(MEGA_SHOWDOWN_MARKER_CLASS);
            CobblemonQuests.LOGGER.info("Mega Showdown detected - enabling dynamax/gigantamax/ultra_burst quest actions");
            return true;
        } catch (ClassNotFoundException e) {
            CobblemonQuests.LOGGER.info("Mega Showdown not found - dynamax/gigantamax/ultra_burst actions will not fire events");
            return false;
        }
    }

    /**
     * Clears the cached detection result.
     * Primarily for testing purposes.
     */
    public static void clearCache() {
        cachedResult = null;
    }
}
