package cobblemonquestsextended.cobblemon_quests_extended.serialization;

import cobblemonquestsextended.cobblemon_quests_extended.model.CobblemonTaskModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Serializer for converting {@link CobblemonTaskModel} to/from NBT and network buffers.
 *
 * <p>This class handles version-aware serialization with automatic migration from v1 (legacy)
 * to v2 (current) format. V1 uses the original CobblemonTask field names while V2 uses
 * normalized model field names.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *     <li>NBT version tracking via {@code _model_version} tag</li>
 *     <li>Automatic v1 to v2 field name migration</li>
 *     <li>Form name normalization (e.g., "alola" to "alolan")</li>
 *     <li>Network buffer serialization for client-server sync</li>
 * </ul>
 */
public final class TaskModelSerializer {

    /**
     * Current NBT serialization version. Increment when making breaking changes.
     */
    public static final int NBT_VERSION = 2;

    /**
     * NBT tag key for storing the model version.
     */
    private static final String VERSION_TAG = "_model_version";

    // ===== V2 NBT Field Names (current) =====
    private static final String ACTIONS = "actions";
    private static final String AMOUNT = "amount";
    private static final String SHINY = "shiny";
    private static final String POKEMONS = "pokemons";
    private static final String POKEMON_TYPES = "pokemonTypes";
    private static final String NATURES = "natures";
    private static final String REGIONS = "regions";
    private static final String GENDERS = "genders";
    private static final String FORMS = "forms";
    private static final String BIOMES = "biomes";
    private static final String DIMENSIONS = "dimensions";
    private static final String POKE_BALLS_USED = "pokeBallsUsed";
    private static final String TIME_MIN = "timeMin";
    private static final String TIME_MAX = "timeMax";
    private static final String MIN_LEVEL = "minLevel";
    private static final String MAX_LEVEL = "maxLevel";
    private static final String DEX_PROGRESS = "dexProgress";
    private static final String TERA_TYPES = "teraTypes";
    private static final String MEGA_FORMS = "megaForms";
    private static final String Z_CRYSTALS = "zCrystals";
    private static final String DYNAMAX_TYPES = "dynamaxTypes";

    // ===== V1 NBT Field Names (legacy) =====
    private static final String V1_ACTION = "action";
    private static final String V1_POKEMON = "pokemon";
    private static final String V1_POKEMON_TYPE = "pokemon_type";
    private static final String V1_NATURES = "natures";
    private static final String V1_REGION = "region";
    private static final String V1_GENDER = "gender";
    private static final String V1_FORM = "form";
    private static final String V1_BIOME = "biome";
    private static final String V1_DIMENSION = "dimension";
    private static final String V1_POKE_BALL_USED = "poke_ball_used";
    private static final String V1_TIME_MIN = "time_min";
    private static final String V1_TIME_MAX = "time_max";
    private static final String V1_MIN_LEVEL = "min_level";
    private static final String V1_MAX_LEVEL = "max_level";
    private static final String V1_DEX_PROGRESS = "dex_progress";
    private static final String V1_TERA_TYPE = "tera_type";
    private static final String V1_MEGA_FORM = "mega_form";
    private static final String V1_Z_CRYSTAL = "z_crystal";
    private static final String V1_DYNAMAX_TYPE = "dynamax_type";

    // Legacy field aliases (for v1 migration)
    private static final String V1_VALUE = "value";
    private static final String V1_ENTITY = "entity";

    /**
     * Form name replacements for normalization.
     * Maps shorthand region names to their proper adjective forms.
     */
    private static final Map<String, String> FORM_REPLACEMENTS = Map.of(
            "alola", "alolan",
            "galar", "galarian",
            "paldea", "paldean",
            "hisui", "hisuian"
    );

    private TaskModelSerializer() {
        // Utility class - prevent instantiation
    }

    // ===== NBT Serialization =====

    /**
     * Serializes a {@link CobblemonTaskModel} to NBT format.
     *
     * @param model    the model to serialize
     * @param provider the holder lookup provider for registry access
     * @return a CompoundTag containing the serialized model data
     */
    public static CompoundTag toNbt(CobblemonTaskModel model, HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();

        // Write version tag
        nbt.putInt(VERSION_TAG, NBT_VERSION);

        // Action selection
        nbt.putString(ACTIONS, writeList(model.getActions()));

        // Basic conditions
        nbt.putLong(AMOUNT, model.getAmount());
        nbt.putBoolean(SHINY, model.isShiny());
        nbt.putString(POKEMONS, writeList(model.getPokemons()));
        nbt.putString(POKEMON_TYPES, writeList(model.getPokemonTypes()));
        nbt.putString(NATURES, writeList(model.getNatures()));
        nbt.putString(REGIONS, writeList(model.getRegions()));
        nbt.putString(GENDERS, writeList(model.getGenders()));
        nbt.putString(FORMS, writeList(model.getForms()));

        // Location and time conditions
        nbt.putString(BIOMES, writeList(model.getBiomes()));
        nbt.putString(DIMENSIONS, writeList(model.getDimensions()));
        nbt.putString(POKE_BALLS_USED, writeList(model.getPokeBallsUsed()));
        nbt.putLong(TIME_MIN, model.getTimeMin());
        nbt.putLong(TIME_MAX, model.getTimeMax());

        // Level conditions
        nbt.putInt(MIN_LEVEL, model.getMinLevel());
        nbt.putInt(MAX_LEVEL, model.getMaxLevel());

        // Pokedex progress condition
        nbt.putString(DEX_PROGRESS, model.getDexProgress());

        // Gimmick-specific conditions
        nbt.putString(TERA_TYPES, writeList(model.getTeraTypes()));
        nbt.putString(MEGA_FORMS, writeList(model.getMegaForms()));
        nbt.putString(Z_CRYSTALS, writeList(model.getZCrystals()));
        nbt.putString(DYNAMAX_TYPES, writeList(model.getDynamaxTypes()));

        return nbt;
    }

