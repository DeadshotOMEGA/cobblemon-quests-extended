# Contributing to Cobblemon Quests Extended

Thank you for your interest in contributing to Cobblemon Quests Extended! This guide will help you get started with development.

## Table of Contents

- [Development Environment Setup](#development-environment-setup)
- [Building the Mod](#building-the-mod)
- [Project Structure](#project-structure)
- [How to Add New Actions](#how-to-add-new-actions)
- [How to Add New Conditions](#how-to-add-new-conditions)
- [How to Add Mod Integrations](#how-to-add-mod-integrations)
- [Testing Guidelines](#testing-guidelines)
- [Code Style Guidelines](#code-style-guidelines)
- [Pull Request Process](#pull-request-process)
- [Version Numbering](#version-numbering)

## Development Environment Setup

### Prerequisites

- **Java Development Kit (JDK) 21** or higher
- **IntelliJ IDEA** (recommended) or another Java IDE
- **Git** for version control
- **Gradle** (included via wrapper, no separate installation needed)

### Initial Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/cobblemon_quests_extended.git
   cd cobblemon_quests_extended
   ```

2. **Import into your IDE:**
   - **IntelliJ IDEA:** `File > Open` and select the project directory
   - The IDE should automatically detect Gradle and import dependencies

3. **Run Gradle sync:**
   ```bash
   ./gradlew build
   ```
   This downloads all dependencies and sets up the development environment.

### Workspace Structure

The project uses **Architectury** for multi-platform support:

- `common/` - Shared code for all platforms
- `fabric/` - Fabric-specific implementation
- `neoforge/` - NeoForge-specific implementation

Most development happens in the `common/` module.

## Building the Mod

### Basic Build

To build all platform variants:

```bash
./gradlew build
```

Built JAR files will be in:
- `fabric/build/libs/cobblemon_quests_extended-1.21.1-fabric-{version}.jar`
- `neoforge/build/libs/cobblemon_quests_extended-1.21.1-neoforge-{version}.jar`

### Platform-Specific Builds

Build only Fabric:
```bash
./gradlew :fabric:build
```

Build only NeoForge:
```bash
./gradlew :neoforge:build
```

### Clean Build

To clean and rebuild from scratch:
```bash
./gradlew clean build
```

### Running in Development

To run the mod in a development environment:

**Fabric:**
```bash
./gradlew :fabric:runClient
```

**NeoForge:**
```bash
./gradlew :neoforge:runClient
```

## Project Structure

```
cobblemon-quests-extended/
├── common/src/main/java/cobblemonquestsextended/cobblemon_quests_extended/
│   ├── CobblemonQuests.java           # Main mod class and initialization
│   ├── commands/                      # Admin commands
│   │   ├── BlacklistPokemonCommand.java
│   │   ├── GivePokemonCommand.java
│   │   ├── RegisterCommands.java
│   │   └── SuppressWarningsCommand.java
│   ├── config/                        # Configuration handling
│   │   └── CobblemonQuestsConfig.java
│   ├── events/                        # Event handlers
│   │   └── CobblemonQuestsEventHandler.java
│   ├── integrations/                  # Third-party mod integrations
│   │   └── megashowdown/
│   │       ├── MegaShowdownDetector.java
│   │       ├── MegaShowdownEventHandler.java
│   │       └── MegaShowdownIntegration.java
│   ├── logger/                        # Custom logging
│   │   └── CobblemonQuestsLogger.java
│   ├── registry/                      # Action registry system
│   │   ├── ActionCategory.java
│   │   ├── ActionDefinition.java
│   │   └── ActionRegistry.java
│   └── tasks/                         # Quest task implementations
│       ├── CobblemonTask.java         # Main task class
│       ├── PokemonTaskTypes.java      # Task type registration
│       └── TaskData.java              # Task data constants
├── fabric/                            # Fabric platform code
├── neoforge/                          # NeoForge platform code
├── build.gradle.kts                   # Root Gradle build script
├── gradle.properties                  # Project properties and versions
└── settings.gradle.kts                # Gradle settings
```

### Key Files

- **`CobblemonQuests.java`** - Main entry point, handles initialization
- **`CobblemonTask.java`** - Core task implementation with condition logic
- **`CobblemonQuestsEventHandler.java`** - Event listeners for Cobblemon events
- **`ActionRegistry.java`** - Centralized registry for all action types
- **`TaskData.java`** - Static data for actions, conditions, forms, etc.

## How to Add New Actions

Actions represent what the player must do to complete a quest (e.g., catch, defeat, evolve).

### Step-by-Step Guide

#### 1. Register the Action in ActionRegistry

Open `common/src/main/java/.../registry/ActionRegistry.java` and add your action to the `registerBuiltInActions()` method:

```java
private static void registerBuiltInActions() {
    // ... existing actions ...

    // Add your new action
    registerInternal("my_custom_action", true, ActionCategory.OTHER);
}
```

**Parameters:**
- `"my_custom_action"` - The action ID (must be unique, lowercase with underscores)
- `true` - Whether this action requires a Pokémon (false for actions like defeat_player)
- `ActionCategory.OTHER` - The category (CATCH, BATTLE, EVOLUTION, TRADE, POKEDEX, GIMMICK, or OTHER)

#### 2. Add Action to TaskData

Open `common/src/main/java/.../tasks/TaskData.java` and add your action to the `actionList`:

```java
public static final List<String> actionList = List.of(
    "catch",
    "defeat",
    // ... existing actions ...
    "my_custom_action"  // Add here
);
```

#### 3. Create Event Listener

In `CobblemonQuestsEventHandler.java`, subscribe to the relevant Cobblemon event:

```java
public CobblemonQuestsEventHandler init() {
    // ... existing subscriptions ...

    // Subscribe to your event
    CobblemonEvents.MY_CUSTOM_EVENT.subscribe(Priority.LOWEST, this::myCustomEventHandler);

    return this;
}

private void myCustomEventHandler(MyCustomEvent event) {
    Pokemon pokemon = event.getPokemon();
    ServerPlayer player = event.getPlayer();

    TeamData teamData = getTeamData(player.getUUID());
    if (teamData == null) return;

    for (CobblemonTask task : pokemonTasks) {
        task.increase(teamData, pokemon, "my_custom_action", 1L, player);
    }
}
```

#### 4. Add Translation Keys

Add translation entries in `common/src/main/resources/assets/cobblemon_quests_extended/lang/en_us.json`:

```json
{
  "cobblemon_quests.actions.my_custom_action": "My Custom Action"
}
```

#### 5. Test Your Action

1. Build the mod: `./gradlew build`
2. Launch the client: `./gradlew :fabric:runClient`
3. Create a quest with your new action in FTB Quests
4. Trigger the action in-game and verify it increments the quest

### Example: Faint Pokemon Action

Here's how the `faint_pokemon` action was implemented:

```java
// 1. Registered in ActionRegistry
registerInternal("faint_pokemon", true, ActionCategory.BATTLE);

// 2. Added to TaskData.actionList
"faint_pokemon"

// 3. Event handler in CobblemonQuestsEventHandler
CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.LOWEST, this::battleFainted);

private void battleFainted(BattleFaintedEvent event) {
    BattlePokemon faintedPokemon = event.getKilled();
    Pokemon pokemon = faintedPokemon.getOriginalPokemon();

    // Find player who caused the faint
    for (BattleActor actor : event.getBattle().getActors()) {
        if (actor.getType() == ActorType.PLAYER) {
            ServerPlayer player = (ServerPlayer) actor.getEntity();
            TeamData teamData = getTeamData(player.getUUID());
            if (teamData == null) continue;

            for (CobblemonTask task : pokemonTasks) {
                task.increase(teamData, pokemon, "faint_pokemon", 1L, player);
            }
        }
    }
}
```

## How to Add New Conditions

Conditions filter which Pokémon count toward quest completion (e.g., shiny, specific level, specific type).

### Step-by-Step Guide

#### 1. Add Field to CobblemonTask

Open `common/src/main/java/.../tasks/CobblemonTask.java` and add a new field:

```java
public class CobblemonTask extends Task {
    // ... existing fields ...
    public ArrayList<String> myCondition = new ArrayList<>();
```

#### 2. Add Serialization Support

In the same file, add serialization in `writeData()` and `readData()`:

```java
@Override
public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
    super.writeData(nbt, provider);
    // ... existing writes ...
    nbt.putString("my_condition", writeList(myCondition));
}

@Override
public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
    super.readData(nbt, provider);
    // ... existing reads ...
    myCondition = readList(nbt.getString("my_condition"));
}
```

#### 3. Add Network Serialization

Add network serialization for client/server sync:

```java
@Override
public void writeNetData(RegistryFriendlyByteBuf buffer) {
    super.writeNetData(buffer);
    // ... existing writes ...
    buffer.writeUtf(writeList(myCondition), Short.MAX_VALUE);
}

@Override
public void readNetData(RegistryFriendlyByteBuf buffer) {
    super.readNetData(buffer);
    // ... existing reads ...
    myCondition = readList(buffer.readUtf(Short.MAX_VALUE));
}
```

#### 4. Add to Config UI

Add a config option so quest creators can set the condition:

```java
@Override
@Environment(EnvType.CLIENT)
public void fillConfigGroup(ConfigGroup config) {
    super.fillConfigGroup(config);
    // ... existing config ...

    List<String> myConditionList = List.of("value1", "value2", "value3");
    addConfigList(config, "my_condition", myCondition, myConditionList, null, null);
}
```

#### 5. Add to TaskData (if using dropdown list)

If your condition uses a predefined list, add it to `TaskData.java`:

```java
public static final List<String> myConditionList = List.of(
    "value1",
    "value2",
    "value3"
);
```

#### 6. Implement Condition Logic

Add the validation logic in the `increase()` method:

```java
public void increase(TeamData teamData, Pokemon pokemon, String executedAction, long progress, ServerPlayer player) {
    if (actions.contains(executedAction) || /* ... */) {
        // ... existing checks ...

        // Check your custom condition
        if (!myCondition.isEmpty()) {
            if (!myCondition.contains(pokemon.getSomeProperty())) {
                return; // Condition not met, don't increment quest
            }
        }

        // ... rest of the method ...
    }
}
```

#### 7. Add Translation Keys

```json
{
  "cobblemon_quests.task.my_condition": "My Condition",
  "cobblemon_quests.my_condition.value1": "Value 1",
  "cobblemon_quests.my_condition.value2": "Value 2",
  "cobblemon_quests.my_condition.value3": "Value 3"
}
```

### Example: Tera Type Condition

Here's how the `tera_type` condition was implemented:

```java
// 1. Field declaration
public ArrayList<String> teraTypes = new ArrayList<>();

// 2. Serialization
nbt.putString("tera_type", writeList(teraTypes));
teraTypes = readList(nbt.getString("tera_type"));

// 3. Network serialization
buffer.writeUtf(writeList(teraTypes), Short.MAX_VALUE);
teraTypes = readList(buffer.readUtf(Short.MAX_VALUE));

// 4. Config UI
addConfigList(config, "tera_types", teraTypes, teraTypeList, null, pokemonTypeNameProcessor);

// 5. TaskData list
public static final List<String> teraTypeList = List.of(
    "normal", "fire", "water", /* ... all types ... */
);

// 6. Validation logic (handled in event handler for terastallization)
// The tera type is checked when the TerastallizationEvent fires
```

## How to Add Mod Integrations

Use the **soft dependency pattern** to add integrations with other mods without creating hard dependencies.

### Step-by-Step Guide

#### 1. Add modCompileOnly Dependency

In `common/build.gradle.kts`, add the dependency as `modCompileOnly`:

```kotlin
dependencies {
    // ... existing dependencies ...

    // Soft dependency - mod not required at runtime
    modCompileOnly("curse.maven:my-mod-123456:7890123")
}
```

**Important:** Use `modCompileOnly`, NOT `modImplementation`. This allows the mod to load without the dependency.

#### 2. Create Integration Package Structure

Create a new package for your integration:

```
common/src/main/java/.../integrations/
└── mymod/
    ├── MyModDetector.java
    ├── MyModEventHandler.java
    └── MyModIntegration.java
```

#### 3. Create Detector Class

The detector checks if the mod is present at runtime:

```java
package cobblemonquestsextended.cobblemon_quests_extended.integrations.mymod;

import cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests;

/**
 * Detects whether MyMod is present at runtime.
 * Uses class detection to avoid hard dependency.
 */
public final class MyModDetector {

    private static Boolean cachedResult = null;
    private static final String MY_MOD_MARKER_CLASS = "com.example.mymod.api.MyModClass";

    private MyModDetector() {
        // Utility class
    }

    /**
     * Checks if MyMod is present.
     * Result is cached after first check.
     *
     * @return true if MyMod is loaded
     */
    public static boolean isLoaded() {
        if (cachedResult == null) {
            cachedResult = detectMyMod();
        }
        return cachedResult;
    }

    private static boolean detectMyMod() {
        try {
            Class.forName(MY_MOD_MARKER_CLASS);
            CobblemonQuests.LOGGER.info("MyMod detected - enabling custom actions");
            return true;
        } catch (ClassNotFoundException e) {
            CobblemonQuests.LOGGER.info("MyMod not found - custom actions will not fire");
            return false;
        }
    }

    /**
     * Clears the cached detection result.
     * Primarily for testing purposes.
     */
    public static void clearCache() {
        cachedResult = null;
    }
}
```

#### 4. Create Event Handler

This class handles events from the integrated mod:

```java
package cobblemonquestsextended.cobblemon_quests_extended.integrations.mymod;

import com.example.mymod.api.events.MyCustomEvent;
import cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests;
import net.minecraft.server.level.ServerPlayer;
import com.cobblemon.mod.common.pokemon.Pokemon;

/**
 * Event handlers for MyMod integration.
 * This class is only loaded when MyMod is present.
 */
public final class MyModEventHandler {

    private MyModEventHandler() {
        // Utility class
    }

    /**
     * Registers event listeners for MyMod events.
     */
    public static void register() {
        MyCustomEvent.REGISTER.subscribe(event -> {
            handleMyCustomEvent(event);
        });

        CobblemonQuests.LOGGER.info("MyMod event handlers registered");
    }

    private static void handleMyCustomEvent(MyCustomEvent event) {
        ServerPlayer player = event.getPlayer();
        Pokemon pokemon = event.getPokemon();

        // Trigger quest progress
        TeamData teamData = CobblemonQuests.eventHandler.getTeamData(player.getUUID());
        if (teamData == null) return;

        for (CobblemonTask task : CobblemonQuests.eventHandler.getPokemonTasks()) {
            task.increase(teamData, pokemon, "my_custom_action", 1L, player);
        }
    }
}
```

#### 5. Create Integration Class

This class safely initializes the integration:

```java
package cobblemonquestsextended.cobblemon_quests_extended.integrations.mymod;

import cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests;

/**
 * Integration layer for MyMod.
 * Handles registration of event listeners when MyMod is present.
 *
 * <p>This class uses a soft dependency pattern - it will only initialize
 * the actual event handlers if MyMod is detected at runtime.</p>
 */
public final class MyModIntegration {

    private static boolean initialized = false;

    private MyModIntegration() {
        // Utility class
    }

    /**
     * Initializes the MyMod integration if the mod is present.
     * Safe to call multiple times - will only initialize once.
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        if (!MyModDetector.isLoaded()) {
            CobblemonQuests.LOGGER.info("Skipping MyMod integration - mod not present");
            return;
        }

        try {
            // Load the event handler class only when MyMod is present
            // This prevents ClassNotFoundException when MyMod is not installed
            Class<?> handlerClass = Class.forName(
                "cobblemonquestsextended.cobblemon_quests_extended.integrations.mymod.MyModEventHandler"
            );
            handlerClass.getMethod("register").invoke(null);
            CobblemonQuests.LOGGER.info("MyMod integration initialized successfully");
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Failed to initialize MyMod integration: " + e.getMessage());
        }
    }
}
```

#### 6. Initialize Integration in Main Class

Add initialization to `CobblemonQuests.java`:

```java
public static void init(Path configPath, boolean useConfig) {
    // ... existing initialization ...

    // Initialize mod integrations (soft dependencies)
    MegaShowdownIntegration.init();
    MyModIntegration.init();  // Add your integration here

    // ... rest of initialization ...
}
```

#### 7. Register Actions

Register your new actions in `ActionRegistry.java`:

```java
private static void registerBuiltInActions() {
    // ... existing actions ...

    // MyMod integration actions (will be registered even if mod not present)
    registerInternal("my_custom_action", true, ActionCategory.OTHER);
}
```

### Why Use Soft Dependencies?

- **No Hard Requirement:** Users can install your mod without requiring the integrated mod
- **Graceful Degradation:** Events simply won't fire if the integrated mod is missing
- **ClassLoader Safety:** Using `Class.forName()` prevents `ClassNotFoundException` at startup
- **Compile-Time Access:** `modCompileOnly` lets you develop against the API without bundling it

## Testing Guidelines

### Manual Testing

1. **Build the mod:**
   ```bash
   ./gradlew build
   ```

2. **Launch development client:**
   ```bash
   ./gradlew :fabric:runClient
   # or
   ./gradlew :neoforge:runClient
   ```

3. **In-game testing:**
   - Create a test world with cheats enabled
   - Open FTB Quests (`/ftbquests`)
   - Create test quests using your new actions/conditions
   - Trigger the actions in-game and verify quest progress

### Test Checklist

Before submitting a pull request, verify:

- [ ] New actions increment quest progress correctly
- [ ] Conditions properly filter Pokémon
- [ ] Quest completion fires when expected
- [ ] No errors in console/logs
- [ ] Works on both Fabric and NeoForge (if applicable)
- [ ] Soft dependencies work with and without the integrated mod
- [ ] Translation keys display correctly in the UI

### Testing Quest Files

Sample quest files can be placed in `test-quests/` directory for testing purposes. Example:

```snbt
{
    id: "test_quest_001"
    tasks: [{
        action: "my_custom_action"
        amount: 5L
        pokemon: "cobblemon:pikachu"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

## Code Style Guidelines

### Java Code Style

- **Indentation:** 4 spaces (no tabs)
- **Line Length:** Aim for 120 characters max
- **Naming Conventions:**
  - Classes: `PascalCase` (e.g., `CobblemonTask`)
  - Methods: `camelCase` (e.g., `increaseProgress`)
  - Constants: `UPPER_SNAKE_CASE` (e.g., `MOD_ID`)
  - Variables: `camelCase` (e.g., `pokemonTasks`)
- **Braces:** Opening brace on same line (K&R style)

### Documentation

- **JavaDoc:** Add JavaDoc comments for all public methods and classes
- **Inline Comments:** Use for complex logic or non-obvious behavior
- **Example:**

```java
/**
 * Checks if the specified action is registered in the registry.
 *
 * @param actionId the action identifier to check
 * @return true if the action exists in the registry, false otherwise
 */
public static boolean isRegistered(String actionId) {
    return ACTIONS.containsKey(actionId);
}
```

### Error Handling

- **Never silently swallow exceptions**
- **Log errors appropriately:**

```java
try {
    // risky operation
} catch (Exception e) {
    CobblemonQuests.LOGGER.warning("Failed to process action: " + e.getMessage());
    throw e; // Re-throw if critical
}
```

### Best Practices

1. **Null Safety:** Check for null before dereferencing
   ```java
   if (teamData == null) return;
   ```

2. **Early Returns:** Use early returns for validation
   ```java
   if (!actions.contains(executedAction)) return;
   if (pokemon == null) return;
   ```

3. **Immutability:** Prefer immutable collections where possible
   ```java
   public static final List<String> actionList = List.of("catch", "defeat");
   ```

4. **Resource Management:** Use try-with-resources for closeable resources

5. **Logging Levels:**
   - `LOGGER.info()` - Important initialization/state changes
   - `LOGGER.debug()` - Detailed debugging information
   - `LOGGER.warning()` - Recoverable errors
   - `LOGGER.error()` - Critical errors

## Pull Request Process

### Before Submitting

1. **Update your branch:**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout your-feature-branch
   git rebase develop
   ```

2. **Build and test:**
   ```bash
   ./gradlew clean build
   ```

3. **Check for errors:**
   - No compilation errors
   - No warnings (if possible)
   - All manual tests pass

### Creating a Pull Request

1. **Push your branch:**
   ```bash
   git push origin your-feature-branch
   ```

2. **Open PR on GitHub:**
   - Target branch: `develop` (NOT `main`)
   - Title: Descriptive summary (e.g., "Add terastallization quest action")
   - Description: Include:
     - What changes were made
     - Why the changes were needed
     - How to test the changes
     - Any breaking changes

3. **PR Template Example:**

```markdown
## Description
Adds support for Terastallization quest actions and Tera type conditions.

## Changes
- Added `terastallize` action to ActionRegistry
- Added `tera_type` condition to CobblemonTask
- Created event handler for TerastallizationEvent
- Added translation keys for all 18 Tera types

## Testing
1. Create a quest with `terastallize` action
2. Set `tera_type` to "fire"
3. Terastallize a Pokémon with Fire Tera type in battle
4. Verify quest increments by 1

## Breaking Changes
None

## Checklist
- [x] Builds without errors
- [x] Tested in-game
- [x] Added translation keys
- [x] Updated documentation
```

### Review Process

- Maintainers will review your PR
- Address any requested changes
- Once approved, your PR will be merged into `develop`
- Changes will be included in the next release

### Git Commit Messages

Follow conventional commits format:

- `feat: add terastallization quest action`
- `fix: resolve null pointer in pokemon check`
- `docs: update CONTRIBUTING.md with new examples`
- `refactor: simplify action registry lookup`
- `test: add integration test for mega evolution`

## Version Numbering

This project uses **Semantic Versioning** (SemVer): `MAJOR.MINOR.PATCH`

### Version Format

```
1.0.0
│ │ │
│ │ └─ PATCH: Bug fixes and minor changes
│ └─── MINOR: New features, backward compatible
└───── MAJOR: Breaking changes, incompatible API changes
```

### When to Increment

- **MAJOR** (1.0.0 → 2.0.0):
  - Breaking changes to quest file format
  - Removed or renamed actions/conditions
  - Changes requiring quest recreation

- **MINOR** (1.0.0 → 1.1.0):
  - New actions or conditions
  - New mod integrations
  - New features (backward compatible)

- **PATCH** (1.0.0 → 1.0.1):
  - Bug fixes
  - Performance improvements
  - Translation updates
  - Documentation updates

### Version in Files

Version is set in `gradle.properties`:

```properties
mod_version=1.0.0
```

### Minecraft Version Suffix

Builds include Minecraft version in filename:

```
cobblemon_quests_extended-1.21.1-fabric-{version}.jar
cobblemon_quests_extended-1.21.1-neoforge-{version}.jar
```

This is configured via `archives_base_name` in `gradle.properties`:

```properties
archives_base_name=cobblemon_quests_extended-1.21.1
```

## Additional Resources

- **Cobblemon Wiki:** https://wiki.cobblemon.com/
- **FTB Quests Wiki:** https://ftb.fandom.com/wiki/FTB_Quests
- **Architectury Documentation:** https://docs.architectury.dev/
- **Fabric Wiki:** https://fabricmc.net/wiki/
- **NeoForge Documentation:** https://docs.neoforged.net/

## Getting Help

- **Discord:** Join the Cobblemon Discord for questions
- **GitHub Issues:** Report bugs or request features
- **GitHub Discussions:** Ask questions or discuss ideas

## License

This project is licensed under **CC-BY-NC-4.0** (Creative Commons Attribution-NonCommercial 4.0 International).

By contributing, you agree that your contributions will be licensed under the same license.

---

Thank you for contributing to Cobblemon Quests Extended! Your contributions help make quest creation better for everyone.
