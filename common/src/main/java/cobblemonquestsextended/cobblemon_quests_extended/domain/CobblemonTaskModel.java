package cobblemonquestsextended.cobblemon_quests_extended.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a Cobblemon quest task.
 * Pure POJO with no FTB Library dependencies.
 */
public class CobblemonTaskModel {
    // Task identity
    private long id;

    // Basic properties
    private long amount = 1L;
    private boolean shiny = false;
    private long timeMin = 0L;
    private long timeMax = 24000L;
    private int minLevel = 0;
    private int maxLevel = 0;
    private String dexProgress = "seen";

    // List properties
    private List<String> actions = new ArrayList<>();
    private List<String> biomes = new ArrayList<>();
    private List<String> dimensions = new ArrayList<>();
    private List<String> forms = new ArrayList<>();
    private List<String> genders = new ArrayList<>();
    private List<String> pokeBallsUsed = new ArrayList<>();
    private List<String> pokemons = new ArrayList<>();
    private List<String> pokemonTypes = new ArrayList<>();
    private List<String> regions = new ArrayList<>();
    private List<String> natures = new ArrayList<>();
    private List<String> teraTypes = new ArrayList<>();
    private List<String> megaForms = new ArrayList<>();
    private List<String> zCrystals = new ArrayList<>();
    private List<String> dynamaxTypes = new ArrayList<>();

    // Private constructor - use Builder
    private CobblemonTaskModel() {}

    // All getters (no setters - immutable after build)
    public long getId() { return id; }
    public long getAmount() { return amount; }
    public boolean isShiny() { return shiny; }
    public long getTimeMin() { return timeMin; }
    public long getTimeMax() { return timeMax; }
    public int getMinLevel() { return minLevel; }
    public int getMaxLevel() { return maxLevel; }
    public String getDexProgress() { return dexProgress; }
    public List<String> getActions() { return List.copyOf(actions); }
    public List<String> getBiomes() { return List.copyOf(biomes); }
    public List<String> getDimensions() { return List.copyOf(dimensions); }
    public List<String> getForms() { return List.copyOf(forms); }
    public List<String> getGenders() { return List.copyOf(genders); }
    public List<String> getPokeBallsUsed() { return List.copyOf(pokeBallsUsed); }
    public List<String> getPokemons() { return List.copyOf(pokemons); }
    public List<String> getPokemonTypes() { return List.copyOf(pokemonTypes); }
    public List<String> getRegions() { return List.copyOf(regions); }
    public List<String> getNatures() { return List.copyOf(natures); }
    public List<String> getTeraTypes() { return List.copyOf(teraTypes); }
    public List<String> getMegaForms() { return List.copyOf(megaForms); }
    public List<String> getZCrystals() { return List.copyOf(zCrystals); }
    public List<String> getDynamaxTypes() { return List.copyOf(dynamaxTypes); }

    // Static factory for builder
    public static Builder builder() { return new Builder(); }

    // Convert from CobblemonTask
    public static CobblemonTaskModel fromTask(cobblemonquestsextended.cobblemon_quests_extended.tasks.CobblemonTask task) {
        return builder()
            .id(task.id)
            .amount(task.amount)
            .shiny(task.shiny)
            .timeMin(task.timeMin)
            .timeMax(task.timeMax)
            .minLevel(task.minLevel)
            .maxLevel(task.maxLevel)
            .dexProgress(task.dexProgress)
            .actions(task.actions)
            .biomes(task.biomes)
            .dimensions(task.dimensions)
            .forms(task.forms)
            .genders(task.genders)
            .pokeBallsUsed(task.pokeBallsUsed)
            .pokemons(task.pokemons)
            .pokemonTypes(task.pokemonTypes)
            .regions(task.regions)
            .natures(task.natures)
            .teraTypes(task.teraTypes)
            .megaForms(task.megaForms)
            .zCrystals(task.zCrystals)
            .dynamaxTypes(task.dynamaxTypes)
            .build();
    }

