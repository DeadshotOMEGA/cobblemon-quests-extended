package cobblemonquestsextended.cobblemon_quests_extended.fabric.config;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import cobblemonquestsextended.cobblemon_quests_extended.commands.RegisterCommands;

public class ConfigCommandsFabric {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                RegisterCommands.register(dispatcher));
    }
}

