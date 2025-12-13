package cobblemonquestsextended.cobblemon_quests_extended.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Immutable domain model representing the conditional fields of a CobblemonTask.
 * This model encapsulates all 21 configurable fields used for quest task conditions.
 *
 * <p>Use the {@link Builder} to construct instances. All List fields are guaranteed
 * to be non-null and immutable after construction.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * CobblemonTaskModel model = CobblemonTaskModel.builder()
 *     .actions(List.of("catch", "defeat"))
 *     .amount(5)
 *     .shiny(true)
 *     .pokemons(List.of("cobblemon:pikachu"))
 *     .build();
 * }</pre>
 */
public final class CobblemonTaskModel {

    // Action selection
    private final List<String> actions;

    // Basic conditions
    private final long amount;
    private final boolean shiny;
    private final List<String> pokemons;
    private final List<String> pokemonTypes;
    private final List<String> natures;
    private final List<String> regions;
    private final List<String> genders;
    private final List<String> forms;

    // Location and time conditions
    private final List<String> biomes;
    private final List<String> dimensions;
    private final List<String> pokeBallsUsed;
    private final long timeMin;
    private final long timeMax;

    // Level conditions
    private final int minLevel;
    private final int maxLevel;

    // Pokedex progress condition
    private final String dexProgress;

    // Gimmick-specific conditions (Mega Showdown integration)
    private final List<String> teraTypes;
    private final List<String> megaForms;
    private final List<String> zCrystals;
    private final List<String> dynamaxTypes;

    /**
     * Private constructor - use {@link Builder} for construction.
     */
    private CobblemonTaskModel(Builder builder) {
        this.actions = List.copyOf(builder.actions);
        this.amount = builder.amount;
        this.shiny = builder.shiny;
        this.pokemons = List.copyOf(builder.pokemons);
        this.pokemonTypes = List.copyOf(builder.pokemonTypes);
        this.natures = List.copyOf(builder.natures);
        this.regions = List.copyOf(builder.regions);
        this.genders = List.copyOf(builder.genders);
        this.forms = List.copyOf(builder.forms);
        this.biomes = List.copyOf(builder.biomes);
        this.dimensions = List.copyOf(builder.dimensions);
        this.pokeBallsUsed = List.copyOf(builder.pokeBallsUsed);
        this.timeMin = builder.timeMin;
        this.timeMax = builder.timeMax;
        this.minLevel = builder.minLevel;
        this.maxLevel = builder.maxLevel;
        this.dexProgress = Objects.requireNonNull(builder.dexProgress, "dexProgress cannot be null");
        this.teraTypes = List.copyOf(builder.teraTypes);
        this.megaForms = List.copyOf(builder.megaForms);
        this.zCrystals = List.copyOf(builder.zCrystals);
        this.dynamaxTypes = List.copyOf(builder.dynamaxTypes);
    }

    // ===== Getters (return immutable views) =====

    /**
     * Returns the list of action types for this task (e.g., "catch", "defeat").
     * @return an immutable list of action identifiers
     */
    public List<String> getActions() {
        return actions;
    }

    /**
     * Returns the required amount/count for task completion.
     * @return the amount (default: 1)
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Returns whether the task requires shiny Pokemon.
     * @return true if shiny Pokemon required
     */
    public boolean isShiny() {
        return shiny;
    }

    /**
     * Returns the list of specific Pokemon species required.
     * @return an immutable list of Pokemon identifiers (e.g., "cobblemon:pikachu")
     */
    public List<String> getPokemons() {
        return pokemons;
    }

    /**
     * Returns the list of required Pokemon elemental types.
     * @return an immutable list of type names (e.g., "fire", "water")
     */
    public List<String> getPokemonTypes() {
        return pokemonTypes;
    }

    /**
     * Returns the list of required Pokemon natures.
     * @return an immutable list of nature names
     */
    public List<String> getNatures() {
        return natures;
    }

    /**
     * Returns the list of required Pokemon regions/generations.
     * @return an immutable list of region identifiers
     */
    public List<String> getRegions() {
        return regions;
    }

    /**
     * Returns the list of required Pokemon genders.
     * @return an immutable list of gender identifiers (e.g., "male", "female", "genderless")
     */
    public List<String> getGenders() {
        return genders;
    }

