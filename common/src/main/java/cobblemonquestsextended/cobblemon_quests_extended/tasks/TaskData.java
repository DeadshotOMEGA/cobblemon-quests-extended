package cobblemonquestsextended.cobblemon_quests_extended.tasks;

import java.util.Arrays;
import java.util.List;

public class TaskData {
    public static final List<String> formList = List.of("normal",
            "alolan",
            "galarian",
            "paldean",
            "hisuian",
            "magikarp-jump-apricot-stripes",
            "magikarp-jump-apricot-tiger",
            "magikarp-jump-apricot-zebra",
            "magikarp-jump-black-forehead",
            "magikarp-jump-black-mask",
            "magikarp-jump-blue-raindrops",
            "magikarp-jump-blue-saucy",
            "magikarp-jump-brown-stripes",
            "magikarp-jump-brown-tiger",
            "magikarp-jump-brown-zebra",
            "magikarp-jump-calico-orange-gold",
            "magikarp-jump-calico-orange-white",
            "magikarp-jump-calico-orange-white-black",
            "magikarp-jump-calico-white-orange",
            "magikarp-jump-gray-bubbles",
            "magikarp-jump-gray-diamonds",
            "magikarp-jump-gray-patches",
            "magikarp-jump-orange-dapples",
            "magikarp-jump-orange-forehead",
            "magikarp-jump-orange-mask",
            "magikarp-jump-orange-orca",
            "magikarp-jump-orange-two-tone",
            "magikarp-jump-pink-dapples",
            "magikarp-jump-pink-orca",
            "magikarp-jump-pink-two-tone",
            "magikarp-jump-purple-bubbles",
            "magikarp-jump-purple-diamonds",
            "magikarp-jump-purple-patches",
            "magikarp-jump-skelly",
            "magikarp-jump-violet-raindrops",
            "magikarp-jump-violet-saucy",
            ""
    );
    // Original actions + new extended actions
    public static final List<String> actionList = Arrays.asList(
            // Original actions
            "catch", "defeat", "defeat_player", "defeat_npc", "evolve", "evolve_into",
            "kill", "level_up", "level_up_to", "release", "throw_ball", "trade_away",
            "trade_for", "obtain", "select_starter", "revive_fossil", "scan", "reel",
            "have_registered",
            // New Cobblemon 1.7.0+ actions
            "mega_evolve", "terastallize", "use_z_move", "change_form", "faint_pokemon",
            "send_out", "give_held_item", "heal", "hatch_egg",
            // Mega Showdown integration actions (soft dependency)
            "dynamax", "gigantamax", "ultra_burst",
            ""
    );

    public static final List<String> genderList = Arrays.asList("male", "female", "genderless");
    public static final List<String> pokemonTypeList = Arrays.asList("normal", "fire", "water", "grass", "electric", "ice", "fighting", "poison", "ground", "flying", "psychic", "bug", "rock", "ghost", "dragon", "dark", "steel", "fairy", "");
    public static final List<String> regionList = Arrays.asList("gen1", "gen2", "gen3", "gen4", "gen5", "gen6", "gen7", "gen8", "gen9");

    // New condition lists for extended features
    public static final List<String> teraTypeList = Arrays.asList(
            "normal", "fire", "water", "grass", "electric", "ice", "fighting",
            "poison", "ground", "flying", "psychic", "bug", "rock", "ghost",
            "dragon", "dark", "steel", "fairy", "stellar", ""
    );

    public static final List<String> megaFormList = Arrays.asList(
            "mega", "mega-x", "mega-y", "primal", ""
    );

    public static final List<String> zCrystalList = Arrays.asList(
            "normalium-z", "firium-z", "waterium-z", "grassium-z", "electrium-z",
            "icium-z", "fightinium-z", "poisonium-z", "groundium-z", "flyinium-z",
            "psychium-z", "buginium-z", "rockium-z", "ghostium-z", "dragonium-z",
            "darkinium-z", "steelium-z", "fairium-z",
            // Pokemon-specific Z-Crystals
            "aloraichium-z", "decidium-z", "eevium-z", "incinium-z", "kommonium-z",
            "lunalium-z", "lycanium-z", "marshadium-z", "mewnium-z", "mimikium-z",
            "pikanium-z", "pikashunium-z", "primarium-z", "snorlium-z", "solganium-z",
            "tapunium-z", "ultranecrozium-z", ""
    );

    public static final List<String> dynamaxTypeList = Arrays.asList(
            "dynamax", "gigantamax", ""
    );


}