    /**
     * Deserializes a {@link CobblemonTaskModel} from NBT format.
     * Automatically detects and migrates v1 (legacy) format to v2.
     *
     * @param nbt      the NBT compound tag to deserialize
     * @param provider the holder lookup provider for registry access
     * @return the deserialized CobblemonTaskModel
     */
    public static CobblemonTaskModel fromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
        int version = detectVersion(nbt);

        if (version == 1) {
            return fromNbtV1(nbt);
        }

        return fromNbtV2(nbt);
    }

    /**
     * Detects the NBT version. Missing version tag indicates v1 (legacy format).
     */
    private static int detectVersion(CompoundTag nbt) {
        if (!nbt.contains(VERSION_TAG)) {
            return 1;
        }
        return nbt.getInt(VERSION_TAG);
    }

    /**
     * Deserializes from v2 format (current).
     */
    private static CobblemonTaskModel fromNbtV2(CompoundTag nbt) {
        return CobblemonTaskModel.builder()
                .actions(readList(nbt.getString(ACTIONS)))
                .amount(nbt.getLong(AMOUNT))
                .shiny(nbt.getBoolean(SHINY))
                .pokemons(readList(nbt.getString(POKEMONS)))
                .pokemonTypes(readList(nbt.getString(POKEMON_TYPES)))
                .natures(readList(nbt.getString(NATURES)))
                .regions(readList(nbt.getString(REGIONS)))
                .genders(readList(nbt.getString(GENDERS)))
                .forms(readList(nbt.getString(FORMS)))
                .biomes(readList(nbt.getString(BIOMES)))
                .dimensions(readList(nbt.getString(DIMENSIONS)))
                .pokeBallsUsed(readList(nbt.getString(POKE_BALLS_USED)))
                .timeMin(nbt.getLong(TIME_MIN))
                .timeMax(nbt.getLong(TIME_MAX))
                .minLevel(nbt.getInt(MIN_LEVEL))
                .maxLevel(nbt.getInt(MAX_LEVEL))
                .dexProgress(nbt.getString(DEX_PROGRESS))
                .teraTypes(readList(nbt.getString(TERA_TYPES)))
                .megaForms(readList(nbt.getString(MEGA_FORMS)))
                .zCrystals(readList(nbt.getString(Z_CRYSTALS)))
                .dynamaxTypes(readList(nbt.getString(DYNAMAX_TYPES)))
                .build();
    }

    /**
     * Deserializes from v1 format (legacy CobblemonTask) with migration.
     * Handles legacy field names and applies normalization.
     */
    private static CobblemonTaskModel fromNbtV1(CompoundTag nbt) {
        CobblemonTaskModel.Builder builder = CobblemonTaskModel.builder();

        // Read v1 fields with legacy names
        builder.actions(readList(nbt.getString(V1_ACTION)));
        builder.shiny(nbt.getBoolean(SHINY));
        builder.pokemonTypes(readList(nbt.getString(V1_POKEMON_TYPE)));
        builder.natures(readList(nbt.getString(V1_NATURES)));
        builder.regions(readList(nbt.getString(V1_REGION)));
        builder.genders(readList(nbt.getString(V1_GENDER)));
        builder.biomes(readList(nbt.getString(V1_BIOME)));
        builder.dimensions(readList(nbt.getString(V1_DIMENSION)));
        builder.pokeBallsUsed(readList(nbt.getString(V1_POKE_BALL_USED)));
        builder.minLevel(nbt.getInt(V1_MIN_LEVEL));
        builder.maxLevel(nbt.getInt(V1_MAX_LEVEL));

        // Gimmick fields
        builder.teraTypes(readList(nbt.getString(V1_TERA_TYPE)));
        builder.megaForms(readList(nbt.getString(V1_MEGA_FORM)));
        builder.zCrystals(readList(nbt.getString(V1_Z_CRYSTAL)));
        builder.dynamaxTypes(readList(nbt.getString(V1_DYNAMAX_TYPE)));

        // Handle amount with legacy "value" alias
        long amount = nbt.getLong(AMOUNT);
        if (amount == 0 && nbt.contains(V1_VALUE)) {
            amount = nbt.getLong(V1_VALUE);
        }
        if (amount == 0) {
            amount = 1;
        }
        builder.amount(amount);

        // Handle pokemons with legacy "entity" alias
        ArrayList<String> pokemons = readList(nbt.getString(V1_POKEMON));
        if (pokemons.isEmpty() && nbt.contains(V1_ENTITY)) {
            pokemons = readList(nbt.getString(V1_ENTITY));
        }
        pokemons.remove("minecraft:");
        builder.pokemons(pokemons);

        // Handle forms with normalization
        ArrayList<String> forms = readList(nbt.getString(V1_FORM));
        forms.replaceAll(form -> FORM_REPLACEMENTS.getOrDefault(form, form));
        builder.forms(forms);

        // Handle time with default correction
        long timeMin = nbt.getLong(V1_TIME_MIN);
        long timeMax = nbt.getLong(V1_TIME_MAX);
        if (timeMin == 0 && timeMax == 0) {
            timeMax = 24000;
        }
        builder.timeMin(timeMin);
        builder.timeMax(timeMax);

        // Handle dexProgress with default
        String dexProgress = nbt.getString(V1_DEX_PROGRESS);
        if (dexProgress.isEmpty()) {
            dexProgress = "seen";
        }
        builder.dexProgress(dexProgress);

        return builder.build();
    }

    // ===== Network Buffer Serialization =====

    /**
     * Serializes a {@link CobblemonTaskModel} to a network buffer for client-server sync.
     *
     * @param model the model to serialize
     * @param buf   the registry-friendly byte buffer to write to
     */
    public static void toNetworkBuffer(CobblemonTaskModel model, RegistryFriendlyByteBuf buf) {
        // Write in a fixed order matching the read order
        buf.writeLong(model.getAmount());
        buf.writeBoolean(model.isShiny());
        buf.writeLong(model.getTimeMin());
        buf.writeLong(model.getTimeMax());
        buf.writeUtf(writeList(model.getPokemons()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getActions()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getBiomes()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getDimensions()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getForms()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getGenders()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getPokeBallsUsed()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getPokemonTypes()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getRegions()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getNatures()), Short.MAX_VALUE);
        buf.writeInt(model.getMinLevel());
        buf.writeInt(model.getMaxLevel());
        buf.writeUtf(model.getDexProgress(), Short.MAX_VALUE);
        // Gimmick-specific fields
        buf.writeUtf(writeList(model.getTeraTypes()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getMegaForms()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getZCrystals()), Short.MAX_VALUE);
        buf.writeUtf(writeList(model.getDynamaxTypes()), Short.MAX_VALUE);
    }

    /**
     * Deserializes a {@link CobblemonTaskModel} from a network buffer.
     *
     * @param buf the registry-friendly byte buffer to read from
     * @return the deserialized CobblemonTaskModel
     */
    public static CobblemonTaskModel fromNetworkBuffer(RegistryFriendlyByteBuf buf) {
        // Read in the same fixed order as write
        long amount = buf.readLong();
        boolean shiny = buf.readBoolean();
        long timeMin = buf.readLong();
        long timeMax = buf.readLong();
        ArrayList<String> pokemons = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> actions = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> biomes = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> dimensions = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> forms = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> genders = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> pokeBallsUsed = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> pokemonTypes = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> regions = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> natures = readList(buf.readUtf(Short.MAX_VALUE));
        int minLevel = buf.readInt();
        int maxLevel = buf.readInt();
        String dexProgress = buf.readUtf(Short.MAX_VALUE);
        // Gimmick-specific fields
        ArrayList<String> teraTypes = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> megaForms = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> zCrystals = readList(buf.readUtf(Short.MAX_VALUE));
        ArrayList<String> dynamaxTypes = readList(buf.readUtf(Short.MAX_VALUE));

        return CobblemonTaskModel.builder()
                .amount(amount)
                .shiny(shiny)
                .timeMin(timeMin)
                .timeMax(timeMax)
                .pokemons(pokemons)
                .actions(actions)
                .biomes(biomes)
                .dimensions(dimensions)
                .forms(forms)
                .genders(genders)
                .pokeBallsUsed(pokeBallsUsed)
                .pokemonTypes(pokemonTypes)
                .regions(regions)
                .natures(natures)
                .minLevel(minLevel)
                .maxLevel(maxLevel)
                .dexProgress(dexProgress)
                .teraTypes(teraTypes)
                .megaForms(megaForms)
                .zCrystals(zCrystals)
                .dynamaxTypes(dynamaxTypes)
                .build();
    }

    // ===== Helper Methods =====

    /**
     * Parses a comma-separated string into a list of distinct, non-empty values.
     * Filters out empty strings and "choice_any" placeholder values.
     *
     * @param s the comma-separated string to parse
     * @return an ArrayList of trimmed, distinct, non-empty values
     */
    private static ArrayList<String> readList(String s) {
        if (s == null || s.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(obj -> !obj.isEmpty() && !obj.contains("choice_any"))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Joins a list of strings into a comma-separated string.
     * Filters out null values before joining.
     *
     * @param list the list of strings to join
     * @return a comma-separated string of non-null values
     */
    private static String writeList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(",", list.stream().filter(Objects::nonNull).toList());
    }
}
