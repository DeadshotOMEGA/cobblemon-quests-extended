# Cobblemon Quests Extended - Developer Context

## Project Purpose
Cobblemon Quests Extended is a Minecraft mod extending FTB Quests with Cobblemon-specific quest actions. It enables quest creators to design Pokemon-based objectives (catching, battling, evolving, using battle gimmicks like Mega Evolution/Terastallization). Supports multiple mod loaders (Fabric & NeoForge) through the Architectury framework.

**Base mod by WinterWolfSV**, extended with v1.7.0+ features including battle gimmicks, form changes, egg hatching, and an extensible ActionRegistry.

## Key Technologies
- **Language**: Java 21, Kotlin (build scripts)
- **Build System**: Gradle (Kotlin DSL)
- **Frameworks**:
  - Architectury (multi-loader support)
  - Cobblemon (Pokemon mod)
  - FTB Quests (quest framework)
  - FTB Teams, FTB Lib (dependencies)
- **Mod Loaders**: Fabric, NeoForge
- **Minecraft Version**: 1.21.1
- **License**: CC-BY-NC-4.0 (non-commercial use only)

## Architecture Overview

### Multi-Loader Pattern
```
┌──────────────────┐
│    common/       │ (95%+ of code - platform-independent)
│  Core systems    │
└────────┬─────────┘
         ├─────────────────┬─────────────────┐
         │                 │                 │
      fabric/          neoforge/          (future loaders)
     Entry point       Entry point
```

