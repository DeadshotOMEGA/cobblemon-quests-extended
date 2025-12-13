package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigPokemonType;
import cobblemonquestsextended.cobblemon_quests_extended.registry.PokemonListCategory;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.*;

/**
 * Selector screen for Pokemon species.
 * Displays all Pokemon in a flat alphabetical list (no grouping).
 * Attempts to load Pokemon from Cobblemon at runtime.
 */
@Environment(EnvType.CLIENT)
public class SelectPokemonScreen extends AbstractSelectorScreen<String, PokemonListCategory> {

    private final ConfigPokemonType config;
    private final ConfigCallback callback;
    private List<String> cachedPokemonList;

    // Fallback Pokemon list if Cobblemon API is unavailable
    // Uses Cobblemon's internal snake_case naming convention
    private static final List<String> FALLBACK_POKEMON = List.of(
            // Gen 1 (including special names)
            "bulbasaur", "ivysaur", "venusaur", "charmander", "charmeleon", "charizard",
            "squirtle", "wartortle", "blastoise", "caterpie", "metapod", "butterfree",
            "weedle", "kakuna", "beedrill", "pidgey", "pidgeotto", "pidgeot",
            "rattata", "raticate", "spearow", "fearow", "ekans", "arbok",
            "pikachu", "raichu", "sandshrew", "sandslash", "nidoran_f", "nidorina",
            "nidoqueen", "nidoran_m", "nidorino", "nidoking", "clefairy", "clefable",
            "vulpix", "ninetales", "jigglypuff", "wigglytuff", "zubat", "golbat",
            "oddish", "gloom", "vileplume", "paras", "parasect", "venonat", "venomoth",
            "diglett", "dugtrio", "meowth", "persian", "psyduck", "golduck",
            "mankey", "primeape", "growlithe", "arcanine", "poliwag", "poliwhirl",
            "poliwrath", "abra", "kadabra", "alakazam", "machop", "machoke", "machamp",
            "bellsprout", "weepinbell", "victreebel", "tentacool", "tentacruel",
            "geodude", "graveler", "golem", "ponyta", "rapidash", "slowpoke", "slowbro",
            "magnemite", "magneton", "farfetchd", "doduo", "dodrio", "seel", "dewgong",
            "grimer", "muk", "shellder", "cloyster", "gastly", "haunter", "gengar",
            "onix", "drowzee", "hypno", "krabby", "kingler", "voltorb", "electrode",
            "exeggcute", "exeggutor", "cubone", "marowak", "hitmonlee", "hitmonchan",
            "lickitung", "koffing", "weezing", "rhyhorn", "rhydon", "chansey",
            "tangela", "kangaskhan", "horsea", "seadra", "goldeen", "seaking",
            "staryu", "starmie", "mr_mime", "scyther", "jynx", "electabuzz", "magmar",
            "pinsir", "tauros", "magikarp", "gyarados", "lapras", "ditto", "eevee",
            "vaporeon", "jolteon", "flareon", "porygon", "omanyte", "omastar",
            "kabuto", "kabutops", "aerodactyl", "snorlax", "articuno", "zapdos",
            "moltres", "dratini", "dragonair", "dragonite", "mewtwo", "mew",
            // Gen 2 (including special names)
            "chikorita", "bayleef", "meganium", "cyndaquil", "quilava", "typhlosion",
            "totodile", "croconaw", "feraligatr", "sentret", "furret", "hoothoot",
            "noctowl", "ledyba", "ledian", "spinarak", "ariados", "crobat", "chinchou",
            "lanturn", "pichu", "cleffa", "igglybuff", "togepi", "togetic", "natu",
            "xatu", "mareep", "flaaffy", "ampharos", "bellossom", "marill", "azumarill",
            "sudowoodo", "politoed", "hoppip", "skiploom", "jumpluff", "aipom", "sunkern",
            "sunflora", "yanma", "wooper", "quagsire", "espeon", "umbreon", "murkrow",
            "slowking", "misdreavus", "unown", "wobbuffet", "girafarig", "pineco",
            "forretress", "dunsparce", "gligar", "steelix", "snubbull", "granbull",
            "qwilfish", "scizor", "shuckle", "heracross", "sneasel", "teddiursa",
            "ursaring", "slugma", "magcargo", "swinub", "piloswine", "corsola",
            "remoraid", "octillery", "delibird", "mantine", "skarmory", "houndour",
            "houndoom", "kingdra", "phanpy", "donphan", "porygon2", "stantler", "smeargle",
            "tyrogue", "hitmontop", "smoochum", "elekid", "magby", "miltank", "blissey",
            "raikou", "entei", "suicune", "larvitar", "pupitar", "tyranitar", "lugia",
            "ho_oh", "celebi",
            // Gen 3 samples
            "treecko", "torchic", "mudkip", "ralts", "gardevoir", "rayquaza",
            // Gen 4 samples
            "turtwig", "chimchar", "piplup", "lucario", "arceus", "mime_jr", "porygon_z",
            // Gen 5 samples
            "snivy", "tepig", "oshawott", "zorua", "zekrom",
            // Gen 6 samples
            "chespin", "fennekin", "froakie", "sylveon", "xerneas",
            // Gen 7 samples (including special names)
            "rowlet", "litten", "popplio", "mimikyu", "solgaleo",
            "type_null", "kommo_o", "jangmo_o", "hakamo_o",
            // Gen 8 samples (including special names)
            "grookey", "scorbunny", "sobble", "corviknight", "zacian",
            "mr_rime", "sirfetchd",
            // Gen 9 samples (including special names)
            "sprigatito", "fuecoco", "quaxly", "pawmi", "koraidon",
            "wo_chien", "chien_pao", "ting_lu", "chi_yu", "great_tusk", "iron_treads"
    );

