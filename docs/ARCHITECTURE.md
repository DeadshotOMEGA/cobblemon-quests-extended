# Cobblemon Quests Extended - Architecture Documentation

## Table of Contents

1. [Overview](#overview)
2. [Multi-Loader Architecture](#multi-loader-architecture)
3. [Module Structure](#module-structure)
4. [Core Systems](#core-systems)
   - [Action Registry](#action-registry)
   - [Event Handling](#event-handling)
   - [Task System](#task-system)
5. [Soft Dependency Pattern](#soft-dependency-pattern)
6. [Data Flow](#data-flow)
7. [Extension Points](#extension-points)
8. [Configuration System](#configuration-system)

---

## Overview

Cobblemon Quests Extended is a multi-loader Minecraft mod that integrates the Cobblemon mod with FTB Quests, enabling quest creators to design Pokemon-based objectives. The mod is built using the Architectury framework to support both Fabric and NeoForge mod loaders from a single codebase.

### Key Features

- **Multi-loader support**: Single codebase for Fabric and NeoForge
- **Action Registry**: Extensible system for registering quest-triggerable actions
- **Event-driven architecture**: Listens to Cobblemon events and maps them to quest progress
- **Soft dependencies**: Graceful integration with optional mods (e.g., Mega Showdown)
- **Type-safe design**: Uses records and immutable data structures

---

## Multi-Loader Architecture

The project uses **Architectury** to maintain a single codebase that compiles for multiple mod loaders.

### Architecture Pattern

```
┌─────────────────────────────────────────┐
│          Common Module                  │
│  (Platform-independent logic)           │
│  - Core systems                         │
│  - Action Registry                      │
│  - Event handlers                       │
│  - Task implementations                 │
└─────────────────────────────────────────┘
            ▲           ▲
            │           │
    ┌───────┴───┐   ┌───┴────────┐
    │   Fabric  │   │  NeoForge  │
    │   Module  │   │   Module   │
    │           │   │            │
    │ - Entry   │   │ - Entry    │
    │   point   │   │   point    │
    │ - Loader  │   │ - Loader   │
    │   specific│   │   specific │
    └───────────┘   └────────────┘
```

### Why Architectury?

- **Code reuse**: 95%+ of logic is shared in the common module
- **Type safety**: Platform-specific APIs are abstracted
- **Maintenance**: Changes propagate to both loaders automatically
- **Performance**: No runtime overhead compared to single-loader mods

---

## Module Structure

### Directory Layout

```
cobblemon-quests-extended/
├── common/                          # Platform-independent code
│   └── src/main/java/
│       └── cobblemonquestsextended/cobblemon_quests_extended/
│           ├── CobblemonQuests.java           # Main entry point
│           ├── registry/                      # Action registry system
│           │   ├── ActionRegistry.java        # Central action registry
│           │   ├── ActionCategory.java        # Action categorization
│           │   └── ActionDefinition.java      # Action metadata
│           ├── events/                        # Event handling
│           │   └── CobblemonQuestsEventHandler.java
│           ├── tasks/                         # Quest task implementations
│           │   ├── CobblemonTask.java         # Main task type
│           │   └── PokemonTaskTypes.java      # Task type registration
│           ├── integrations/                  # Soft dependency integrations
│           │   └── megashowdown/
│           │       ├── MegaShowdownIntegration.java
│           │       ├── MegaShowdownDetector.java
│           │       └── MegaShowdownEventHandler.java
│           └── config/                        # Configuration system
│               └── CobblemonQuestsConfig.java
├── fabric/                          # Fabric-specific code
│   └── src/main/java/
│       └── cobblemonquestsextended/cobblemon_quests_extended/fabric/
│           └── Cobblemon_QuestsFabric.java    # Fabric entry point
└── neoforge/                        # NeoForge-specific code
    └── src/main/java/
        └── cobblemonquestsextended/cobblemon_quests_extended/neoforge/
            └── Cobblemon_QuestsNeoForge.java  # NeoForge entry point
```

### Module Responsibilities

| Module | Responsibility |
|--------|----------------|
| **common** | All core logic, event handling, quest tasks, action registry |
| **fabric** | Fabric mod initialization, loader-specific registration |
| **neoforge** | NeoForge mod initialization, loader-specific registration |

---

## Core Systems

### Action Registry

The Action Registry is the heart of the extensibility system. It provides a centralized, thread-safe registry for all quest-triggerable actions.

#### Location
`/common/src/main/java/cobblemonquestsextended/cobblemon_quests_extended/registry/`

#### Key Components

**1. ActionDefinition (Record)**

Immutable metadata for an action type:

```java
public record ActionDefinition(
    String id,                  // e.g., "catch", "defeat"
    String translationKey,      // For i18n
    boolean requiresPokemon,    // Whether action needs Pokemon parameter
    ActionCategory category     // Organizational category
)
```

**2. ActionCategory (Enum)**

Categories for organizing actions:

- `CATCH` - Obtaining Pokemon (catch, hatch, reel, etc.)
- `BATTLE` - Combat-related (defeat, faint, kill)
- `EVOLUTION` - Evolution and form changes
- `TRADE` - Trading Pokemon
- `POKEDEX` - Pokedex registration
- `GIMMICK` - Battle gimmicks (mega evolution, terastallization, Z-moves, dynamax)
- `OTHER` - Miscellaneous actions

**3. ActionRegistry (Singleton)**

Thread-safe registry using `ConcurrentHashMap`:

```java
public final class ActionRegistry {
    private static final Map<String, ActionDefinition> ACTIONS = new ConcurrentHashMap<>();

    // Registration methods
    public static boolean register(String actionId, ActionDefinition definition)
    public static void registerOrReplace(String actionId, ActionDefinition definition)

    // Query methods
    public static Optional<ActionDefinition> getAction(String actionId)
    public static List<ActionDefinition> getActionsByCategory(ActionCategory category)
}
```

#### Built-in Actions

The registry includes 40+ built-in actions registered in the static initializer:

| Category | Example Actions |
|----------|----------------|
| CATCH | `catch`, `obtain`, `hatch_egg`, `reel`, `select_starter`, `revive_fossil` |
| BATTLE | `defeat`, `defeat_player`, `defeat_npc`, `kill`, `faint_pokemon` |
| EVOLUTION | `evolve`, `evolve_into`, `change_form` |
| TRADE | `trade_away`, `trade_for` |
| POKEDEX | `scan`, `register`, `have_registered` |
| GIMMICK | `mega_evolve`, `terastallize`, `use_z_move`, `dynamax`, `gigantamax`, `ultra_burst` |
| OTHER | `level_up`, `level_up_to`, `release`, `throw_ball`, `send_out`, `heal` |

#### How It Works

1. **Initialization**: Static initializer registers all built-in actions at class load time
2. **Thread Safety**: Uses `ConcurrentHashMap` for concurrent access
3. **Extensibility**: Other mods can register custom actions via the public API
4. **Validation**: Prevents null entries and duplicate registrations

---

### Event Handling

The event handling system bridges Cobblemon's game events to quest progress tracking.

#### Location
`/common/src/main/java/cobblemonquestsextended/cobblemon_quests_extended/events/CobblemonQuestsEventHandler.java`

#### Architecture

```
Cobblemon Events → Event Handler → Task Processor → FTB Quests
```

#### Key Features

**1. Event Subscription**

Uses Cobblemon's event system with `Priority.LOWEST` to ensure events are processed after other handlers:

```java
public CobblemonQuestsEventHandler init() {
    CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, this::pokemonCatchEvent);
    CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.LOWEST, this::pokemonBattleVictory);
    CobblemonEvents.EVOLUTION_COMPLETE.subscribe(Priority.LOWEST, this::pokemonEvolutionComplete);
    // ... 20+ more event subscriptions
    return this;
}
```

**2. Event Mapping**

Each Cobblemon event is mapped to one or more quest actions:

| Cobblemon Event | Quest Action(s) |
|----------------|-----------------|
| `POKEMON_CAPTURED` | `catch`, `obtain` |
| `STARTER_CHOSEN` | `select_starter`, `catch` |
| `EVOLUTION_COMPLETE` | `evolve_into`, `catch` (for new form) |
| `EVOLUTION_ACCEPTED` | `evolve` |
| `BATTLE_VICTORY` | `defeat`, `defeat_player`, `defeat_npc` |
| `BATTLE_FAINTED` | `faint_pokemon` |
| `LEVEL_UP_EVENT` | `level_up`, `level_up_to` |
| `MEGA_EVOLUTION` | `mega_evolve` |
| `TERASTALLIZATION` | `terastallize` |
| `ZPOWER_USED` | `use_z_move` |
| `FORME_CHANGE` | `change_form` |
| `TRADE_EVENT_POST` | `trade_away`, `trade_for` |
| `FOSSIL_REVIVED` | `revive_fossil` |
| `BOBBER_SPAWN_POKEMON_POST` | `reel` |
| `POKEMON_SCANNED` | `scan` |
| `POKEDEX_DATA_CHANGED_POST` | `register`, `have_registered` |
| `THROWN_POKEBALL_HIT` | `throw_ball` |
| `POKEMON_SENT_POST` | `send_out` |
| `HATCH_EGG_POST` | `hatch_egg` |
| `POKEMON_RELEASED_EVENT_PRE` | `release` |

**3. Task Processing**

Events are processed through two main methods:

```java
// For Pokemon-based actions
public void processTasksForTeam(Pokemon pokemon, String action, long amount, ServerPlayer player)

// For non-Pokemon actions (e.g., NPC defeats)
public void processTasksForTeam(String data, String action, long amount, ServerPlayer player)
```

**4. Deduplication**

Prevents double-counting when multiple events fire for the same action:

```java
private UUID lastPokemonUuid = null;  // Track last processed Pokemon

// In battle victory handler:
if (actor.getPokemonList().getFirst().getEffectedPokemon().getUuid() == lastPokemonUuid) return;
```

**5. Error Handling**

All event handlers are wrapped in try-catch blocks to prevent crashes:

```java
try {
    // Process event
} catch (Exception e) {
    CobblemonQuests.LOGGER.warning("Error processing event: " + Arrays.toString(e.getStackTrace()));
}
```

---

### Task System

The task system implements the actual quest objective logic.

#### Location
`/common/src/main/java/cobblemonquestsextended/cobblemon_quests_extended/tasks/`

#### CobblemonTask

Extends FTB Quests' `Task` class to provide Pokemon-specific quest objectives.

**Key Fields:**

```java
public class CobblemonTask extends Task {
    // Progress tracking
    public long amount = 1L;

    // Pokemon filters
    public ArrayList<String> pokemons = new ArrayList<>();
    public ArrayList<String> forms = new ArrayList<>();
    public ArrayList<String> genders = new ArrayList<>();
    public ArrayList<String> pokemonTypes = new ArrayList<>();
    public ArrayList<String> natures = new ArrayList<>();
    public boolean shiny = false;
    public int minLevel = 0;
    public int maxLevel = 0;

    // Action filters
    public ArrayList<String> actions = new ArrayList<>();

    // Environmental filters
    public ArrayList<String> biomes = new ArrayList<>();
    public ArrayList<String> dimensions = new ArrayList<>();
    public ArrayList<String> regions = new ArrayList<>();
    public long timeMin = 0;
    public long timeMax = 24000;

    // Capture filters
    public ArrayList<String> pokeBallsUsed = new ArrayList<>();

    // Pokedex filters
    public String dexProgress = "seen";

    // Gimmick filters (for battle mechanics)
    public ArrayList<String> teraTypes = new ArrayList<>();
    public ArrayList<String> megaForms = new ArrayList<>();
    public ArrayList<String> zCrystals = new ArrayList<>();
    public ArrayList<String> dynamaxTypes = new ArrayList<>();
}
```

**Task Validation:**

The task validates Pokemon against all specified conditions before incrementing progress. This allows for highly specific quests like:
- "Catch a shiny Charizard with a Master Ball in the Desert biome during the day"
- "Defeat 10 Water-type Pokemon between levels 30-40"
- "Evolve a Timid nature Eevee into Espeon"

---

## Soft Dependency Pattern

The mod uses a sophisticated soft dependency pattern to integrate with optional mods without requiring them to be installed.

### Location
`/common/src/main/java/cobblemonquestsextended/cobblemon_quests_extended/integrations/megashowdown/`

### How It Works

#### 1. Detection Phase

**MegaShowdownDetector.java**

```java
public static boolean isLoaded() {
    try {
        Class.forName("com.github.yajatkaul.mega_showdown.api.event.DynamaxStartCallback");
        return true;
    } catch (ClassNotFoundException e) {
        return false;
    }
}
```

- Uses `Class.forName()` to detect if Mega Showdown classes exist
- Result is cached to avoid repeated reflection
- Safe to call even when mod is not present

#### 2. Conditional Loading

**MegaShowdownIntegration.java**

```java
public static void init() {
    if (!MegaShowdownDetector.isLoaded()) {
        LOGGER.info("Skipping Mega Showdown integration - mod not present");
        return;
    }

    try {
        // Load event handler class ONLY when mod is present
        Class<?> handlerClass = Class.forName(
            "cobblemonquestsextended.cobblemon_quests_extended.integrations.megashowdown.MegaShowdownEventHandler"
        );
        handlerClass.getMethod("register").invoke(null);
    } catch (Exception e) {
        LOGGER.warning("Failed to initialize integration: " + e.getMessage());
    }
}
```

- **Critical**: Uses reflection to load the event handler class
- Prevents `ClassNotFoundException` when Mega Showdown is not installed
- If the handler class was imported directly, the JVM would fail to load the integration class

#### 3. Event Handler (Loaded Conditionally)

**MegaShowdownEventHandler.java**

```java
public final class MegaShowdownEventHandler {
    // THIS CLASS IS ONLY LOADED IF MEGA SHOWDOWN IS PRESENT

    public static void register() {
        DynamaxStartCallback.EVENT.register(MegaShowdownEventHandler::onDynamaxStart);
        UltraBurstCallback.EVENT.register(MegaShowdownEventHandler::onUltraBurst);
    }

    private static void onDynamaxStart(PokemonBattle battle, BattlePokemon pokemon, Boolean gmax) {
        String action = gmax ? "gigantamax" : "dynamax";
        CobblemonQuests.eventHandler.processTasksForTeam(pokemon, action, 1, player);
    }
}
```

- Contains direct imports from Mega Showdown
- Only loaded via reflection when Mega Showdown is detected
- Processes Mega Showdown events and maps them to quest actions

### Benefits

1. **No hard dependency**: Mod works without Mega Showdown installed
2. **No runtime overhead**: Integration code never loads if dependency is absent
3. **Extensible pattern**: Easy to add more integrations using the same approach
4. **Fail-safe**: Catches and logs errors instead of crashing

### Extension Guide for Developers

To add a new soft dependency integration:

1. Create a detector class:
   ```java
   public class MyModDetector {
       public static boolean isLoaded() {
           try {
               Class.forName("com.mymod.SomeClass");
               return true;
           } catch (ClassNotFoundException e) {
               return false;
           }
       }
   }
   ```

2. Create an integration loader:
   ```java
   public class MyModIntegration {
       public static void init() {
           if (!MyModDetector.isLoaded()) return;

           try {
               Class<?> handler = Class.forName("your.package.MyModEventHandler");
               handler.getMethod("register").invoke(null);
           } catch (Exception e) {
               CobblemonQuests.LOGGER.warning("Failed to load MyMod integration");
           }
       }
   }
   ```

3. Create the event handler (loaded via reflection):
   ```java
   public class MyModEventHandler {
       public static void register() {
           // Register event listeners for the optional mod
       }
   }
   ```

4. Call from main initialization:
   ```java
   // In CobblemonQuests.init()
   MyModIntegration.init();
   ```

---

## Data Flow

### Quest Progress Flow

```
┌─────────────────┐
│  Minecraft      │
│  Game Event     │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────┐
│  Cobblemon Event            │
│  (e.g., PokemonCaptured)    │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  CobblemonQuestsEventHandler│
│  - Maps to action(s)        │
│  - Extracts Pokemon data    │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  processTasksForTeam()      │
│  - Gets player's team       │
│  - Finds active tasks       │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  For each CobblemonTask:    │
│  - Check action matches     │
│  - Validate Pokemon         │
│  - Check conditions         │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  task.increase()            │
│  - Increment progress       │
│  - Update TeamData          │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  FTB Quests                 │
│  - Updates UI               │
│  - Triggers rewards         │
│  - Unlocks next quests      │
└─────────────────────────────┘
```

### Initialization Flow

```
┌──────────────────────────┐
│ Mod Loader Entry Point   │
│ (Fabric/NeoForge)        │
└──────────┬───────────────┘
           │
           ▼
┌──────────────────────────┐
│ CobblemonQuests.init()   │
│ - Initialize config      │
└──────────┬───────────────┘
           │
           ├──────────────────────────────┐
           │                              │
           ▼                              ▼
┌──────────────────────┐    ┌───────────────────────────┐
│ ActionRegistry.init()│    │ CobblemonQuestsEventHandler│
│ - Register actions   │    │ - Subscribe to events      │
└──────────────────────┘    └───────────────────────────┘
           │                              │
           ▼                              ▼
┌──────────────────────┐    ┌───────────────────────────┐
│ PokemonTaskTypes     │    │ MegaShowdownIntegration   │
│ - Register task type │    │ - Detect & load if present│
└──────────────────────┘    └───────────────────────────┘
```

---

## Extension Points

### For Mod Developers

#### 1. Register Custom Actions

Add new quest-triggerable actions to the registry:

```java
// During mod initialization
ActionRegistry.register("my_custom_action", ActionDefinition.of(
    "my_custom_action",
    true,                    // requiresPokemon
    ActionCategory.OTHER
));
```

#### 2. Trigger Custom Actions

Fire your custom actions to update quest progress:

```java
// In your event handler
Pokemon pokemon = /* ... */;
ServerPlayer player = /* ... */;

CobblemonQuests.eventHandler.processTasksForTeam(
    pokemon,
    "my_custom_action",
    1,      // amount to increment
    player
);
```

#### 3. Add Soft Dependency Integrations

Follow the pattern in `/integrations/megashowdown/`:

1. Create a detector class
2. Create an integration loader with reflection
3. Create an event handler (loaded conditionally)
4. Register from `CobblemonQuests.init()`

#### 4. Extend Task Conditions

Add new filtering conditions to `CobblemonTask`:

```java
// In CobblemonTask.java
public ArrayList<String> customField = new ArrayList<>();

// In writeData/readData
nbt.putString("custom_field", writeList(customField));
customField = readList(nbt.getString("custom_field"));

// In validation logic
if (!customField.isEmpty()) {
    String pokemonCustomValue = getPokemonCustomValue(pokemon);
    if (!customField.contains(pokemonCustomValue)) {
        return false; // Condition not met
    }
}
```

### API Surface

**Public Registry API:**
- `ActionRegistry.register()`
- `ActionRegistry.registerOrReplace()`
- `ActionRegistry.getAction()`
- `ActionRegistry.getActionsByCategory()`
- `ActionRegistry.isRegistered()`

**Event Processing API:**
- `CobblemonQuests.eventHandler.processTasksForTeam()`

**Constants:**
- `CobblemonQuests.MOD_ID`
- `CobblemonQuests.LOGGER`

---

## Configuration System

### Location
`/common/src/main/java/cobblemonquestsextended/cobblemon_quests_extended/config/CobblemonQuestsConfig.java`

### Configuration File

File location: `<config_dir>/cobblemon_quests_extended/cobblemon_quests_extended.config`

**Format:**
```
configVersion: 1.0
ignoredPokemon: pokemon1, pokemon2
suppressWarnings: false
```

### Configuration Options

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `configVersion` | double | 1.0 | Config file version for migration |
| `ignoredPokemon` | List<String> | [] | Pokemon to exclude from quest tracking |
| `suppressWarnings` | boolean | false | Hide non-critical warnings in logs |

### Loading Process

1. **Initialization**: Called from platform-specific entry points
2. **File check**: Creates default config if missing
3. **Parsing**: Reads key-value pairs from file
4. **Migration**: Handles config version updates
5. **Validation**: Logs warnings for unknown keys

---

## Performance Considerations

### Optimizations

1. **Lazy Task Loading**: Tasks are cached and only reloaded when quest file changes
   ```java
   private HashSet<CobblemonTask> pokemonTasks = null;  // Loaded on first use
   ```

2. **Thread-Safe Registry**: `ConcurrentHashMap` allows lock-free reads
   ```java
   private static final Map<String, ActionDefinition> ACTIONS = new ConcurrentHashMap<>();
   ```

3. **UUID Deduplication**: Prevents processing the same Pokemon twice
   ```java
   if (lastPokemonUuid == pokemon.getUuid()) return;
   ```

4. **Event Priority**: Uses `Priority.LOWEST` to process after other mods

5. **Cached Soft Dependency Detection**: Only checks once per session
   ```java
   private static Boolean cachedResult = null;
   ```

### Memory Footprint

- **Static registry**: ~10-20 KB for 40+ actions
- **Task cache**: Scales with number of active Cobblemon quests
- **Event handlers**: Single instance, minimal overhead

---

## Testing Strategy

### Unit Testing Recommendations

**Action Registry:**
- Test registration of custom actions
- Test duplicate registration prevention
- Test category filtering
- Test thread safety

**Event Handlers:**
- Mock Cobblemon events
- Verify action mapping
- Test deduplication logic
- Test error handling

**Soft Dependencies:**
- Test detection with and without optional mod
- Verify graceful degradation
- Test reflection-based loading

### Integration Testing

- Quest progress with various Pokemon conditions
- Multi-player team quest sharing
- Config loading and migration
- Cross-platform compatibility (Fabric vs NeoForge)

---

## Troubleshooting

### Common Issues

**Actions not triggering:**
1. Check if action is registered: `/cobblemon-quests list-actions`
2. Verify event subscription in logs
3. Check task filters (Pokemon, biome, time, etc.)

**Soft dependency not loading:**
1. Verify optional mod is installed
2. Check logs for "Failed to initialize X integration"
3. Ensure detector class name matches actual mod class

**Quest progress not updating:**
1. Verify player is in a team (FTB Teams requirement)
2. Check if quest is unlocked/started
3. Review task conditions

### Debug Logging

Enable debug logging by checking source code for `LOGGER.debug()` calls. Consider adding:
```java
CobblemonQuests.LOGGER.info("Processing action: " + action + " for Pokemon: " + pokemon.getSpecies().getName());
```

---

## Future Architecture Considerations

### Potential Enhancements

1. **Event Bus Abstraction**: Separate event handling from FTB Quests coupling
2. **Async Task Processing**: Process heavy validation off the main thread
3. **Data Pack Integration**: Load custom actions from JSON data packs
4. **Plugin API**: Formalize extension points with an SPI
5. **Metrics Collection**: Track most-used actions and conditions

### Scalability

Current architecture supports:
- Unlimited custom actions
- Unlimited soft dependencies
- Thousands of concurrent tasks
- Multi-server deployments (via FTB Quests sync)

---

## Contributing

When extending the architecture:

1. **Maintain separation**: Keep common/platform code separate
2. **Use soft dependencies**: Never hard-require optional mods
3. **Follow patterns**: Use existing patterns (registry, event handling)
4. **Document changes**: Update this architecture doc
5. **Test both loaders**: Verify Fabric and NeoForge compatibility

---

## License & Credits

This architecture powers Cobblemon Quests Extended, integrating:
- **Cobblemon** - Pokemon mod for Minecraft
- **FTB Quests** - Quest system framework
- **Architectury** - Multi-loader API framework

For implementation details, see source code at:
`/home/sauk/cobble-quests/cobblemon-quests-extended/`

---

**Document Version**: 1.0
**Last Updated**: 2025-12-12
**Mod Version**: Compatible with v1.1.12+