**Structure:**
- **common/** - ActionRegistry, EventHandler, Task system, Integrations
- **fabric/** - Fabric-specific initialization (Cobblemon_QuestsFabric.java)
- **neoforge/** - NeoForge-specific initialization (Cobblemon_QuestsNeoForge.java)

### Core Systems

#### 1. ActionRegistry (Extensibility Hub)
Located in `common/src/main/java/...registry/`

- **Purpose**: Centralized, thread-safe registry for quest-triggerable actions
- **Components**:
  - `ActionRegistry` - Static registry using ConcurrentHashMap
  - `ActionDefinition` (record) - Action metadata (id, requiresPokemon, category)
  - `ActionCategory` (enum) - CATCH, BATTLE, EVOLUTION, TRADE, POKEDEX, GIMMICK, OTHER
- **Built-in Actions**: 40+ actions including catch, defeat, mega_evolve, terastallize, hatch_egg, etc.
- **Key Methods**: `register()`, `getAction()`, `getActionsByCategory()`
- **Extension Pattern**: Other mods call `ActionRegistry.register()` to add custom actions

#### 2. Event Handling
Located in `common/src/main/java/.../events/CobblemonQuestsEventHandler.java`

- **Purpose**: Bridge Cobblemon events → quest progress
- **Pattern**: Listen to 20+ Cobblemon events (POKEMON_CAPTURED, BATTLE_VICTORY, EVOLUTION_COMPLETE, etc.)
- **Event Mapping**: Each event maps to one or more actions
- **Deduplication**: Prevents double-counting via UUID tracking
- **Error Handling**: Wrapped in try-catch to prevent crashes

#### 3. Task System
Located in `common/src/main/java/.../tasks/`

- **Purpose**: Implements Pokemon-specific quest objectives
- **Main Class**: `CobblemonTask` extends FTB Quests' Task
- **Validation Filters**: Pokemon (species, form, gender, type, nature, level, shiny), Environment (biome, dimension, time), Capture method (Poke Ball type), Gimmicks (Tera type, Mega form, Z-Crystal)
- **Progress Tracking**: Increments when all filters match

### Soft Dependency Pattern (Optional Mods)
Located in `common/src/main/java/.../integrations/`

**For Mega Showdown mod integration:**
1. **Detector** - Uses Class.forName() to check if mod is present
2. **Integration Loader** - Reflection-based conditional loading
3. **Event Handler** - Only loaded if detected; handles Dynamax/Gigantamax/Ultra Burst

**Why reflection?** Prevents ClassNotFoundException when optional mod missing.

## Build System (Gradle)

### Key Files
- **settings.gradle.kts** - Includes common, fabric, neoforge modules
- **gradle.properties** - Root: JVM args, mod metadata, versions
- **neoforge/gradle.properties** - NeoForge-specific versions
- **build.gradle.kts** files - Per-module compilation

### Dependencies
- **Cobblemon** 1.7.1+1.21.1
- **FTB Quests** 2101.1.19 (latest 1.21.1)
- **Architectury** 13.0.8
- **Fabric Loader** 0.18.1, **Fabric API** 0.116.7+1.21.1
- **NeoForge** 21.1.182

### Build Output
- **Artifacts**: `cobblemon_quests_extended-1.21.1-{version}.jar` per loader
- **Common code compiled** into both Fabric and NeoForge JARs

## Critical Conventions

### Package Structure
```
cobblemonquestsextended.cobblemon_quests_extended.
├── registry/          (ActionRegistry, ActionDefinition, ActionCategory)
├── events/            (CobblemonQuestsEventHandler)
├── tasks/             (CobblemonTask, Task types)
├── config/            (CobblemonQuestsConfig)
├── integrations/      (Soft dependency handlers)
├── commands/          (Admin commands)
├── logger/            (Logging)
└── CobblemonQuests    (Main entry point, MOD_ID, LOGGER)
```

### Event Subscription Pattern
- Use `Priority.LOWEST` to process after other handlers
- Subscribe in init() method from entry point
- Always wrap handlers in try-catch

### Filtering & Validation
- Filters are cumulative (AND logic)
- Empty lists = "any" (filter not applied)
- Use early returns for efficiency

### NBT Serialization
- Write/read via `writeData()` and `readData()`
- Use helper methods `writeList()` and `readList()`

## Important Constraints & Boundaries

### What NOT to Do
- **Never** hardcode dependency on optional mods - use soft dependency pattern
- **Never** modify FTB Quests core classes
- **Don't** add to common/ code that's loader-specific
- **Avoid** blocking main thread (use Priority.LOWEST)
- **Don't** throw exceptions in event handlers

### Multi-Loader Requirements
- All common code must work on both Fabric and NeoForge
- Platform-specific code ONLY in fabric/ and neoforge/ packages
- Test changes on both loaders

### Quest Configuration Format
- Uses SNBT (Structured NBT) syntax
- Key task fields: `action`, `amount`, `pokemon`, `type`, condition filters
- See docs/QUEST-CREATION.md for full spec

### Licensing Restriction
- **CC-BY-NC-4.0**: Non-commercial use only
- Must credit WinterWolfSV (original author)
- Modifications must be indicated

## Extension Points for Developers

### 1. Register Custom Action
```java
ActionRegistry.register("my_action", ActionDefinition.of(
    "my_action", true, ActionCategory.OTHER
));
```

### 2. Trigger Action Progress
```java
CobblemonQuests.eventHandler.processTasksForTeam(
    pokemon, "my_action", 1, player
);
```

### 3. Add Soft Dependency Integration
Follow `/integrations/megashowdown/` pattern with Detector → Integration → EventHandler (reflection-loaded).

### 4. Extend Task Conditions
Add field to CobblemonTask, implement in writeData/readData, validate in task logic.

## Directory Summary
- **gradle/** - Gradle wrapper
- **common/** - Shared source code (registry, events, tasks, integrations)
- **fabric/, neoforge/** - Loader-specific entry points
- **docs/** - Architecture, quest creation, roadmap documentation
- **test-quests/** - SNBT test quest examples
- **scripts/** - Build/deployment scripts

## Key Files to Understand First
1. `common/src/main/java/.../CobblemonQuests.java` - Entry point, initialization flow
2. `common/src/main/java/.../registry/ActionRegistry.java` - Extensibility system
3. `common/src/main/java/.../events/CobblemonQuestsEventHandler.java` - Event mapping
4. `common/src/main/java/.../tasks/CobblemonTask.java` - Task validation logic
5. `docs/ARCHITECTURE.md` - Detailed system diagrams
6. `README.md` - Features, installation, quest examples

## Contributing Notes
- Changes to common/ must be validated on both Fabric and NeoForge
- Follow ActionRegistry pattern for extensibility
- Use soft dependencies for optional mod integration
- Document new actions in ARCHITECTURE.md
- Include test quests in test-quests/ for validation
