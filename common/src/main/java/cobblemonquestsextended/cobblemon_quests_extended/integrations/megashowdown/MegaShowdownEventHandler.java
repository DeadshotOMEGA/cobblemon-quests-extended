package cobblemonquestsextended.cobblemon_quests_extended.integrations.megashowdown;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.github.yajatkaul.mega_showdown.api.event.DynamaxEndCallback;
import com.github.yajatkaul.mega_showdown.api.event.DynamaxStartCallback;
import com.github.yajatkaul.mega_showdown.api.event.UltraBurstCallback;
import cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;

/**
 * Event handler for Mega Showdown mod events.
 * This class directly imports Mega Showdown classes and should only be loaded
 * when Mega Showdown is confirmed to be present (via reflection from MegaShowdownIntegration).
 *
 * <p>Handles the following Mega Showdown events:</p>
 * <ul>
 *   <li>DynamaxStartCallback - for "dynamax" and "gigantamax" actions</li>
 *   <li>DynamaxEndCallback - (currently unused, reserved for future)</li>
 *   <li>UltraBurstCallback - for "ultra_burst" action</li>
 * </ul>
 */
public final class MegaShowdownEventHandler {

    private MegaShowdownEventHandler() {
        // Utility class
    }

    /**
     * Registers all Mega Showdown event listeners.
     * Called via reflection from MegaShowdownIntegration.
     */
    public static void register() {
        DynamaxStartCallback.EVENT.register(MegaShowdownEventHandler::onDynamaxStart);
        DynamaxEndCallback.EVENT.register(MegaShowdownEventHandler::onDynamaxEnd);
        UltraBurstCallback.EVENT.register(MegaShowdownEventHandler::onUltraBurst);
        CobblemonQuests.LOGGER.info("Registered Mega Showdown event handlers");
    }

    /**
     * Handles Dynamax/Gigantamax start events.
     *
     * @param battle the Pokemon battle
     * @param battlePokemon the Pokemon that dynamaxed
     * @param gmax whether this is a Gigantamax (true) or regular Dynamax (false)
     */
    private static void onDynamaxStart(PokemonBattle battle, BattlePokemon battlePokemon, Boolean gmax) {
        try {
            Pokemon pokemon = battlePokemon.getEffectedPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();

            if (player != null) {
                String action = gmax ? "gigantamax" : "dynamax";
                CobblemonQuests.eventHandler.processTasksForTeam(pokemon, action, 1, player);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing dynamax event: " + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Handles Dynamax end events.
     * Currently unused but available for future quest types.
     *
     * @param battle the Pokemon battle
     * @param battlePokemon the Pokemon that ended dynamax
     */
    private static void onDynamaxEnd(PokemonBattle battle, BattlePokemon battlePokemon) {
        // Reserved for future use - could track "maintain dynamax for X turns" type quests
    }

    /**
     * Handles Ultra Burst events (Necrozma Ultra form).
     *
     * @param battle the Pokemon battle
     * @param battlePokemon the Pokemon that used Ultra Burst
     */
    private static void onUltraBurst(PokemonBattle battle, BattlePokemon battlePokemon) {
        try {
            Pokemon pokemon = battlePokemon.getEffectedPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();

            if (player != null) {
                CobblemonQuests.eventHandler.processTasksForTeam(pokemon, "ultra_burst", 1, player);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing ultra burst event: " + Arrays.toString(e.getStackTrace()));
        }
    }
}