    // Apply to CobblemonTask
    public void applyToTask(cobblemonquestsextended.cobblemon_quests_extended.tasks.CobblemonTask task) {
        task.amount = this.amount;
        task.shiny = this.shiny;
        task.timeMin = this.timeMin;
        task.timeMax = this.timeMax;
        task.minLevel = this.minLevel;
        task.maxLevel = this.maxLevel;
        task.dexProgress = this.dexProgress;
        task.actions = new ArrayList<>(this.actions);
        task.biomes = new ArrayList<>(this.biomes);
        task.dimensions = new ArrayList<>(this.dimensions);
        task.forms = new ArrayList<>(this.forms);
        task.genders = new ArrayList<>(this.genders);
        task.pokeBallsUsed = new ArrayList<>(this.pokeBallsUsed);
        task.pokemons = new ArrayList<>(this.pokemons);
        task.pokemonTypes = new ArrayList<>(this.pokemonTypes);
        task.regions = new ArrayList<>(this.regions);
        task.natures = new ArrayList<>(this.natures);
        task.teraTypes = new ArrayList<>(this.teraTypes);
        task.megaForms = new ArrayList<>(this.megaForms);
        task.zCrystals = new ArrayList<>(this.zCrystals);
        task.dynamaxTypes = new ArrayList<>(this.dynamaxTypes);
    }

    // Builder class with all fluent setters
    public static class Builder {
        private final CobblemonTaskModel model = new CobblemonTaskModel();

        public Builder id(long id) { model.id = id; return this; }
        public Builder amount(long amount) { model.amount = amount; return this; }
        public Builder shiny(boolean shiny) { model.shiny = shiny; return this; }
        public Builder timeMin(long timeMin) { model.timeMin = timeMin; return this; }
        public Builder timeMax(long timeMax) { model.timeMax = timeMax; return this; }
        public Builder minLevel(int minLevel) { model.minLevel = minLevel; return this; }
        public Builder maxLevel(int maxLevel) { model.maxLevel = maxLevel; return this; }
        public Builder dexProgress(String dexProgress) { model.dexProgress = dexProgress; return this; }

        public Builder actions(List<String> actions) { model.actions = new ArrayList<>(actions); return this; }
        public Builder biomes(List<String> biomes) { model.biomes = new ArrayList<>(biomes); return this; }
        public Builder dimensions(List<String> dimensions) { model.dimensions = new ArrayList<>(dimensions); return this; }
        public Builder forms(List<String> forms) { model.forms = new ArrayList<>(forms); return this; }
        public Builder genders(List<String> genders) { model.genders = new ArrayList<>(genders); return this; }
        public Builder pokeBallsUsed(List<String> pokeBallsUsed) { model.pokeBallsUsed = new ArrayList<>(pokeBallsUsed); return this; }
        public Builder pokemons(List<String> pokemons) { model.pokemons = new ArrayList<>(pokemons); return this; }
        public Builder pokemonTypes(List<String> pokemonTypes) { model.pokemonTypes = new ArrayList<>(pokemonTypes); return this; }
        public Builder regions(List<String> regions) { model.regions = new ArrayList<>(regions); return this; }
        public Builder natures(List<String> natures) { model.natures = new ArrayList<>(natures); return this; }
        public Builder teraTypes(List<String> teraTypes) { model.teraTypes = new ArrayList<>(teraTypes); return this; }
        public Builder megaForms(List<String> megaForms) { model.megaForms = new ArrayList<>(megaForms); return this; }
        public Builder zCrystals(List<String> zCrystals) { model.zCrystals = new ArrayList<>(zCrystals); return this; }
        public Builder dynamaxTypes(List<String> dynamaxTypes) { model.dynamaxTypes = new ArrayList<>(dynamaxTypes); return this; }

        // Convenience methods for adding single items
        public Builder addAction(String action) { model.actions.add(action); return this; }
        public Builder addBiome(String biome) { model.biomes.add(biome); return this; }
        public Builder addDimension(String dimension) { model.dimensions.add(dimension); return this; }
        public Builder addForm(String form) { model.forms.add(form); return this; }
        public Builder addGender(String gender) { model.genders.add(gender); return this; }
        public Builder addPokeBall(String pokeBall) { model.pokeBallsUsed.add(pokeBall); return this; }
        public Builder addPokemon(String pokemon) { model.pokemons.add(pokemon); return this; }
        public Builder addPokemonType(String type) { model.pokemonTypes.add(type); return this; }
        public Builder addRegion(String region) { model.regions.add(region); return this; }
        public Builder addNature(String nature) { model.natures.add(nature); return this; }
        public Builder addTeraType(String teraType) { model.teraTypes.add(teraType); return this; }
        public Builder addMegaForm(String megaForm) { model.megaForms.add(megaForm); return this; }
        public Builder addZCrystal(String zCrystal) { model.zCrystals.add(zCrystal); return this; }
        public Builder addDynamaxType(String dynamaxType) { model.dynamaxTypes.add(dynamaxType); return this; }

        public CobblemonTaskModel build() {
            return model;
        }
    }
}
