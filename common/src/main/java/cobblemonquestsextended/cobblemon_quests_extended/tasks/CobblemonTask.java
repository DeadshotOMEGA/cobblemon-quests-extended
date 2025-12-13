package cobblemonquestsextended.cobblemon_quests_extended.tasks;

import com.cobblemon.mod.common.CobblemonItemComponents;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.PokedexManager;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.item.components.PokemonItemComponent;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigActionType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigBiomeType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigDimensionType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigDynamaxType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigFormType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigGenderType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigMegaFormType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigNatureType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigPokeBallType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigPokemonType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigRegionType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigTeraType;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigTypeSelector;
import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigZCrystalType;
import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.net.EditObjectMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector4f;
import cobblemonquestsextended.cobblemon_quests_extended.config.CobblemonQuestsConfig;

import java.util.*;
import java.util.stream.Collectors;

import static cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests.MOD_ID;
import static cobblemonquestsextended.cobblemon_quests_extended.tasks.TaskData.*;

public class CobblemonTask extends Task {
    public Icon pokeBallIcon = ItemIcon.getItemIcon(PokeBalls.getPokeBall().item());
    public long amount = 1L;
    public boolean shiny = false;
    public long timeMin = 0;
    public long timeMax = 24000;
    public int minLevel = 0;
    public int maxLevel = 0;
    public String dexProgress = "seen";
    public ArrayList<String> actions = new ArrayList<>();
    public ArrayList<String> biomes = new ArrayList<>();
    public ArrayList<String> dimensions = new ArrayList<>();
    public ArrayList<String> forms = new ArrayList<>();
    public ArrayList<String> genders = new ArrayList<>();
    public ArrayList<String> pokeBallsUsed = new ArrayList<>();
    public ArrayList<String> pokemons = new ArrayList<>();
    public ArrayList<String> pokemonTypes = new ArrayList<>();
    public ArrayList<String> regions = new ArrayList<>();
    public ArrayList<String> natures = new ArrayList<>();
    // New condition fields for extended features
    public ArrayList<String> teraTypes = new ArrayList<>();
    public ArrayList<String> megaForms = new ArrayList<>();
    public ArrayList<String> zCrystals = new ArrayList<>();
    public ArrayList<String> dynamaxTypes = new ArrayList<>();

    // Static sets for field visibility rules (Phase 2)
    private static final Set<String> CATCH_BATTLE_ACTIONS = Set.of(
        "catch", "obtain", "revive_fossil", "reel", "hatch_egg", "select_starter",
        "defeat", "defeat_player", "defeat_npc", "kill", "faint_pokemon"
    );

    private static final Set<String> FORM_RELATED_ACTIONS = Set.of(
        "catch", "obtain", "revive_fossil", "reel", "select_starter", "hatch_egg",
        "evolve", "evolve_into", "change_form",
        "register", "have_registered", "scan"
    );

    public CobblemonTask(long id, Quest quest) {
        super(id, quest);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.COBBLEMON;
    }

