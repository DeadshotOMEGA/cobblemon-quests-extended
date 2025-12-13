# Quest Visual Designer - Implementation Roadmap

This roadmap outlines the phased development plan for enhancing the quest creation experience in Cobblemon Quests Extended.

## Overview

| Phase | Name | Status | Risk | Timeline |
|-------|------|--------|------|----------|
| 1 | Foundation | ✅ Complete | LOW | v1.2.0 |
| 2 | Smart Conditional Fields | ✅ Complete | MODERATE | v1.3.0 |
| 3 | Domain Model + Validation | ✅ Complete | MODERATE | v1.4.0 |
| 4 | Live Preview + UI Integration | ✅ Complete | MODERATE | v1.5.0 |
| 5 | Web Editor | 🔲 Planned | HIGH | v2.0.0 |
| 6 | CLI/API | 🔲 Planned | LOW | v2.1.0 |

---

## Phase 1: Foundation ✅ COMPLETE

**Goal:** Fix immediate pain point + lay groundwork
**Risk:** LOW
**Version:** 1.2.0
**Status:** Released 2025-12-13

### Deliverables

- [x] Fix action picker (ConfigActionType + SelectActionScreen with categories)
- [x] Create ActionDefinition description/example fields in ActionRegistry
- [x] Add localization for action descriptions (28 actions)
- [x] Rich tooltips in action picker (description, example, requirements indicator)
- [x] Category color coding and collapsible sections

### Files Changed

**New:**
- `client/config/ConfigActionType.java` - Custom ConfigValue for action selection
- `client/gui/SelectActionScreen.java` - Categorized action picker UI
- `client/gui/ActionButton.java` - Reusable action button widget

**Modified:**
- `registry/ActionDefinition.java` - Added descriptionKey, exampleKey fields
- `tasks/CobblemonTask.java` - Integrated ConfigActionType
- `lang/en_us.json` - Action descriptions and examples

---

## Phase 2: Smart Conditional Fields ✅ COMPLETE

**Goal:** Only show relevant condition fields based on selected action
**Risk:** MODERATE
**Version:** 1.3.0
**Status:** Released 2025-12-13

### Problem

Currently, all condition fields are shown regardless of selected action. For example:
- `tera_type` is shown even when action is `catch` (irrelevant)
- `mega_form` is shown even when action is `trade_away` (irrelevant)
- `poke_ball_used` is shown even when action is `level_up` (irrelevant)

### Deliverables

1. **FieldVisibilityRules** - Define which fields are relevant for each action
   - Map of action → relevant condition fields
   - Support for field dependencies (e.g., `mega_form` only when `mega_evolve`)

