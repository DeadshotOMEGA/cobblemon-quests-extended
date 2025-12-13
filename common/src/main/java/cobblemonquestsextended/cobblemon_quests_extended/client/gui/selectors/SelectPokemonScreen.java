package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigPokemonType;
import cobblemonquestsextended.cobblemon_quests_extended.registry.PokemonGeneration;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Selector screen for Pokemon species.
 * Groups Pokemon by generation using PokemonGeneration enum.
 * Attempts to load Pokemon from Cobblemon at runtime.
 */
@Environment(EnvType.CLIENT)
public class SelectPokemonScreen extends AbstractSelectorScreen<String, PokemonGeneration> {

    private final ConfigPokemonType config;
    private final ConfigCallback callback;
    private Map<PokemonGeneration, List<String>> cachedGroupedItems;

    // Fallback Pokemon list if Cobblemon API is unavailable
    private static final List<String> FALLBACK_POKEMON = List.of(
            // Gen 1 samples
            "bulbasaur", "charmander", "squirtle", "pikachu", "eevee", "mewtwo",
            // Gen 2 samples
            "chikorita", "cyndaquil", "totodile", "togepi", "lugia",
            // Gen 3 samples
            "treecko", "torchic", "mudkip", "ralts", "rayquaza",
            // Gen 4 samples
            "turtwig", "chimchar", "piplup", "lucario", "arceus",
            // Gen 5 samples
            "snivy", "tepig", "oshawott", "zorua", "zekrom",
            // Gen 6 samples
            "chespin", "fennekin", "froakie", "sylveon", "xerneas",
            // Gen 7 samples
            "rowlet", "litten", "popplio", "mimikyu", "solgaleo",
            // Gen 8 samples
            "grookey", "scorbunny", "sobble", "corviknight", "zacian",
            // Gen 9 samples
            "sprigatito", "fuecoco", "quaxly", "pawmi", "koraidon"
    );

    public SelectPokemonScreen(ConfigPokemonType config, ConfigCallback callback) {
        super(
                PokemonGeneration.class,
                Component.translatable("cobblemon_quests_extended.gui.select_pokemon"),
                false,
                config.getValue() != null ? List.of(config.getValue()) : List.of(),
                items -> {
                    if (!items.isEmpty()) {
                        config.setCurrentValue(items.get(0));
                        callback.save(true);
                    } else {
                        callback.save(false);
                    }
                }
        );
        this.config = config;
        this.callback = callback;
    }

    @Override
    protected Map<PokemonGeneration, List<String>> getGroupedItems() {
        if (cachedGroupedItems == null) {
            cachedGroupedItems = buildGroupedItems();
        }
        return cachedGroupedItems;
    }

    private Map<PokemonGeneration, List<String>> buildGroupedItems() {
        Map<PokemonGeneration, List<String>> result = new EnumMap<>(PokemonGeneration.class);
        for (PokemonGeneration gen : PokemonGeneration.values()) {
            result.put(gen, new ArrayList<>());
        }

        List<String> allPokemon = loadPokemonFromCobblemon();
        if (allPokemon.isEmpty()) {
            allPokemon = FALLBACK_POKEMON;
        }

        // Group by generation based on known dex ranges
        // Since we don't have dex numbers, use the fallback categorization
        for (String pokemon : allPokemon) {
            PokemonGeneration gen = estimateGeneration(pokemon);
            result.get(gen).add(pokemon);
        }

        // Sort each list alphabetically
        for (List<String> list : result.values()) {
            Collections.sort(list);
        }

        // Remove empty generations
        result.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        return result;
    }

    private List<String> loadPokemonFromCobblemon() {
        List<String> pokemon = new ArrayList<>();

        try {
            // Try to access Cobblemon's species registry
            // This uses reflection to avoid compile-time dependency issues
            Class<?> pokemonSpeciesClass = Class.forName("com.cobblemon.mod.common.api.pokemon.PokemonSpecies");
            Object instance = pokemonSpeciesClass.getField("INSTANCE").get(null);
            java.lang.reflect.Method getSpeciesMethod = pokemonSpeciesClass.getMethod("getSpecies");
            @SuppressWarnings("unchecked")
            Iterable<?> species = (Iterable<?>) getSpeciesMethod.invoke(instance);

            for (Object spec : species) {
                java.lang.reflect.Method getNameMethod = spec.getClass().getMethod("getName");
                String name = (String) getNameMethod.invoke(spec);
                pokemon.add(name.toLowerCase());
            }
        } catch (Exception e) {
            // Cobblemon not available or API changed - use fallback
        }

        return pokemon;
    }