    @Override
    public long getMaxProgress() {
        return amount;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putLong("amount", amount);
        nbt.putBoolean("shiny", shiny);
        nbt.putLong("time_min", timeMin);
        nbt.putLong("time_max", timeMax);
        nbt.putString("action", writeList(actions));
        nbt.putString("biome", writeList(biomes));
        nbt.putString("dimension", writeList(dimensions));
        nbt.putString("pokemon", writeList(pokemons));
        nbt.putString("form", writeList(forms));
        nbt.putString("gender", writeList(genders));
        nbt.putString("poke_ball_used", writeList(pokeBallsUsed));
        nbt.putString("pokemon_type", writeList(pokemonTypes));
        nbt.putString("region", writeList(regions));
        nbt.putString("natures", writeList(natures));
        nbt.putInt("min_level", minLevel);
        nbt.putInt("max_level", maxLevel);
        nbt.putString("dex_progress", dexProgress);
        // New extended condition fields
        nbt.putString("tera_type", writeList(teraTypes));
        nbt.putString("mega_form", writeList(megaForms));
        nbt.putString("z_crystal", writeList(zCrystals));
        nbt.putString("dynamax_type", writeList(dynamaxTypes));
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        amount = nbt.getLong("amount");
        shiny = nbt.getBoolean("shiny");
        timeMin = nbt.getLong("time_min");
        timeMax = nbt.getLong("time_max");
        actions = readList(nbt.getString("action"));
        biomes = readList(nbt.getString("biome"));
        dimensions = readList(nbt.getString("dimension"));
        pokemons = readList(nbt.getString("pokemon"));
        forms = readList(nbt.getString("form"));
        genders = readList(nbt.getString("gender"));
        pokeBallsUsed = readList(nbt.getString("poke_ball_used"));
        pokemonTypes = readList(nbt.getString("pokemon_type"));
        regions = readList(nbt.getString("region"));
        natures = readList(nbt.getString("natures"));
        minLevel = nbt.getInt("min_level");
        maxLevel = nbt.getInt("max_level");
        dexProgress = nbt.getString("dex_progress");
        // New extended condition fields
        teraTypes = readList(nbt.getString("tera_type"));
        megaForms = readList(nbt.getString("mega_form"));
        zCrystals = readList(nbt.getString("z_crystal"));
        dynamaxTypes = readList(nbt.getString("dynamax_type"));

        if (!forms.isEmpty()) {
            Map<String, String> formReplacements = Map.of(
                    "alola", "alolan",
                    "galar", "galarian",
                    "paldea", "paldean",
                    "hisui", "hisuian"
            );
            forms.replaceAll(form -> formReplacements.getOrDefault(form, form));
        }
        if (timeMin == timeMax && timeMin == 0) {
            timeMax = 24000;
        }
        if (nbt.contains("value")) {
            amount = nbt.getLong("value");
        }
        if (nbt.contains("entity")) {
            pokemons = readList(nbt.getString("entity"));
        }
        if (amount == 0) {
            amount = 1;
        }
        if (dexProgress.isEmpty()) {
            dexProgress = "seen";
        }
        pokemons.remove("minecraft:");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeLong(amount);
        buffer.writeBoolean(shiny);
        buffer.writeLong(timeMin);
        buffer.writeLong(timeMax);
        buffer.writeUtf(writeList(pokemons), Short.MAX_VALUE);
        buffer.writeUtf(writeList(actions), Short.MAX_VALUE);
        buffer.writeUtf(writeList(biomes), Short.MAX_VALUE);
        buffer.writeUtf(writeList(dimensions), Short.MAX_VALUE);
        buffer.writeUtf(writeList(forms), Short.MAX_VALUE);
        buffer.writeUtf(writeList(genders), Short.MAX_VALUE);
        buffer.writeUtf(writeList(pokeBallsUsed), Short.MAX_VALUE);
        buffer.writeUtf(writeList(pokemonTypes), Short.MAX_VALUE);
        buffer.writeUtf(writeList(regions), Short.MAX_VALUE);
        buffer.writeUtf(writeList(natures), Short.MAX_VALUE);
        buffer.writeInt(minLevel);
        buffer.writeInt(maxLevel);
        buffer.writeUtf(dexProgress, Short.MAX_VALUE);
        // New extended condition fields
        buffer.writeUtf(writeList(teraTypes), Short.MAX_VALUE);
        buffer.writeUtf(writeList(megaForms), Short.MAX_VALUE);
        buffer.writeUtf(writeList(zCrystals), Short.MAX_VALUE);
        buffer.writeUtf(writeList(dynamaxTypes), Short.MAX_VALUE);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        amount = buffer.readLong();
        shiny = buffer.readBoolean();
        timeMin = buffer.readLong();
        timeMax = buffer.readLong();
        pokemons = readList(buffer.readUtf(Short.MAX_VALUE));
        actions = readList(buffer.readUtf(Short.MAX_VALUE));
        biomes = readList(buffer.readUtf(Short.MAX_VALUE));
        dimensions = readList(buffer.readUtf(Short.MAX_VALUE));
        forms = readList(buffer.readUtf(Short.MAX_VALUE));
        genders = readList(buffer.readUtf(Short.MAX_VALUE));
        pokeBallsUsed = readList(buffer.readUtf(Short.MAX_VALUE));
        pokemonTypes = readList(buffer.readUtf(Short.MAX_VALUE));
        regions = readList(buffer.readUtf(Short.MAX_VALUE));
        natures = readList(buffer.readUtf(Short.MAX_VALUE));
        minLevel = buffer.readInt();
        maxLevel = buffer.readInt();
        dexProgress = buffer.readUtf(Short.MAX_VALUE);
        // New extended condition fields
        teraTypes = readList(buffer.readUtf(Short.MAX_VALUE));
        megaForms = readList(buffer.readUtf(Short.MAX_VALUE));
        zCrystals = readList(buffer.readUtf(Short.MAX_VALUE));
        dynamaxTypes = readList(buffer.readUtf(Short.MAX_VALUE));
    }

