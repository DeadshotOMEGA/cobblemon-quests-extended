package cobblemonquestsextended.cobblemon_quests_extended.integrations.megashowdown;

import cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests;

/**
 * Integration layer for Mega Showdown mod.
 * Handles registration of event listeners when Mega Showdown is present.
 *
 * <p>This class uses a soft dependency pattern - it will only initialize
 * the actual event handlers if Mega Showdown is detected at runtime.</p>
 */
public final class MegaShowdownIntegration {

    private static boolean initialized = false;

    private MegaShowdownIntegration() {
        // Utility class
    }

    /**
     * Initializes the Mega Showdown integration if the mod is present.
     * Safe to call multiple times - will only initialize once.
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        if (!MegaShowdownDetector.isLoaded()) {
            CobblemonQuests.LOGGER.info("Skipping Mega Showdown integration - mod not present");
            return;
        }

        try {
            // Load the event handler class only when Mega Showdown is present
            // This prevents ClassNotFoundException when Mega Showdown is not installed
            Class<?> handlerClass = Class.forName(
                "cobblemonquestsextended.cobblemon_quests_extended.integrations.megashowdown.MegaShowdownEventHandler"
            );
            handlerClass.getMethod("register").invoke(null);
            CobblemonQuests.LOGGER.info("Mega Showdown integration initialized successfully");
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Failed to initialize Mega Showdown integration: " + e.getMessage());
        }
    }
}
