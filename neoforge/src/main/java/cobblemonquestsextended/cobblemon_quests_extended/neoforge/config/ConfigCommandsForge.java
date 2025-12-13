package cobblemonquestsextended.cobblemon_quests_extended.neoforge.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import cobblemonquestsextended.cobblemon_quests_extended.commands.RegisterCommands;

public class ConfigCommandsForge {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        RegisterCommands.register(event.getDispatcher());
    }
}