    public String writeList(ArrayList<String> list) {
        list.removeIf(Objects::isNull);
        return String.join(",", list);
    }

    public ArrayList<String> readList(String s) {
        return Arrays.stream(s.split(",")).map(String::trim).filter(obj -> !obj.isEmpty() && !obj.contains("choice_any")).distinct().collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Phase 3: Dynamic field visibility - reopens config screen when actions change.
     *
     * Uses custom CobblemonTaskEditScreen that changes the Accept button to "Update Fields"
     * when actions are modified, providing clear UX for the field rebuild feature.
     */
    @Override
    @Environment(EnvType.CLIENT)
    public void onEditButtonClicked(Runnable gui) {
        // Create config group with save callback
        // Use short id "task" to keep breadcrumbs short (e.g., "Task → Action" not "cobblemon_quests_extended → Action")
        ConfigGroup group = new ConfigGroup("task", accepted -> {
            gui.run();

            if (!accepted) {
                return; // User cancelled - do nothing
            }

            // Normal save flow
            if (validateEditedConfig()) {
                NetworkManager.sendToServer(EditObjectMessage.forQuestObject(this));
            }
        }) {
            @Override
            public Component getName() {
                return Component.translatable(MOD_ID + ".task.title");
            }

            @Override
            public String getNameKey() {
                return MOD_ID + ".config.root";
            }
        };

        // Populate config fields directly (skip createSubGroup to avoid deep breadcrumb nesting)
        fillConfigGroup(group);

        // Open custom edit screen with dynamic button behavior
        // When actions change, "Accept" becomes "Update Fields" and reopens the screen
        new cobblemonquestsextended.cobblemon_quests_extended.client.gui.CobblemonTaskEditScreen(
            group, this, () -> onEditButtonClicked(gui)
        ).openGui();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        // ===== GROUP 1: ACTION (always shown) =====
        ConfigGroup actionGroup = config.getOrCreateSubgroup("action")
            .setNameKey(MOD_ID + ".config.group.action");

        actionGroup.addList("actions", actions, new ConfigActionType(), "catch")
            .setNameKey(MOD_ID + ".task.actions");

        actionGroup.addLong("amount", amount, v -> amount = v, 1L, 1L, Long.MAX_VALUE)
            .setNameKey(MOD_ID + ".task.amount");

        // Only show remaining groups if at least one action is selected
        if (actions.isEmpty()) {
            return;
        }

        // ===== GROUP 2: POKEMON (species, types, properties) =====
        ConfigGroup pokemonGroup = config.getOrCreateSubgroup("pokemon")
            .setNameKey(MOD_ID + ".config.group.pokemon");

        pokemonGroup.addList("pokemons", pokemons, new ConfigPokemonType(), "")
            .setNameKey(MOD_ID + ".task.pokemons");

        pokemonGroup.addList("pokemon_types", pokemonTypes, new ConfigTypeSelector(), "")
            .setNameKey(MOD_ID + ".task.pokemon_types");

        if (shouldShowFormFields()) {
            pokemonGroup.addList("forms", forms, new ConfigFormType(), "")
                .setNameKey(MOD_ID + ".task.forms");
        }

        pokemonGroup.addList("genders", genders, new ConfigGenderType(), "")
            .setNameKey(MOD_ID + ".task.genders");

        pokemonGroup.addList("natures", natures, new ConfigNatureType(), "")
            .setNameKey(MOD_ID + ".task.natures");

        pokemonGroup.addList("regions", regions, new ConfigRegionType(), "")
            .setNameKey(MOD_ID + ".task.regions");

        pokemonGroup.addBool("shiny", shiny, v -> shiny = v, false)
            .setNameKey(MOD_ID + ".task.shiny");

        // ===== GROUP 3: LEVEL (conditional - level_up actions) =====
        if (shouldShowLevelFields()) {
            ConfigGroup levelGroup = config.getOrCreateSubgroup("level")
                .setNameKey(MOD_ID + ".config.group.level");

            levelGroup.addInt("min_level", minLevel, v -> minLevel = v, 0, 0, Integer.MAX_VALUE)
                .setNameKey(MOD_ID + ".task.min_level");

            levelGroup.addInt("max_level", maxLevel, v -> maxLevel = v, 0, 0, Integer.MAX_VALUE)
                .setNameKey(MOD_ID + ".task.max_level");
        }

        // ===== GROUP 4: LOCATION (conditional - catch/battle actions) =====
        if (shouldShowTimeLocationFields()) {
            ConfigGroup locationGroup = config.getOrCreateSubgroup("location")
                .setNameKey(MOD_ID + ".config.group.location");

            locationGroup.addList("biomes", biomes, new ConfigBiomeType(), "")
                .setNameKey(MOD_ID + ".task.biomes");

            locationGroup.addList("dimensions", dimensions, new ConfigDimensionType(), "")
                .setNameKey(MOD_ID + ".task.dimensions");

            locationGroup.addLong("time_min", timeMin, v -> timeMin = v, 0L, 0L, 24000L)
                .setNameKey(MOD_ID + ".task.time_min");

            locationGroup.addLong("time_max", timeMax, v -> timeMax = v, 24000L, 0L, 24000L)
                .setNameKey(MOD_ID + ".task.time_max");
        }

        // ===== GROUP 5: CAPTURE (conditional - catch/battle actions) =====
        if (shouldShowTimeLocationFields()) {
            ConfigGroup captureGroup = config.getOrCreateSubgroup("capture")
                .setNameKey(MOD_ID + ".config.group.capture");

            captureGroup.addList("pokeballs", pokeBallsUsed, new ConfigPokeBallType(), "")
                .setNameKey(MOD_ID + ".task.pokeballs");
        }

        // ===== GROUP 6: POKEDEX (conditional - register/scan actions) =====
        if (shouldShowDexProgressField()) {
            ConfigGroup pokedexGroup = config.getOrCreateSubgroup("pokedex")
                .setNameKey(MOD_ID + ".config.group.pokedex");

            List<String> dexProgressList = List.of("caught", "seen");
            pokedexGroup.addEnum("dex_progress", dexProgress, v -> dexProgress = v,
                NameMap.of(dexProgress, dexProgressList)
                    .nameKey(v -> "cobblemon_quests.dex_progress." + v)
                    .create(), dexProgress)
                .setNameKey(MOD_ID + ".task.dex_progress");
        }

        // ===== GROUP 7: GIMMICKS (conditional - gimmick actions) =====
        if (hasAnyGimmickAction()) {
            ConfigGroup gimmickGroup = config.getOrCreateSubgroup("gimmicks")
                .setNameKey(MOD_ID + ".config.group.gimmicks");

            if (shouldShowGimmickFields("mega_evolve")) {
                gimmickGroup.addList("mega_forms", megaForms, new ConfigMegaFormType(), "")
                    .setNameKey(MOD_ID + ".task.mega_forms");
            }

            if (shouldShowGimmickFields("terastallize")) {
                gimmickGroup.addList("tera_types", teraTypes, new ConfigTeraType(), "")
                    .setNameKey(MOD_ID + ".task.tera_types");
            }

            if (shouldShowGimmickFields("use_z_move")) {
                gimmickGroup.addList("z_crystals", zCrystals, new ConfigZCrystalType(), "")
                    .setNameKey(MOD_ID + ".task.z_crystals");
            }

            if (shouldShowGimmickFields("dynamax") || shouldShowGimmickFields("gigantamax")
                    || shouldShowGimmickFields("ultra_burst")) {
                gimmickGroup.addList("dynamax_types", dynamaxTypes, new ConfigDynamaxType(), "")
                    .setNameKey(MOD_ID + ".task.dynamax_types");
            }
        }
    }

    // ===== VISIBILITY HELPER METHODS =====

    /**
     * Checks if any selected action requires level restrictions.
     * Used to conditionally show min_level and max_level fields.
     */
    private boolean shouldShowLevelFields() {
        return actions.contains("level_up") || actions.contains("level_up_to");
    }

    /**
     * Checks if any selected action involves pokedex/dex progress tracking.
     * Used to conditionally show dex_progress field (seen vs caught).
     */
    private boolean shouldShowDexProgressField() {
        return actions.contains("register")
            || actions.contains("have_registered")
            || actions.contains("scan");
    }

    /**
     * Checks if any selected action is time/location dependent.
     * Catching and battle actions benefit from time and location filters.
     */
    private boolean shouldShowTimeLocationFields() {
        return actions.stream().anyMatch(CATCH_BATTLE_ACTIONS::contains);
    }

    /**
     * Checks if a specific gimmick action is selected.
     * Used for mega_evolve, terastallize, use_z_move, dynamax actions.
     */
    private boolean shouldShowGimmickFields(String gimmickAction) {
        return actions.contains(gimmickAction);
    }

    /**
     * Checks if form/variant filtering should be shown.
     * Catch, evolution, and pokedex actions support form filtering.
     */
    private boolean shouldShowFormFields() {
        return actions.stream().anyMatch(FORM_RELATED_ACTIONS::contains);
    }

    /**
     * Checks if any gimmick-related action is selected.
     */
    private boolean hasAnyGimmickAction() {
        return shouldShowGimmickFields("mega_evolve")
            || shouldShowGimmickFields("terastallize")
            || shouldShowGimmickFields("use_z_move")
            || shouldShowGimmickFields("dynamax")
            || shouldShowGimmickFields("gigantamax")
            || shouldShowGimmickFields("ultra_burst");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Component getAltTitle() {
        StringBuilder titleBuilder = new StringBuilder();
        for (String action : actions) {
            titleBuilder.append(Component.translatable("cobblemon_quests.actions." + action).getString()).append(" ");
        }
        if (actions.contains("have_registered") || actions.contains("register")) {
            titleBuilder.append("(").append(dexProgress).append(") ");
        }

        titleBuilder.append(amount).append("x ");
        if (shiny) {
            titleBuilder.append(Component.translatable("cobblemon_quests.task.shiny").getString()).append(" ");
        }
        for (String gender : genders) {
            titleBuilder.append(Component.translatable("cobblemon_quests.genders." + gender).getString()).append(" ");
        }
        for (String form : forms) {
            titleBuilder.append(Component.translatable("cobblemon_quests.forms." + form).getString()).append(" ");
        }
        for (String region : regions) {
            titleBuilder.append(Component.translatable("cobblemon_quests.regions." + region).getString()).append(" ");
        }
        for (String pokemonType : pokemonTypes) {
            titleBuilder.append(Component.translatable("cobblemon.type." + pokemonType).getString()).append(" ");
        }
        for (String nature : natures) {
            titleBuilder.append(Component.translatable(nature).getString()).append(" ");
        }
        if (pokemons.isEmpty()) {
            titleBuilder.append(Component.translatable("cobblemon_quests.task.pokemons").getString()).append(" ");
        } else {
            for (String pokemon : pokemons) {
                titleBuilder.append(Component.translatable("cobblemon.species." + pokemon.split(":")[1] + ".name").getString()).append(" ");
                if (pokemons.indexOf(pokemon) != pokemons.size() - 1) {
                    titleBuilder.append("or ");
                }
            }
        }
        for (String pokeballUsed : pokeBallsUsed) {
            if (pokeBallsUsed.indexOf(pokeballUsed) == 0) {
                titleBuilder.append("using a ");
            } else {
                titleBuilder.append("or ");
            }
            titleBuilder.append(Component.translatable("item." + pokeballUsed.replace(":", ".")).getString()).append(" ");
        }
        for (String dimension : dimensions) {
            titleBuilder.append("in ").append(dimension.split(":")[1].replace("_", " ")).append(" ");
        }
        for (String biome : biomes) {
            titleBuilder.append("in a ").append(biome.split(":")[1].replace("_", " ")).append(" biome ");
        }
        if (!(timeMin == 0 && timeMax == 24000)) {
            titleBuilder.append("between the time ").append(timeMin).append(" and ").append(timeMax).append(" ");
        }
        if (maxLevel != 0) {
            if (minLevel == maxLevel) {
                titleBuilder.append("at level ").append(minLevel).append(" ");
            } else {
                titleBuilder.append("between level ").append(minLevel).append(" and ").append(maxLevel).append(" ");
            }
        }

        return Component.literal(titleBuilder.toString().trim());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getAltIcon() {
        if (pokemons.isEmpty()) {
            return pokeBallIcon;
        }
        return getPokemonIcon(ResourceLocation.parse(pokemons.getFirst()));
    }


    public Icon getIconFromIdentifier(ResourceLocation ResourceLocation) {
        ItemStack itemStack = BuiltInRegistries.ITEM.get(ResourceLocation).getDefaultInstance();
        if (itemStack.isEmpty()) {
            return pokeBallIcon;
        } else {
            return ItemIcon.getItemIcon(itemStack);
        }

    }

    public Icon getPokemonIcon(ResourceLocation pokemon) {
        // TODO Figure out why the pokemon icon tint is so dark
        Item pokemonModelItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("cobblemon", "pokemon_model"));
        CompoundTag nbt = new CompoundTag();
        nbt.putString("species", pokemon.toString());
        ItemStack stack = new ItemStack(pokemonModelItem);
        PokemonItemComponent c = new PokemonItemComponent(pokemon, new HashSet<>(), new Vector4f(1, 1, 1, 1));
        stack.set(CobblemonItemComponents.POKEMON_ITEM, c);
        return ItemIcon.getItemIcon(stack);
        // Command to give player pokemon model:
        // give @s cobblemon:pokemon_model[cobblemon:pokemon_item={species:"cobblemon:<pokemon_name>",aspects:[]}]
    }

    public void increase(TeamData teamData, Pokemon pokemon, String executedAction, long progress, ServerPlayer player) {
        List<String> obtainingMethods = List.of("catch", "evolve-into", "trade_for", "obtain", "revive_fossil");
        if (actions.contains(executedAction) || (actions.contains("obtain") && obtainingMethods.contains(executedAction))) {
            if (CobblemonQuestsConfig.ignoredPokemon.contains(pokemon.getSpecies().toString().toLowerCase())) return;
            Level world = player.level();
            // Check region
            if (!regions.isEmpty()) {
                if (!regions.contains(pokemon.getSpecies().getLabels().toArray()[0].toString())) {
                    return;
                }
            }
            // Check the time of action
            if (!(timeMin == 0 && timeMax == 24000)) {
                long timeOfDay = world.getDayTime() % 24000;
                long actualMin = timeMin;
                long actualMax = timeMax;
                // Adjusts the time to account for the 24000 cycle
                if (timeMin > timeMax) {
                    actualMax = timeMax + 24000;
                    if (timeOfDay < timeMin) {
                        timeOfDay += 24000;
                    }
                }
                if (timeOfDay < actualMin || timeOfDay >= actualMax) {
                    return;
                }
            }

            if (maxLevel != 0) {
                if (pokemon.getLevel() > maxLevel || pokemon.getLevel() < minLevel) {
                    return;
                }
            }

            if (!pokeBallsUsed.isEmpty()) {
                if (!pokeBallsUsed.contains(pokemon.getCaughtBall().getName().toString())) {
                    return;
                }
            }
            // Check dimension
            if (!dimensions.isEmpty()) {
                if (!dimensions.contains(world.dimension().location().toString())) {
                    return;
                }
            }
            // Check biome
            if (!biomes.isEmpty()) {
                if (!biomes.contains(world.getBiome(player.blockPosition()).unwrapKey().get().location().toString())) {
                    return;
                }
            }
            // Check gender
            if (!genders.isEmpty()) {
                if (!genders.contains(pokemon.getGender().toString().toLowerCase())) {
                    return;
                }
            }
            // Check form
            if (!forms.isEmpty()) {
                boolean flag = forms.contains(pokemon.getForm().getName().toLowerCase());
                for (String aspect : pokemon.getAspects()) {
                    if (forms.contains(aspect)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) return;
            }

            // Check type
            if (!pokemonTypes.isEmpty()) {
                boolean flag = false;
                for (ElementalType type : pokemon.getTypes()) {
                    if (pokemonTypes.contains(type.getName().toLowerCase())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) return;
            }

            if (!natures.isEmpty()) {
                if (!natures.contains(pokemon.getNature().getDisplayName())) {
                    return;
                }
            }

            // Check shiny
            if (!pokemon.getShiny() && shiny) return;
            boolean shouldAddProgress = pokemons.stream().anyMatch(p -> p.split(":").length > 1 && p.split(":")[1].equals(pokemon.getSpecies().toString())) || pokemons.isEmpty();

            if (shouldAddProgress) {
                if (executedAction.equals("level_up_to")) {
                    if (teamData.getProgress(this) < progress) {
                        teamData.setProgress(this, progress);
                    }
                    return;
                }
                if (executedAction.equals("register")) {
                    if (dexProgress.equals("seen")) {
                        progress = (progress == 1) ? 0 : 1;
                    } else if (dexProgress.equals("caught")) {
                        progress = progress != 0 ? 0 : 1;
                    }
                }
                teamData.addProgress(this, progress);
            }
        }
    }

    public void increaseHaveRegistered(TeamData teamData, PokedexManager pokedexManager) {
        if (!this.actions.contains("have_registered")) return;
        if (teamData.isCompleted(this)) return;
        long progress = 0;
        Map<ResourceLocation, SpeciesDexRecord> dexRecords = pokedexManager.getSpeciesRecords();
        for (ResourceLocation record : dexRecords.keySet()) {
            SpeciesDexRecord dexRecord = dexRecords.get(record);
            if (!pokemons.isEmpty() && !pokemons.contains(record.toString())) continue;
            Set<String> aspects = dexRecord.getAspects();
            boolean flag = false;

            if (Objects.equals(dexProgress, "caught")) {
                if (!dexRecord.getKnowledge().equals(PokedexEntryProgress.CAUGHT)) {
                    continue;
                }
            }

            if (shiny) {
                if (!aspects.contains("shiny")) continue;
            }

            if (!genders.isEmpty()) {
                for (String gender : genders) {
                    if (aspects.contains(gender)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) continue;
                flag = false;
            }
            if (!forms.isEmpty()) {
                for (String form : forms) {
                    if (aspects.contains(form)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) continue;
                flag = false;
            }

            Species species = PokemonSpecies.getByIdentifier(record);
            if (species == null) continue;

            if (!regions.isEmpty()) {
                if (!regions.contains(species.getLabels().toArray()[0].toString())) {
                    continue;
                }
            }

            if (!pokemonTypes.isEmpty()) {
                for (ElementalType type : species.getTypes()) {
                    if (pokemonTypes.contains(type.getName().toLowerCase())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) continue;
            }
            progress += 1;
        }
        teamData.setProgress(this, progress);
    }

    // data is a string that should match an entry in the (comma separated) form field.
    public void increaseWoPokemon(TeamData teamData, String data, String executedAction, long progress) {
        if (actions.contains(executedAction) && (forms.contains(data) || forms.isEmpty())) {
            teamData.addProgress(this, progress);
        }
    }
}
