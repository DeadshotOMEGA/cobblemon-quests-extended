package cobblemonquestsextended.cobblemon_quests_extended;

import cobblemonquestsextended.cobblemon_quests_extended.config.CobblemonQuestsConfig;
import cobblemonquestsextended.cobblemon_quests_extended.events.CobblemonQuestsEventHandler;
import cobblemonquestsextended.cobblemon_quests_extended.integrations.megashowdown.MegaShowdownIntegration;
import cobblemonquestsextended.cobblemon_quests_extended.logger.CobblemonQuestsLogger;
import cobblemonquestsextended.cobblemon_quests_extended.registry.ActionRegistry;
import cobblemonquestsextended.cobblemon_quests_extended.tasks.PokemonTaskTypes;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;

import java.nio.file.Path;

public class CobblemonQuests {
    public static final String MOD_ID = "cobblemon_quests_extended";
    public static final CobblemonQuestsLogger LOGGER = new CobblemonQuestsLogger();
    public static Path configPath;
    public static CobblemonQuestsEventHandler eventHandler;

    public static void init(Path configPath, boolean useConfig) {
        LOGGER.info("Initializing Cobblemon Quests Extended...");

        if (useConfig) {
            CobblemonQuests.configPath = configPath;
            CobblemonQuestsConfig.init();
        }

        // Initialize core components
        ActionRegistry.init();
        eventHandler = new CobblemonQuestsEventHandler().init();
        PokemonTaskTypes.init();

        // Initialize client-side components (GuiProvider for dynamic field visibility)
        if (Platform.getEnv() == EnvType.CLIENT) {
            PokemonTaskTypes.initClient();
        }

        // Initialize mod integrations (soft dependencies)
        MegaShowdownIntegration.init();

        LOGGER.info("Cobblemon Quests Extended initialized successfully!");
    }
}
