package cobblemonquestsextended.cobblemon_quests_extended.domain.serialization;

import cobblemonquestsextended.cobblemon_quests_extended.model.CobblemonTaskModel;
import net.minecraft.nbt.CompoundTag;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Serializes/deserializes CobblemonTaskModel to/from NBT.
 * Supports version migration for backward compatibility.
 */
public class TaskModelSerializer {

    private static final int CURRENT_VERSION = 2;

    /**
     * Serializes the model to NBT.
     */
    public CompoundTag serialize(CobblemonTaskModel model) {
        CompoundTag nbt = new CompoundTag();

        // Version for future migrations
        nbt.putInt("version", CURRENT_VERSION);

        // Basic properties
        nbt.putLong("amount", model.getAmount());
        nbt.putBoolean("shiny", model.isShiny());
        nbt.putLong("time_min", model.getTimeMin());
        nbt.putLong("time_max", model.getTimeMax());
        nbt.putInt("min_level", model.getMinLevel());
        nbt.putInt("max_level", model.getMaxLevel());
        nbt.putString("dex_progress", model.getDexProgress());

        // Lists
        nbt.putString("action", writeList(model.getActions()));
        nbt.putString("biome", writeList(model.getBiomes()));
        nbt.putString("dimension", writeList(model.getDimensions()));
        nbt.putString("form", writeList(model.getForms()));
        nbt.putString("gender", writeList(model.getGenders()));
        nbt.putString("poke_ball_used", writeList(model.getPokeBallsUsed()));
        nbt.putString("pokemon", writeList(model.getPokemons()));
        nbt.putString("pokemon_type", writeList(model.getPokemonTypes()));
        nbt.putString("region", writeList(model.getRegions()));
        nbt.putString("natures", writeList(model.getNatures()));
        nbt.putString("tera_type", writeList(model.getTeraTypes()));
        nbt.putString("mega_form", writeList(model.getMegaForms()));
        nbt.putString("z_crystal", writeList(model.getZCrystals()));
        nbt.putString("dynamax_type", writeList(model.getDynamaxTypes()));

        return nbt;
    }

    /**
     * Deserializes the model from NBT with version migration.
     */
    public CobblemonTaskModel deserialize(CompoundTag nbt) {
        int version = nbt.contains("version") ? nbt.getInt("version") : 1;

        // Migrate if needed
        if (version < CURRENT_VERSION) {
            nbt = migrate(nbt, version);
        }

        return CobblemonTaskModel.builder()
            .amount(nbt.getLong("amount"))
            .shiny(nbt.getBoolean("shiny"))
            .timeMin(nbt.getLong("time_min"))
            .timeMax(nbt.getLong("time_max"))
            .minLevel(nbt.getInt("min_level"))
            .maxLevel(nbt.getInt("max_level"))
            .dexProgress(nbt.getString("dex_progress"))
            .actions(readList(nbt.getString("action")))
            .biomes(readList(nbt.getString("biome")))
            .dimensions(readList(nbt.getString("dimension")))
            .forms(readList(nbt.getString("form")))
            .genders(readList(nbt.getString("gender")))
            .pokeBallsUsed(readList(nbt.getString("poke_ball_used")))
            .pokemons(readList(nbt.getString("pokemon")))
            .pokemonTypes(readList(nbt.getString("pokemon_type")))
            .regions(readList(nbt.getString("region")))
            .natures(readList(nbt.getString("natures")))
            .teraTypes(readList(nbt.getString("tera_type")))
            .megaForms(readList(nbt.getString("mega_form")))
            .zCrystals(readList(nbt.getString("z_crystal")))
            .dynamaxTypes(readList(nbt.getString("dynamax_type")))
            .build();
    }

    /**
     * Migrates NBT data from older versions.
     */
    private CompoundTag migrate(CompoundTag nbt, int fromVersion) {
        CompoundTag migrated = nbt.copy();

        if (fromVersion < 2) {
            // v1 -> v2: Fix form naming (alola -> alolan, etc.)
            String forms = migrated.getString("form");
            if (!forms.isEmpty()) {
                Map<String, String> replacements = Map.of(
                    "alola", "alolan",
                    "galar", "galarian",
                    "paldea", "paldean",
                    "hisui", "hisuian"
                );
                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                    forms = forms.replace(entry.getKey(), entry.getValue());
                }
                migrated.putString("form", forms);
            }

            // v1 -> v2: Handle legacy "value" field
            if (nbt.contains("value") && !nbt.contains("amount")) {
                migrated.putLong("amount", nbt.getLong("value"));
            }

            // v1 -> v2: Handle legacy "entity" field
            if (nbt.contains("entity") && !nbt.contains("pokemon")) {
                migrated.putString("pokemon", nbt.getString("entity"));
            }

            // v1 -> v2: Fix time defaults
            if (migrated.getLong("time_min") == 0 && migrated.getLong("time_max") == 0) {
                migrated.putLong("time_max", 24000L);
            }

            // v1 -> v2: Fix amount default
            if (migrated.getLong("amount") == 0) {
                migrated.putLong("amount", 1L);
            }

            // v1 -> v2: Fix dex_progress default
            if (migrated.getString("dex_progress").isEmpty()) {
                migrated.putString("dex_progress", "seen");
            }
        }

        migrated.putInt("version", CURRENT_VERSION);
        return migrated;
    }

    private String writeList(List<String> list) {
        return list.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.joining(","));
    }

    private List<String> readList(String s) {
        if (s == null || s.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(s.split(","))
            .map(String::trim)
            .filter(obj -> !obj.isEmpty() && !obj.contains("choice_any"))
            .distinct()
            .collect(Collectors.toCollection(ArrayList::new));
    }
}