    private PokemonGeneration estimateGeneration(String pokemon) {
        // Known Pokemon to generation mapping for common cases
        Map<String, PokemonGeneration> knownMappings = Map.ofEntries(
                // Gen 1
                Map.entry("bulbasaur", PokemonGeneration.GEN_1),
                Map.entry("charmander", PokemonGeneration.GEN_1),
                Map.entry("squirtle", PokemonGeneration.GEN_1),
                Map.entry("pikachu", PokemonGeneration.GEN_1),
                Map.entry("eevee", PokemonGeneration.GEN_1),
                Map.entry("mewtwo", PokemonGeneration.GEN_1),
                // Gen 2
                Map.entry("chikorita", PokemonGeneration.GEN_2),
                Map.entry("cyndaquil", PokemonGeneration.GEN_2),
                Map.entry("totodile", PokemonGeneration.GEN_2),
                Map.entry("togepi", PokemonGeneration.GEN_2),
                Map.entry("lugia", PokemonGeneration.GEN_2),
                // Gen 3
                Map.entry("treecko", PokemonGeneration.GEN_3),
                Map.entry("torchic", PokemonGeneration.GEN_3),
                Map.entry("mudkip", PokemonGeneration.GEN_3),
                Map.entry("ralts", PokemonGeneration.GEN_3),
                Map.entry("rayquaza", PokemonGeneration.GEN_3),
                // Gen 4
                Map.entry("turtwig", PokemonGeneration.GEN_4),
                Map.entry("chimchar", PokemonGeneration.GEN_4),
                Map.entry("piplup", PokemonGeneration.GEN_4),
                Map.entry("lucario", PokemonGeneration.GEN_4),
                Map.entry("arceus", PokemonGeneration.GEN_4),
                // Gen 5
                Map.entry("snivy", PokemonGeneration.GEN_5),
                Map.entry("tepig", PokemonGeneration.GEN_5),
                Map.entry("oshawott", PokemonGeneration.GEN_5),
                Map.entry("zorua", PokemonGeneration.GEN_5),
                Map.entry("zekrom", PokemonGeneration.GEN_5),
                // Gen 6
                Map.entry("chespin", PokemonGeneration.GEN_6),
                Map.entry("fennekin", PokemonGeneration.GEN_6),
                Map.entry("froakie", PokemonGeneration.GEN_6),
                Map.entry("sylveon", PokemonGeneration.GEN_6),
                Map.entry("xerneas", PokemonGeneration.GEN_6),
                // Gen 7
                Map.entry("rowlet", PokemonGeneration.GEN_7),
                Map.entry("litten", PokemonGeneration.GEN_7),
                Map.entry("popplio", PokemonGeneration.GEN_7),
                Map.entry("mimikyu", PokemonGeneration.GEN_7),
                Map.entry("solgaleo", PokemonGeneration.GEN_7),
                // Gen 8
                Map.entry("grookey", PokemonGeneration.GEN_8),
                Map.entry("scorbunny", PokemonGeneration.GEN_8),
                Map.entry("sobble", PokemonGeneration.GEN_8),
                Map.entry("corviknight", PokemonGeneration.GEN_8),
                Map.entry("zacian", PokemonGeneration.GEN_8),
                // Gen 9
                Map.entry("sprigatito", PokemonGeneration.GEN_9),
                Map.entry("fuecoco", PokemonGeneration.GEN_9),
                Map.entry("quaxly", PokemonGeneration.GEN_9),
                Map.entry("pawmi", PokemonGeneration.GEN_9),
                Map.entry("koraidon", PokemonGeneration.GEN_9)
        );

        return knownMappings.getOrDefault(pokemon.toLowerCase(), PokemonGeneration.GEN_1);
    }

    @Override
    protected Component getItemDisplayName(String item) {
        return Component.translatable("cobblemon.species." + item + ".name");
    }

    @Override
    protected Color4I getCategoryColor(PokemonGeneration category) {
        return category.getColor4I();
    }

    @Override
    protected String getCategoryTranslationKey(PokemonGeneration category) {
        return category.getTranslationKey();
    }

    @Override
    protected void onItemSelected(String item) {
        config.setCurrentValue(item);
        callback.save(true);
    }

    @Override
    protected boolean isItemSelected(String item) {
        return item.equals(config.getValue());
    }

    @Override
    protected ChatFormatting getCategoryFormatting(PokemonGeneration category) {
        return switch (category) {
            case GEN_1 -> ChatFormatting.RED;
            case GEN_2 -> ChatFormatting.GOLD;
            case GEN_3 -> ChatFormatting.GREEN;
            case GEN_4 -> ChatFormatting.BLUE;
            case GEN_5 -> ChatFormatting.DARK_GRAY;
            case GEN_6 -> ChatFormatting.LIGHT_PURPLE;
            case GEN_7 -> ChatFormatting.YELLOW;
            case GEN_8 -> ChatFormatting.DARK_PURPLE;
            case GEN_9 -> ChatFormatting.AQUA;
        };
    }
}