    /**
     * Returns the list of required Pokemon forms/variants.
     * @return an immutable list of form identifiers (e.g., "alolan", "galarian")
     */
    public List<String> getForms() {
        return forms;
    }

    /**
     * Returns the list of required biomes for the task.
     * @return an immutable list of biome identifiers
     */
    public List<String> getBiomes() {
        return biomes;
    }

    /**
     * Returns the list of required dimensions for the task.
     * @return an immutable list of dimension identifiers
     */
    public List<String> getDimensions() {
        return dimensions;
    }

    /**
     * Returns the list of required Poke Balls for catching.
     * @return an immutable list of Poke Ball identifiers
     */
    public List<String> getPokeBallsUsed() {
        return pokeBallsUsed;
    }

    /**
     * Returns the minimum time of day (in ticks) for the task.
     * @return the minimum time (0-24000, default: 0)
     */
    public long getTimeMin() {
        return timeMin;
    }

    /**
     * Returns the maximum time of day (in ticks) for the task.
     * @return the maximum time (0-24000, default: 24000)
     */
    public long getTimeMax() {
        return timeMax;
    }

    /**
     * Returns the minimum Pokemon level required.
     * @return the minimum level (0 means no restriction, default: 0)
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * Returns the maximum Pokemon level required.
     * @return the maximum level (0 means no restriction, default: 0)
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Returns the required Pokedex progress type.
     * @return the dex progress type ("seen" or "caught", default: "seen")
     */
    public String getDexProgress() {
        return dexProgress;
    }

    /**
     * Returns the list of required Tera types (Terastallization).
     * @return an immutable list of Tera type identifiers
     */
    public List<String> getTeraTypes() {
        return teraTypes;
    }

    /**
     * Returns the list of required Mega Evolution forms.
     * @return an immutable list of Mega form identifiers
     */
    public List<String> getMegaForms() {
        return megaForms;
    }

    /**
     * Returns the list of required Z-Crystals.
     * @return an immutable list of Z-Crystal identifiers
     */
    public List<String> getZCrystals() {
        return zCrystals;
    }

    /**
     * Returns the list of required Dynamax/Gigantamax types.
     * @return an immutable list of Dynamax type identifiers
     */
    public List<String> getDynamaxTypes() {
        return dynamaxTypes;
    }

    // ===== Builder Support =====

    /**
     * Creates a new Builder with default values.
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new Builder pre-populated with values from this model.
     * @return a new Builder instance with this model's values
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    // ===== Object Methods =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CobblemonTaskModel that = (CobblemonTaskModel) o;
        return amount == that.amount
                && shiny == that.shiny
                && timeMin == that.timeMin
                && timeMax == that.timeMax
                && minLevel == that.minLevel
                && maxLevel == that.maxLevel
                && Objects.equals(actions, that.actions)
                && Objects.equals(pokemons, that.pokemons)
                && Objects.equals(pokemonTypes, that.pokemonTypes)
                && Objects.equals(natures, that.natures)
                && Objects.equals(regions, that.regions)
                && Objects.equals(genders, that.genders)
                && Objects.equals(forms, that.forms)
                && Objects.equals(biomes, that.biomes)
                && Objects.equals(dimensions, that.dimensions)
                && Objects.equals(pokeBallsUsed, that.pokeBallsUsed)
                && Objects.equals(dexProgress, that.dexProgress)
                && Objects.equals(teraTypes, that.teraTypes)
                && Objects.equals(megaForms, that.megaForms)
                && Objects.equals(zCrystals, that.zCrystals)
                && Objects.equals(dynamaxTypes, that.dynamaxTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                actions, amount, shiny, pokemons, pokemonTypes, natures, regions,
                genders, forms, biomes, dimensions, pokeBallsUsed, timeMin, timeMax,
                minLevel, maxLevel, dexProgress, teraTypes, megaForms, zCrystals, dynamaxTypes
        );
    }

    @Override
    public String toString() {
        return "CobblemonTaskModel{" +
                "actions=" + actions +
                ", amount=" + amount +
                ", shiny=" + shiny +
                ", pokemons=" + pokemons +
                ", pokemonTypes=" + pokemonTypes +
                ", natures=" + natures +
                ", regions=" + regions +
                ", genders=" + genders +
                ", forms=" + forms +
                ", biomes=" + biomes +
                ", dimensions=" + dimensions +
                ", pokeBallsUsed=" + pokeBallsUsed +
                ", timeMin=" + timeMin +
                ", timeMax=" + timeMax +
                ", minLevel=" + minLevel +
                ", maxLevel=" + maxLevel +
                ", dexProgress='" + dexProgress + '\'' +
                ", teraTypes=" + teraTypes +
                ", megaForms=" + megaForms +
                ", zCrystals=" + zCrystals +
                ", dynamaxTypes=" + dynamaxTypes +
                '}';
    }

    // ===== Builder Class =====

    /**
     * Builder for constructing {@link CobblemonTaskModel} instances.
     * All fields have sensible defaults matching {@code CobblemonTask}.
     */
    public static final class Builder {