    public SelectPokemonScreen(ConfigPokemonType config, ConfigCallback callback) {
        super(
                PokemonListCategory.class,
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
    protected Map<PokemonListCategory, List<String>> getGroupedItems() {
        if (cachedPokemonList == null) {
            cachedPokemonList = buildPokemonList();
        }
        // Return all Pokemon under a single category
        return Map.of(PokemonListCategory.ALL, cachedPokemonList);
    }

    private List<String> buildPokemonList() {
        List<String> allPokemon = loadPokemonFromCobblemon();
        if (allPokemon.isEmpty()) {
            allPokemon = new ArrayList<>(FALLBACK_POKEMON);
        }

        // Sort alphabetically (case-insensitive)
        try {
            allPokemon.sort(String.CASE_INSENSITIVE_ORDER);
        } catch (Exception e) {
            // Sorting failed, leave as-is
        }

        return allPokemon;
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
                try {
                    java.lang.reflect.Method getNameMethod = spec.getClass().getMethod("getName");
                    String name = (String) getNameMethod.invoke(spec);
                    if (name != null && !name.isEmpty()) {
                        // Normalize the name: lowercase, handle ResourceLocation format
                        String normalized = normalizePokemonName(name);
                        if (!normalized.isEmpty()) {
                            pokemon.add(normalized);
                        }
                    }
                } catch (Exception e) {
                    // Skip this Pokemon if we can't get its name
                }
            }
        } catch (Exception e) {
            // Cobblemon not available or API changed - use fallback
        }

        return pokemon;
    }

    /**
     * Normalizes a Pokemon name from Cobblemon's internal format.
     * Handles various formats like ResourceLocation (namespace:name) and ensures lowercase.
     */
    private String normalizePokemonName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        try {
            String result = name;

            // Handle ResourceLocation format (e.g., "cobblemon:pikachu")
            if (result.contains(":")) {
                result = result.substring(result.indexOf(':') + 1);
            }

            // Normalize to lowercase
            result = result.toLowerCase();

            // Trim any whitespace
            result = result.trim();

            return result;
        } catch (Exception e) {
            return name.toLowerCase();
        }
    }

    @Override
    protected Component getItemDisplayName(String item) {
        if (item == null || item.isEmpty()) {
            return Component.literal("Unknown");
        }

        try {
            // Try Cobblemon's translation key first
            String translationKey = "cobblemon.species." + item + ".name";
            if (net.minecraft.client.resources.language.I18n.exists(translationKey)) {
                return Component.translatable(translationKey);
            }
        } catch (Exception e) {
            // Translation lookup failed, use fallback
        }

        // Fall back to nicely formatted name
        return Component.literal(formatPokemonName(item));
    }

    /**
     * Formats a Pokemon ID into a readable display name.
     * Converts underscores/dashes to spaces and capitalizes words.
     */
    private String formatPokemonName(String name) {
        if (name == null || name.isEmpty()) {
            return "Unknown";
        }

        try {
            // Replace underscores and dashes with spaces, then capitalize each word
            String[] words = name.replace("_", " ").replace("-", " ").split(" ");
            StringBuilder result = new StringBuilder();
            for (String word : words) {
                if (word != null && !word.isEmpty()) {
                    if (result.length() > 0) {
                        result.append(" ");
                    }
                    result.append(Character.toUpperCase(word.charAt(0)));
                    if (word.length() > 1) {
                        result.append(word.substring(1).toLowerCase());
                    }
                }
            }
            return result.length() > 0 ? result.toString() : name;
        } catch (Exception e) {
            // Fallback to original name if formatting fails
            return name;
        }
    }

    @Override
    protected Color4I getCategoryColor(PokemonListCategory category) {
        return category.getColor4I();
    }

    @Override
    protected String getCategoryTranslationKey(PokemonListCategory category) {
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
    protected ChatFormatting getCategoryFormatting(PokemonListCategory category) {
        return ChatFormatting.AQUA;
    }

    /**
     * Override to add item buttons directly without category headers.
     * Since we have only one category, we skip the header for a cleaner list.
     */
    @Override
    public void addButtons(Panel panel) {
        Map<PokemonListCategory, List<String>> groupedItems = getGroupedItems();
        List<String> items = groupedItems.get(PokemonListCategory.ALL);

        if (items != null) {
            for (String item : items) {
                panel.add(new FlatItemButton(panel, item));
            }
        }

        // Set consistent width for all buttons
        int width = panel.getWidgets().stream()
                .map(Widget::getWidth)
                .max(Integer::compare)
                .orElse(200);
        panel.getWidgets().forEach(w -> w.setWidth(width));
    }

    /**
     * Simplified item button without category color indicator on the left.
     */
    private class FlatItemButton extends ItemButton {
        public FlatItemButton(Panel panel, String item) {
            super(panel, item, PokemonListCategory.ALL);
        }

        @Override
        public void drawBackground(net.minecraft.client.gui.GuiGraphics graphics,
                                   dev.ftb.mods.ftblibrary.ui.Theme theme,
                                   int x, int y, int w, int h) {
            // Simple hover highlight without category color indicator
            if (isMouseOver) {
                Color4I.WHITE.withAlpha(30).draw(graphics, x, y, w, h);
            }
            Color4I.GRAY.withAlpha(40).draw(graphics, x, y + h, w, 1);
        }
    }
}
