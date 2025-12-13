package cobblemonquestsextended.cobblemon_quests_extended.events;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.FormeChangeEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.MegaEvolutionEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.TerastallizationEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.ZMoveUsedEvent;
import com.cobblemon.mod.common.api.events.fishing.BobberSpawnPokemonEvent;
import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent;
import com.cobblemon.mod.common.api.events.pokedex.scanning.PokemonScannedEvent;
import com.cobblemon.mod.common.api.events.pokemon.*;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionAcceptedEvent;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent;
import com.cobblemon.mod.common.api.events.starter.StarterChosenEvent;
import com.cobblemon.mod.common.api.events.storage.ReleasePokemonEvent;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.PokedexManager;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.ftb.mods.ftbquests.api.QuestFile;
import dev.ftb.mods.ftbquests.events.ClearFileCacheEvent;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.data.TeamManagerImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests;
import cobblemonquestsextended.cobblemon_quests_extended.tasks.CobblemonTask;

import java.util.*;

public class CobblemonQuestsEventHandler {
    private HashSet<CobblemonTask> pokemonTasks = null;
    private UUID lastPokemonUuid = null;

    public CobblemonQuestsEventHandler init() {
        EntityEvent.LIVING_DEATH.register(this::entityKill);
        ClearFileCacheEvent.EVENT.register(this::fileCacheClear);
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, this::pokemonCatchEvent);
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.LOWEST, this::pokemonBattleVictory);
        CobblemonEvents.STARTER_CHOSEN.subscribe(Priority.LOWEST, this::pokemonStarterChosen);
        CobblemonEvents.EVOLUTION_COMPLETE.subscribe(Priority.LOWEST, this::pokemonEvolutionComplete);
        CobblemonEvents.LEVEL_UP_EVENT.subscribe(Priority.LOWEST, this::pokemonLevelUp);
        CobblemonEvents.EVOLUTION_ACCEPTED.subscribe(Priority.LOWEST, this::pokemonEvolutionAccepted);
        CobblemonEvents.TRADE_EVENT_POST.subscribe(Priority.LOWEST, this::pokemonTrade);
        CobblemonEvents.POKEMON_RELEASED_EVENT_PRE.subscribe(Priority.LOWEST, this::pokemonRelease);
        CobblemonEvents.FOSSIL_REVIVED.subscribe(Priority.LOWEST, this::fossilRevived);
        CobblemonEvents.BOBBER_SPAWN_POKEMON_POST.subscribe(Priority.LOWEST, this::pokemonBobberSpawn);
        CobblemonEvents.POKEMON_SCANNED.subscribe(Priority.LOWEST, this::pokemonScan);
        CobblemonEvents.POKEDEX_DATA_CHANGED_POST.subscribe(Priority.LOWEST, this::pokeDexChangedPost);
        CobblemonEvents.POKEDEX_DATA_CHANGED_PRE.subscribe(Priority.LOWEST, this::pokeDexChanged);
        CobblemonEvents.THROWN_POKEBALL_HIT.subscribe(Priority.LOWEST, this::pokeballHit);
        PlayerEvent.PLAYER_JOIN.register((this::playerJoin));
        // New battle instruction events (Cobblemon 1.7.0+)
        CobblemonEvents.MEGA_EVOLUTION.subscribe(Priority.LOWEST, this::megaEvolution);
        CobblemonEvents.TERASTALLIZATION.subscribe(Priority.LOWEST, this::terastallization);
        CobblemonEvents.ZPOWER_USED.subscribe(Priority.LOWEST, this::zMoveUsed);
        CobblemonEvents.FORME_CHANGE.subscribe(Priority.LOWEST, this::formeChange);
        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.LOWEST, this::battleFainted);
        CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.LOWEST, this::pokemonSentPost);
        CobblemonEvents.HATCH_EGG_POST.subscribe(Priority.LOWEST, this::hatchEggPost);
        return this;
    }

    private void triggerPokeDexUpdate(UUID playerUUID) {
        try {
            TeamData teamData = getTeamData(playerUUID);
            if (teamData == null) return;
            PokedexManager pokedexManager = Cobblemon.playerDataManager.getPokedexData(playerUUID);
            for (CobblemonTask task : pokemonTasks) {
                task.increaseHaveRegistered(teamData, pokedexManager);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error adding caught pokemon to the dex " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void playerJoin(ServerPlayer player) {
        triggerPokeDexUpdate(player.getUUID());
    }

    private void pokeDexChanged(PokedexDataChangedEvent pre) {
        // 0: encountered
        // 1: caught after encounter
        // 2: caught without encounter
        try {
            Pokemon pokemon = pre.getDataSource().getPokemon();
            PokedexEntryProgress before = pre.getPokedexManager().getKnowledgeForSpecies(pokemon.getSpecies().getResourceIdentifier());
            PokedexEntryProgress after = pre.getKnowledge();
            int value = after.equals(PokedexEntryProgress.CAUGHT)
                    ? (before.equals(PokedexEntryProgress.ENCOUNTERED) ? 1 : 2)
                    : after.equals(PokedexEntryProgress.ENCOUNTERED) ? 0 : -1;
            if (value == -1)
                throw new Exception("Invalid pokedex change.", new Throwable("Before: " + before + " After: " + after));
            LivingEntity owner = pre.getDataSource().getPokemon().getOwnerEntity();
            PokemonEntity pokemonEntity = pokemon.getEntity();
            Level world = Optional.ofNullable(owner)
                    .map(LivingEntity::level)
                    .orElseGet(() -> Optional.ofNullable(pokemonEntity)
                            .map(PokemonEntity::level)
                            .orElse(null));

            if (world == null) {
                Throwable cause = new Throwable("Owner uuid " + pre.getPlayerUUID() + "Owner: " + owner + " PokemonEntity: " + pokemonEntity);
                throw new NoSuchElementException("World is null while processing pokedex.", cause);
            }

            Player player = world.getPlayerByUUID(pre.getPlayerUUID());
            if (player instanceof ServerPlayer serverPlayer) {
                processTasksForTeam(pokemon, "register", value, serverPlayer);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing pokedex changed event " + e.getCause() + " " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void pokeDexChangedPost(PokedexDataChangedEvent.Post post) {
        triggerPokeDexUpdate(post.getPlayerUUID());
    }

    private void pokemonScan(PokemonScannedEvent pokemonScannedEvent) {
        try {
            if (!(pokemonScannedEvent.getScannedEntity().resolveEntityScan() instanceof PokemonEntity)) return;
            Pokemon pokemon = ((PokemonEntity) pokemonScannedEvent.getScannedEntity()).getPokemon();
            if (lastPokemonUuid == pokemon.getUuid()) return;
            lastPokemonUuid = pokemon.getUuid();
            ServerPlayer player = pokemonScannedEvent.getPlayer();
            processTasksForTeam(pokemon, "scan", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing scan event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void pokemonBobberSpawn(BobberSpawnPokemonEvent.Post post) {
        try {
            Pokemon pokemon = post.getPokemon().getPokemon();
            ServerPlayer player = (ServerPlayer) post.component1().getPlayerOwner();
            processTasksForTeam(pokemon, "reel", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing bobber spawn event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void fossilRevived(FossilRevivedEvent fossilRevivedEvent) {
        try {
            ServerPlayer player = fossilRevivedEvent.getPlayer();
            Pokemon pokemon = fossilRevivedEvent.getPokemon();
            processTasksForTeam(pokemon, "revive_fossil", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing fossil revive event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void fileCacheClear(QuestFile file) {
        if (file.isServerSide()) {
            pokemonTasks = null;
        }
    }

    private void pokemonRelease(ReleasePokemonEvent.Pre pre) {
        try {
            ServerPlayer player = pre.getPlayer();
            Pokemon pokemon = pre.getPokemon();
            processTasksForTeam(pokemon, "release", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing release event " + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Player 1 gives pokemon 1 to player 2
     * Player 2 gives pokemon 2 to player 1
     */
    private void pokemonTrade(TradeEvent tradeEvent) {
        try {
            Pokemon pokemonGivenByPlayer1 = tradeEvent.getTradeParticipant2Pokemon();
            Pokemon pokemonGivenByPlayer2 = tradeEvent.getTradeParticipant1Pokemon();
            ServerPlayer player1 = pokemonGivenByPlayer2.getOwnerPlayer();
            ServerPlayer player2 = pokemonGivenByPlayer1.getOwnerPlayer();
            processTasksForTeam(pokemonGivenByPlayer2, "trade_for", 1, player1);
            processTasksForTeam(pokemonGivenByPlayer1, "trade_away", 1, player1);
            processTasksForTeam(pokemonGivenByPlayer1, "trade_for", 1, player2);
            processTasksForTeam(pokemonGivenByPlayer2, "trade_away", 1, player2);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing trade event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void pokemonBattleVictory(BattleVictoryEvent battleVictoryEvent) {
        try {
            List<ServerPlayer> players = battleVictoryEvent.getBattle().getPlayers();
            if (players.isEmpty()) return; // Not sure why no players would be in the battle, but better safe than sorry
            if (players.size() == 2) {
                ServerPlayer player1 = players.get(0);
                ServerPlayer player2 = players.get(1);
                if (player1.getUUID().equals(battleVictoryEvent.getWinners().getFirst().getUuid())) {
                    processTasksForTeam(player2.getName().getString(), "defeat_player", 1, player1);
                } else {
                    processTasksForTeam(player1.getName().getString(), "defeat_player", 1, player2);
                }
            }
            ServerPlayer player = players.getFirst();
            if (!player.getUUID().equals(battleVictoryEvent.getWinners().getFirst().getUuid())) return;
            for (BattleActor actor : battleVictoryEvent.getBattle().getActors()) {
                if (actor.getType() == ActorType.NPC) {
                    processTasksForTeam(actor.getName().getString(), "defeat_npc", 1, player);
                    break;
                }
                if (actor.getType() == ActorType.WILD) {
                    // Checks if the Pokémon is the last Pokémon that was caught. Done to bypass an issue with two events being
                    // fired for the same Pokémon and adding progress to catch and defeat tasks.
                    if (actor.getPokemonList().getFirst().getEffectedPokemon().getUuid() == lastPokemonUuid) return;
                    processTasksForTeam(actor.getPokemonList().getFirst().getEffectedPokemon(), "defeat", 1, player);
                    break;
                }
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing battle victory event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void pokemonCatchEvent(PokemonCapturedEvent pokemonCapturedEvent) {
        lastPokemonUuid = pokemonCapturedEvent.getPokemon().getUuid();
        pokemonCatch(pokemonCapturedEvent.getPokemon(), pokemonCapturedEvent.getPlayer());
    }

    private void pokeballHit(ThrownPokeballHitEvent thrownPokeballHitEvent) {
        try {
            Entity ballOwner = thrownPokeballHitEvent.getPokeBall().getOwner();
            if (ballOwner instanceof ServerPlayer player) {
                Pokemon pokemon = thrownPokeballHitEvent.getPokemon().getPokemon();
                pokemon.setCaughtBall(thrownPokeballHitEvent.getPokeBall().getPokeBall());
                processTasksForTeam(pokemon, "throw_ball", 1, player);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing pokéball hit event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private EventResult entityKill(Entity livingEntity, DamageSource damageSource) {
        try {
            if (damageSource.getEntity() instanceof ServerPlayer player && !PlayerHooks.isFake(player)) {
                Pokemon pokemon = livingEntity instanceof PokemonEntity ? ((PokemonEntity) livingEntity).getPokemon() : null;
                if (pokemon == null) return EventResult.pass();
                processTasksForTeam(pokemon, "kill", 1, player);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing entity kill event " + Arrays.toString(e.getStackTrace()));
        }
        return EventResult.pass();
    }

    public void pokemonCatch(Pokemon pokemon, ServerPlayer player) {
        try {
            processTasksForTeam(pokemon, "catch", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing catch event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void pokemonStarterChosen(StarterChosenEvent starterChosenEvent) {
        try {
            ServerPlayer player = starterChosenEvent.getPlayer();
            Pokemon pokemon = starterChosenEvent.getPokemon();
            processTasksForTeam(pokemon, "select_starter", 1, player);
            pokemonCatch(pokemon, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing starter chosen event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void pokemonEvolutionComplete(EvolutionCompleteEvent evolutionCompleteEvent) {
        try {
            Pokemon pokemon = evolutionCompleteEvent.getPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            processTasksForTeam(pokemon, "evolve_into", 1, player);
            pokemonCatch(pokemon, pokemon.getOwnerPlayer());
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing evolution complete event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void pokemonEvolutionAccepted(EvolutionAcceptedEvent evolutionAcceptedEvent) {
        try {
            Pokemon pokemon = evolutionAcceptedEvent.getPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            processTasksForTeam(pokemon, "evolve", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing evolution event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void pokemonLevelUp(LevelUpEvent levelUpEvent) {
        try {
            ServerPlayer player = levelUpEvent.getPokemon().getOwnerPlayer();
            Pokemon pokemon = levelUpEvent.getPokemon();
            long deltaLevel = levelUpEvent.getNewLevel() - levelUpEvent.getOldLevel();
            System.out.println("Old level: " + levelUpEvent.getOldLevel() + " New level: " + levelUpEvent.getNewLevel() + " Delta level: " + deltaLevel);
            processTasksForTeam(pokemon, "level_up_to", levelUpEvent.getNewLevel(), player);
            processTasksForTeam(pokemon, "level_up", deltaLevel, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing level up event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void megaEvolution(MegaEvolutionEvent event) {
        try {
            Pokemon pokemon = event.getPokemon().getEffectedPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            if (player != null) {
                processTasksForTeam(pokemon, "mega_evolve", 1, player);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing mega evolution event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void terastallization(TerastallizationEvent event) {
        try {
            Pokemon pokemon = event.getPokemon().getEffectedPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            if (player != null) {
                processTasksForTeam(pokemon, "terastallize", 1, player);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing terastallization event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void zMoveUsed(ZMoveUsedEvent event) {
        try {
            Pokemon pokemon = event.getPokemon().getEffectedPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            if (player != null) {
                processTasksForTeam(pokemon, "use_z_move", 1, player);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing Z-move event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void formeChange(FormeChangeEvent event) {
        try {
            Pokemon pokemon = event.getPokemon().getEffectedPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            if (player != null) {
                processTasksForTeam(pokemon, "change_form", 1, player);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing forme change event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void battleFainted(BattleFaintedEvent event) {
        try {
            // Get the Pokemon that caused the faint from the context
            BattlePokemon origin = event.getContext().getOrigin();
            if (origin != null) {
                Pokemon originPokemon = origin.getEffectedPokemon();
                ServerPlayer player = originPokemon.getOwnerPlayer();
                if (player != null) {
                    // The fainted Pokemon (the one that was defeated)
                    Pokemon faintedPokemon = event.getKilled().getEffectedPokemon();
                    processTasksForTeam(faintedPokemon, "faint_pokemon", 1, player);
                }
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing battle faint event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void pokemonSentPost(PokemonSentEvent.Post event) {
        try {
            Pokemon pokemon = event.getPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            if (player != null) {
                processTasksForTeam(pokemon, "send_out", 1, player);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing pokemon sent event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void hatchEggPost(HatchEggEvent.Post event) {
        try {
            ServerPlayer player = event.getPlayer();
            Pokemon pokemon = event.getPokemon();
            processTasksForTeam(pokemon, "hatch_egg", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing egg hatch event " + Arrays.toString(e.getStackTrace()));
        }
    }

    public void processTasksForTeam(Pokemon pokemon, String action, long amount, ServerPlayer player) {
        try {
            TeamData teamData = getTeamData(player);
            if (teamData == null) return;
            for (CobblemonTask task : pokemonTasks) {
                if (teamData.getProgress(task) < task.getMaxProgress() && teamData.canStartTasks(task.getQuest())) {
                    task.increase(teamData, pokemon, action, amount, player);
                }
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("(1) Error processing task for team " + Arrays.toString(e.getStackTrace()));
        }
    }

    public void processTasksForTeam(String data, String action, long amount, ServerPlayer player) {
        try {
            TeamData teamData = getTeamData(player);
            if (teamData == null) return;
            for (CobblemonTask task : pokemonTasks) {
                if (teamData.getProgress(task) < task.getMaxProgress() && teamData.canStartTasks(task.getQuest())) {
                    task.increaseWoPokemon(teamData, data, action, amount);
                }
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("(2) Error processing task for team " + Arrays.toString(e.getStackTrace()));
        }
    }

    private TeamData getTeamData(ServerPlayer player) {
        if (this.pokemonTasks == null) {
            this.pokemonTasks = new HashSet<>(ServerQuestFile.INSTANCE.collect(CobblemonTask.class));
        }
        if (this.pokemonTasks.isEmpty()) return null;
        Team team = TeamManagerImpl.INSTANCE.getTeamForPlayer(player).orElse(null);
        if (team == null) return null;
        return ServerQuestFile.INSTANCE.getOrCreateTeamData(team);
    }

    private TeamData getTeamData(UUID uuid) {
        if (this.pokemonTasks == null) {
            this.pokemonTasks = new HashSet<>(ServerQuestFile.INSTANCE.collect(CobblemonTask.class));
        }
        if (uuid == null) return null;
        if (this.pokemonTasks.isEmpty()) return null;
        Team team = TeamManagerImpl.INSTANCE.getTeamByID(uuid).orElse(null);
        if (team == null) return null;
        return ServerQuestFile.INSTANCE.getOrCreateTeamData(team);
    }
}