        // Action selection
        private List<String> actions = new ArrayList<>();

        // Basic conditions
        private long amount = 1L;
        private boolean shiny = false;
        private List<String> pokemons = new ArrayList<>();
        private List<String> pokemonTypes = new ArrayList<>();
        private List<String> natures = new ArrayList<>();
        private List<String> regions = new ArrayList<>();
        private List<String> genders = new ArrayList<>();
        private List<String> forms = new ArrayList<>();

        // Location and time conditions
        private List<String> biomes = new ArrayList<>();
        private List<String> dimensions = new ArrayList<>();
        private List<String> pokeBallsUsed = new ArrayList<>();
        private long timeMin = 0L;
        private long timeMax = 24000L;

        // Level conditions
        private int minLevel = 0;
        private int maxLevel = 0;

        // Pokedex progress condition
        private String dexProgress = "seen";

        // Gimmick-specific conditions
        private List<String> teraTypes = new ArrayList<>();
        private List<String> megaForms = new ArrayList<>();
        private List<String> zCrystals = new ArrayList<>();
        private List<String> dynamaxTypes = new ArrayList<>();

        /**
         * Creates a new Builder with default values.
         */
        public Builder() {
        }

        /**
         * Creates a new Builder pre-populated with values from an existing model.
         * @param model the model to copy values from
         */
        private Builder(CobblemonTaskModel model) {
            this.actions = new ArrayList<>(model.actions);
            this.amount = model.amount;
            this.shiny = model.shiny;
            this.pokemons = new ArrayList<>(model.pokemons);
            this.pokemonTypes = new ArrayList<>(model.pokemonTypes);
            this.natures = new ArrayList<>(model.natures);
            this.regions = new ArrayList<>(model.regions);
            this.genders = new ArrayList<>(model.genders);
            this.forms = new ArrayList<>(model.forms);
            this.biomes = new ArrayList<>(model.biomes);
            this.dimensions = new ArrayList<>(model.dimensions);
            this.pokeBallsUsed = new ArrayList<>(model.pokeBallsUsed);
            this.timeMin = model.timeMin;
            this.timeMax = model.timeMax;
            this.minLevel = model.minLevel;
            this.maxLevel = model.maxLevel;
            this.dexProgress = model.dexProgress;
            this.teraTypes = new ArrayList<>(model.teraTypes);
            this.megaForms = new ArrayList<>(model.megaForms);
            this.zCrystals = new ArrayList<>(model.zCrystals);
            this.dynamaxTypes = new ArrayList<>(model.dynamaxTypes);
        }

        /**
         * Creates a new Builder pre-populated with values from an existing model.
         * @param model the model to copy values from
         * @return a new Builder instance
         */
        public static Builder from(CobblemonTaskModel model) {
            return new Builder(model);
        }

        // ===== Fluent Setters =====