2. **Dynamic field hiding in fillConfigGroup()**
   - Listen for action changes
   - Show/hide fields based on visibility rules
   - Preserve values when fields are hidden (don't clear)

3. **Field grouping** - Organize conditions into logical groups:
   - **Pokemon Filters:** species, form, gender, type, shiny, level
   - **Location Filters:** biome, dimension, region, time
   - **Battle Gimmicks:** mega_form, tera_type, z_crystal, dynamax_type
   - **Capture:** poke_ball_used
   - **Pokedex:** dex_progress

### Files

**New:**
- `config/FieldVisibilityRules.java` - Action → field mapping
- `config/ConditionFieldGroup.java` - Field grouping enum

**Modified:**
- `tasks/CobblemonTask.java` - Dynamic field visibility in fillConfigGroup()

### Technical Notes

```java
// Example FieldVisibilityRules structure
public class FieldVisibilityRules {
    private static final Map<String, Set<String>> ACTION_FIELDS = Map.of(
        "catch", Set.of("pokemon", "form", "gender", "shiny", "min_level", "max_level",
                        "pokemon_type", "region", "biome", "dimension", "time_min",
                        "time_max", "poke_ball_used"),
        "mega_evolve", Set.of("pokemon", "mega_form"),
        "terastallize", Set.of("pokemon", "tera_type"),
        // ...
    );

    public static boolean isFieldRelevant(String action, String field) {
        Set<String> fields = ACTION_FIELDS.get(action);
        return fields != null && fields.contains(field);
    }
}
```

### Execution Plan

| Tier | Stage | Agent | Purpose | Status |
|------|-------|-------|---------|--------|
| T0 | Git Setup | `git-flow-manager` | Create feature/smart-conditional-fields branch | ✅ |
| T1 | Explore | `Explore` | Analyze CobblemonTask, ActionRegistry, FTB config system | ✅ |
| T3 | Planning | `Plan` | Create implementation plan for 3 Java files | ✅ |
| T4 | Implementation | `programmer` | Implement visibility rules in CobblemonTask | ✅ |
| T5 | Validation | `code-reviewer` | Review implementation for quality and patterns | ✅ |
| T6 | Debug | `Explore` | Investigate why fields weren't updating dynamically | ✅ |
| T7 | Phase 3 Fix | Direct | Override onEditButtonClicked() for dynamic rebuild | ✅ |

### Implementation Notes

**Key Discovery:** FTB Library's ConfigGroup doesn't support dynamic field visibility after initialization. `fillConfigGroup()` is called once when the dialog opens.

**Solution:** Override `onEditButtonClicked()` in CobblemonTask to:
1. Capture initial `actions` state
2. In the callback, detect if `actions` changed
3. If changed, reopen the screen (triggering `fillConfigGroup()` with new state)
4. If unchanged, proceed with normal save flow

**Files Created:**
- `client/gui/CobblemonTaskEditScreen.java` - Custom EditConfigScreen with dynamic "Update Fields" button
- `tasks/CobblemonTaskGuiProvider.java` - Custom GuiProvider for new task creation flow

**Files Modified:**
- `CobblemonTask.java` - Added `onEditButtonClicked()` override, visibility helper methods, static Sets
- `PokemonTaskTypes.java` - Added `initClient()` for GuiProvider registration
- `CobblemonQuests.java` - Calls `initClient()` on client side
- `en_us.json` - Added `task.title` and `task.update_fields` translation keys

**User Experience:**
- When user changes actions, the "Accept" button changes to "Update Fields"
- Clicking "Update Fields" applies changes and reopens screen with updated conditional fields
- Clear visual indication that field visibility has changed based on selected actions

---

## Phase 3: Enhanced UI + Domain Model + Validation ✅ COMPLETE

**Goal:** Professional UI for all condition fields, clean data separation, validation
**Risk:** MODERATE
**Version:** 1.4.0
**Status:** Released 2025-12-13

### Reference Documents

- **[CONDITIONAL_FIELD_ANALYSIS.md](CONDITIONAL_FIELD_ANALYSIS.md)** - Comprehensive analysis of all 19 conditional fields, action-by-action visibility matrix for 28 actions, and proposed implementation

### Implementation Status

#### ✅ Completed

| Component | Files | Notes |
|-----------|-------|-------|
| **Domain Model** | `domain/CobblemonTaskModel.java` | Pure POJO with Builder pattern, `fromTask()` and `applyToTask()` methods |
| **Validation System** | `domain/validation/*.java` | ValidationSeverity, ValidationIssue, ValidationResult, TaskValidator with 15+ rules |
| **Serializer** | `domain/serialization/TaskModelSerializer.java` | NBT serialization with v1→v2 migration |
| **Category Enums** | `registry/*.java` | PokemonTypeCategory, BiomeCategory, NatureCategory, FormCategory, PokemonGeneration |
| **ConfigValue Types** | `client/config/Config*Type.java` | 11 new types with color coding and custom selectors |
| **Selector Screens** | `client/gui/selectors/*.java` | AbstractSelectorScreen base + 11 specific screens |
| **Localization** | `lang/en_us.json` | ~100 new translation keys for validation, categories, forms |
| **Edit Screen** | `client/gui/CobblemonTaskEditScreen.java` | Validation integration, "Fix Errors" button state |
| **Logging** | Throughout | Extensive logging for debugging validation flow |

#### ✅ Completed in Phase 4

| Component | Issue | Resolution |
|-----------|-------|------------|
| **ConfigValue Integration** | New Config*Type classes wired to CobblemonTask.fillConfigGroup() | ✅ Done |
| **Selector Screen Usage** | Selector screens now open when clicking fields | ✅ Done |
| **Validation UI Feedback** | Validation errors/warnings shown in LivePreviewPanel | ✅ Done |
| **Visual Error Indicators** | Errors and warnings visible in real-time preview | ✅ Done |

### Files Created

**Domain Layer (`domain/`):**
- `CobblemonTaskModel.java` - Immutable domain model with Builder
- `validation/ValidationSeverity.java` - ERROR/WARNING/INFO enum
- `validation/ValidationIssue.java` - Record with field, messageKey, severity
- `validation/ValidationResult.java` - Immutable result container
- `validation/TaskValidator.java` - Validates models, 15+ rules
- `serialization/TaskModelSerializer.java` - NBT with migration

**Category Enums (`registry/`):**
- `PokemonTypeCategory.java` - PHYSICAL/SPECIAL
- `BiomeCategory.java` - FOREST/PLAINS/DESERT/etc.
- `NatureCategory.java` - NEUTRAL/PHYSICAL/SPECIAL/SPEED/DEFENSIVE
- `FormCategory.java` - REGIONAL/MEGA/DYNAMAX/LEGENDARY/OTHER
- `PokemonGeneration.java` - GEN_1 through GEN_9 with dex ranges

**ConfigValue Types (`client/config/`):**
- `ConfigTypeSelector.java` - Pokemon type (18 types)
- `ConfigPokemonType.java` - Pokemon species
- `ConfigFormType.java` - Forms/aspects
- `ConfigBiomeType.java` - Biomes by category
- `ConfigDimensionType.java` - Dimensions
- `ConfigNatureType.java` - Natures (25)
- `ConfigPokeBallType.java` - Poke Balls (27)
- `ConfigTeraType.java` - Tera types (19)
- `ConfigRegionType.java` - Regions (9)
- `ConfigGenderType.java` - Genders (3)
- `ConfigMegaFormType.java` - Mega forms (4)

**Selector Screens (`client/gui/selectors/`):**
- `AbstractSelectorScreen.java` - Generic base class
- `SelectTypeScreen.java` - Extends AbstractSelectorScreen
- `SelectNatureScreen.java` - Shows stat boost/penalty
- `SelectBiomeScreen.java` - Loads from Minecraft registry
- `SelectPokemonScreen.java` - Generation-based grouping, Cobblemon API reflection
- `SelectFormScreen.java` - Form category grouping
- `SelectTeraTypeScreen.java` - 18 types + stellar
- `SelectRegionScreen.java` - 9 Pokemon regions
- `SelectGenderScreen.java` - Male/Female/Genderless
- `SelectMegaFormScreen.java` - Mega/X/Y/Primal
- `SelectDimensionScreen.java` - Registry-based
- `SelectPokeBallScreen.java` - 27 balls with colors

---

## Phase 4: Live Preview + UI Integration ✅ COMPLETE

**Goal:** Natural language preview + complete Phase 3 integration + validation feedback
**Risk:** MODERATE
**Version:** 1.5.0
**Status:** Released 2025-12-13

### Deliverables Completed

#### Part A: Complete Phase 3 Integration ✅

1. **ConfigValue types wired to CobblemonTask.fillConfigGroup()**
   - Replaced `StringConfig` with new `Config*Type` classes
   - All selector screens now open when clicking fields

2. **All 11 selector screens tested and working**
   - Pokemon (flat alphabetical list), Type, Nature, Biome, Dimension, Region, Gender, MegaForm, Form, TeraType, PokeBall

#### Part B: Validation Feedback Panel ✅

3. **LivePreviewPanel** - Combined preview + validation widget
   - Shows on RIGHT side of edit screen (side-by-side layout)
   - Real-time validation errors in red
   - Real-time validation warnings in yellow
   - Word wrapping for all content sections
   - Updates as fields change

4. **CobblemonTaskEditScreen Integration**
   - Custom `alignWidgets()` for side-by-side layout
   - Config panel on left, preview on right
   - Validation results passed to panel in real-time

#### Part C: Natural Language Preview ✅

5. **NaturalLanguageGenerator** - Converts task model → English
   - Human-readable quest requirement description
   - Supports all 28 actions and 19 condition fields
   - Handles pluralization and grammar
   - Shows active conditions only

### Files Created

**Preview System (`preview/`):**
- `NaturalLanguageGenerator.java` - Text generation engine with action-specific formatting
- `LivePreviewPanel.java` - Combined preview + validation widget with word wrap
- `ConditionFormatter.java` - Format individual conditions for display

**Registry (`registry/`):**
- `PokemonListCategory.java` - Simple single-category enum for flat Pokemon list

### Files Modified

- `tasks/CobblemonTask.java` - Replaced StringConfig with Config*Type classes
- `client/gui/CobblemonTaskEditScreen.java` - Side-by-side layout with LivePreviewPanel
- `client/gui/selectors/SelectPokemonScreen.java` - Simplified to flat alphabetical list
- `lang/en_us.json` - Added preview translation keys

### UI Improvements

- **Collapsible config groups** - Action, Pokemon, Level, Location, Capture, Pokedex, Gimmicks
- **Side-by-side layout** - Config fields on left, live preview on right
- **Word wrapping** - All preview panel sections wrap text properly
- **Flat Pokemon list** - Removed generation grouping, pure alphabetical

---

## Phase 5: Web Editor 🔲 PLANNED

**Goal:** Standalone quest creator for easier editing
**Risk:** HIGH
**Target Version:** 2.0.0

### Problem

Editing quests in-game is cumbersome. Server admins want to:
- Edit quests without launching Minecraft
- Share quest templates with other servers
- Bulk edit multiple quests
- Preview quests before importing

### Deliverables

1. **React/TypeScript web app**
   - Modern, responsive UI
   - Offline-capable (PWA)
   - No server required (static hosting)

2. **.snbt parser in TypeScript**
   - Parse FTB Quests chapter files
   - Handle all Cobblemon task fields
   - Preserve non-Cobblemon quest data

3. **Form builder matching in-game UX**
   - Same categories and field organization
   - Rich tooltips and descriptions
   - Validation with helpful errors

4. **Export/import functionality**
   - Export to .snbt file
   - Import existing chapters
   - Copy/paste individual quests

5. **Shareable quest templates**
   - Template library
   - Share via URL
   - Community contributions

### Project Structure

```
cobblemon-quest-editor/
├── src/
│   ├── components/
│   │   ├── ActionPicker/
│   │   ├── ConditionFields/
│   │   ├── Preview/
│   │   └── QuestTree/
│   ├── parser/
│   │   ├── snbt-parser.ts
│   │   └── snbt-serializer.ts
│   ├── models/
│   │   ├── quest.ts
│   │   └── task.ts
│   ├── validation/
│   │   └── task-validator.ts
│   └── templates/
│       └── quest-templates.ts
├── public/
└── package.json
```

### Tech Stack

- **Framework:** React 18+ with TypeScript
- **UI:** Tailwind CSS + shadcn/ui
- **State:** Zustand or Jotai
- **Build:** Vite
- **Hosting:** GitHub Pages or Vercel

---

## Phase 6: CLI/API 🔲 PLANNED

**Goal:** Programmatic quest generation
**Risk:** LOW
**Target Version:** 2.1.0

### Problem

Modpack developers and server admins want to:
- Generate quests from data (spreadsheets, databases)
- Create quest variations automatically
- Integrate with CI/CD pipelines
- Script bulk operations

### Deliverables

1. **Java API for quest building**
   - Fluent builder API
   - Type-safe task construction
   - Validation included

2. **CLI tool for bulk operations**
   - Generate quests from JSON/YAML
   - Validate chapter files
   - Convert between formats

3. **Template system**
   - Parameterized templates
   - Variable substitution
   - Loop generation

4. **Documentation**
   - API reference
   - CLI usage guide
   - Template syntax guide

### API Example

```java
// Java API usage
CobblemonQuest quest = CobblemonQuest.builder()
    .title("Catch a Shiny Starter")
    .description("Find and catch a shiny starter Pokemon!")
    .task(CobblemonTask.builder()
        .action("catch")
        .pokemon("bulbasaur", "charmander", "squirtle")
        .shiny(true)
        .amount(1)
        .build())
    .reward(ItemReward.of("minecraft:diamond", 5))
    .build();

quest.writeTo(Path.of("chapter.snbt"));
```

### CLI Example

```bash
# Generate quests from template
cobblemon-quests generate \
  --template catch-all-pokemon.yml \
  --data pokemon-list.csv \
  --output chapters/pokedex/

# Validate chapter file
cobblemon-quests validate chapter.snbt

# Convert JSON to SNBT
cobblemon-quests convert --from json --to snbt quest.json
```

### Project Structure

```
cobblemon-quests-cli/
├── src/main/java/
│   ├── api/
│   │   ├── CobblemonQuest.java
│   │   ├── CobblemonTask.java
│   │   └── builders/
│   ├── cli/
│   │   ├── GenerateCommand.java
│   │   ├── ValidateCommand.java
│   │   └── ConvertCommand.java
│   └── templates/
│       ├── TemplateEngine.java
│       └── TemplateParser.java
├── src/main/resources/
│   └── templates/
└── build.gradle.kts
```

---

## Version Planning

| Version | Phase | Key Features |
|---------|-------|--------------|
| 1.2.0 | Phase 1 | Action Picker UI, tooltips, categories |
| 1.3.0 | Phase 2 | Smart field visibility, field groups |
| 1.4.0 | Phase 3 | Domain model, validation (infrastructure only) |
| 1.5.0 | Phase 4 | Live preview, ConfigValue integration, validation UI |
| 2.0.0 | Phase 5 | Web editor |
| 2.1.0 | Phase 6 | CLI/API |

---

## Contributing

Want to help? Each phase has specific contribution opportunities:

- **Phase 2:** Field visibility rules for all 28 actions
- **Phase 3:** Validation rules, edge case testing
- **Phase 4:** Natural language templates for all conditions
- **Phase 5:** React component development, .snbt parser
- **Phase 6:** CLI commands, template system

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.