        /**
         * Sets the action types for this task.
         * @param actions the list of action identifiers
         * @return this builder
         */
        public Builder actions(List<String> actions) {
            this.actions = actions != null ? new ArrayList<>(actions) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the required amount for task completion.
         * @param amount the amount (must be positive)
         * @return this builder
         */
        public Builder amount(long amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Sets whether shiny Pokemon are required.
         * @param shiny true if shiny Pokemon required
         * @return this builder
         */
        public Builder shiny(boolean shiny) {
            this.shiny = shiny;
            return this;
        }

        /**
         * Sets the list of required Pokemon species.
         * @param pokemons the list of Pokemon identifiers
         * @return this builder
         */
        public Builder pokemons(List<String> pokemons) {
            this.pokemons = pokemons != null ? new ArrayList<>(pokemons) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the list of required Pokemon types.
         * @param pokemonTypes the list of type names
         * @return this builder
         */
        public Builder pokemonTypes(List<String> pokemonTypes) {
            this.pokemonTypes = pokemonTypes != null ? new ArrayList<>(pokemonTypes) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the list of required natures.
         * @param natures the list of nature names
         * @return this builder
         */
        public Builder natures(List<String> natures) {
            this.natures = natures != null ? new ArrayList<>(natures) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the list of required regions.
         * @param regions the list of region identifiers
         * @return this builder
         */
        public Builder regions(List<String> regions) {
            this.regions = regions != null ? new ArrayList<>(regions) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the list of required genders.
         * @param genders the list of gender identifiers
         * @return this builder
         */
        public Builder genders(List<String> genders) {
            this.genders = genders != null ? new ArrayList<>(genders) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the list of required forms/variants.
         * @param forms the list of form identifiers
         * @return this builder
         */
        public Builder forms(List<String> forms) {
            this.forms = forms != null ? new ArrayList<>(forms) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the list of required biomes.
         * @param biomes the list of biome identifiers
         * @return this builder
         */
        public Builder biomes(List<String> biomes) {
            this.biomes = biomes != null ? new ArrayList<>(biomes) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the list of required dimensions.
         * @param dimensions the list of dimension identifiers
         * @return this builder
         */
        public Builder dimensions(List<String> dimensions) {
            this.dimensions = dimensions != null ? new ArrayList<>(dimensions) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the list of required Poke Balls.
         * @param pokeBallsUsed the list of Poke Ball identifiers
         * @return this builder
         */
        public Builder pokeBallsUsed(List<String> pokeBallsUsed) {
            this.pokeBallsUsed = pokeBallsUsed != null ? new ArrayList<>(pokeBallsUsed) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the minimum time of day (in ticks).
         * @param timeMin the minimum time (0-24000)
         * @return this builder
         */
        public Builder timeMin(long timeMin) {
            this.timeMin = timeMin;
            return this;
        }

        /**
         * Sets the maximum time of day (in ticks).
         * @param timeMax the maximum time (0-24000)
         * @return this builder
         */
        public Builder timeMax(long timeMax) {
            this.timeMax = timeMax;
            return this;
        }

        /**
         * Sets the minimum Pokemon level.
         * @param minLevel the minimum level (0 means no restriction)
         * @return this builder
         */
        public Builder minLevel(int minLevel) {
            this.minLevel = minLevel;
            return this;
        }

        /**
         * Sets the maximum Pokemon level.
         * @param maxLevel the maximum level (0 means no restriction)
         * @return this builder
         */
        public Builder maxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        /**
         * Sets the required Pokedex progress type.
         * @param dexProgress the progress type ("seen" or "caught")
         * @return this builder
         */
        public Builder dexProgress(String dexProgress) {
            this.dexProgress = dexProgress != null ? dexProgress : "seen";
            return this;
        }

        /**
         * Sets the list of required Tera types.
         * @param teraTypes the list of Tera type identifiers
         * @return this builder
         */
        public Builder teraTypes(List<String> teraTypes) {
            this.teraTypes = teraTypes != null ? new ArrayList<>(teraTypes) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the list of required Mega forms.
         * @param megaForms the list of Mega form identifiers
         * @return this builder
         */
        public Builder megaForms(List<String> megaForms) {
            this.megaForms = megaForms != null ? new ArrayList<>(megaForms) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the list of required Z-Crystals.
         * @param zCrystals the list of Z-Crystal identifiers
         * @return this builder
         */
        public Builder zCrystals(List<String> zCrystals) {
            this.zCrystals = zCrystals != null ? new ArrayList<>(zCrystals) : new ArrayList<>();
            return this;
        }

        /**
         * Sets the list of required Dynamax types.
         * @param dynamaxTypes the list of Dynamax type identifiers
         * @return this builder
         */
        public Builder dynamaxTypes(List<String> dynamaxTypes) {
            this.dynamaxTypes = dynamaxTypes != null ? new ArrayList<>(dynamaxTypes) : new ArrayList<>();
            return this;
        }

        /**
         * Builds and returns the immutable {@link CobblemonTaskModel}.
         * @return a new CobblemonTaskModel instance
         */
        public CobblemonTaskModel build() {
            return new CobblemonTaskModel(this);
        }
    }
}
