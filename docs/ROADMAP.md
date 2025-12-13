# Quest Visual Designer - Implementation Roadmap

This roadmap outlines the phased development plan for enhancing the quest creation experience in Cobblemon Quests Extended.

## Overview

| Phase | Name | Status | Risk | Timeline |
|-------|------|--------|------|----------|
| 1 | Foundation | ✅ Complete | LOW | v1.2.0 |
| 2 | Smart Conditional Fields | ✅ Complete | MODERATE | v1.3.0 |
| 3 | Domain Model + Validation | 🔲 Planned | MODERATE | v1.4.0 |
| 4 | Live Preview | 🔲 Planned | LOW | v1.5.0 |
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

## Phase 3: Enhanced UI + Domain Model + Validation 🔲 PLANNED

**Goal:** Professional UI for all condition fields, clean data separation, validation
**Risk:** MODERATE
**Target Version:** 1.4.0

### Reference Documents

- **[CONDITIONAL_FIELD_ANALYSIS.md](CONDITIONAL_FIELD_ANALYSIS.md)** - Comprehensive analysis of all 19 conditional fields, action-by-action visibility matrix for 28 actions, and proposed implementation

### Problems

1. **Inconsistent UI**: Actions have a beautiful categorized picker with tooltips, but all other list fields (Pokemon, Types, Biomes, etc.) still use basic FTB Library list editors
2. **Poor organization**: The conditionals screen shows all fields in a flat list without logical grouping
3. **No tooltips**: Condition fields lack helpful descriptions explaining what they do
4. **Tight coupling**: Quest data is coupled to FTB Library's ConfigGroup, making validation and testing difficult
5. **Incorrect field visibility**: Many fields shown when they shouldn't be (see analysis doc for full matrix)

### Deliverables

#### UI Enhancement

1. **Redesigned Conditionals Screen**
   - Organized field groups with collapsible sections
   - Logical grouping: Pokemon Filters, Location Filters, Battle Gimmicks, etc.
   - Tooltips on every field explaining its purpose and valid values
   - Visual hierarchy matching the Action Picker quality

2. **Enhanced List Selectors** - Apply Action Picker pattern to all lists:
   - **Pokemon Selector**: Categorized by generation/type, search, sprites, sort options (Alphabetical / Pokédex Number)
   - **Type Selector**: All 18 types with color coding and icons
   - **Biome Selector**: Categorized biomes with descriptions
   - **Dimension Selector**: Available dimensions with icons
   - **Region Selector**: Cobblemon regions (Kanto, Johto, etc.)
   - **Nature Selector**: All 25 natures with stat effects shown
   - **PokeBall Selector**: All balls with sprites and descriptions
   - **Gender Selector**: Male/Female/Genderless with icons
   - **Tera Type Selector**: Types with tera crystal styling
   - **Mega Form Selector**: Mega/Mega-X/Mega-Y/Primal options
   - **Form Selector**: Species-specific forms with previews

#### Domain Model

3. **CobblemonTaskModel** - Domain object (not UI-coupled)
   - Pure data class with typed fields
   - No dependency on FTB Library or Minecraft
   - Builder pattern for construction

4. **TaskValidator** - Validate incompatible combinations
   - Check action + condition compatibility
   - Validate required fields for specific actions
   - Return user-friendly error messages

5. **TaskModelSerializer** - Convert model ↔ NBT
   - Serialize model to NBT for storage
   - Deserialize NBT to model
   - Handle version migrations

6. **Validation feedback in GUI**
   - Real-time validation as fields change
   - Error indicators on invalid fields
   - Tooltip with validation error message

### Files

**New (UI):**
- `client/gui/CobblemonTaskScreen.java` - Redesigned main config screen
- `client/gui/selectors/SelectPokemonScreen.java` - Pokemon picker
- `client/gui/selectors/SelectTypeScreen.java` - Type picker
- `client/gui/selectors/SelectBiomeScreen.java` - Biome picker
- `client/gui/selectors/SelectNatureScreen.java` - Nature picker
- `client/gui/selectors/SelectPokeBallScreen.java` - PokeBall picker
- `client/gui/selectors/SelectFormScreen.java` - Form picker
- `client/config/Config*Type.java` - Custom ConfigValue types for each selector

**New (Domain):**
- `model/CobblemonTaskModel.java` - Domain model
- `model/CobblemonTaskModelBuilder.java` - Builder for model
- `validation/TaskValidator.java` - Validation logic
- `validation/ValidationResult.java` - Validation result container
- `serialization/TaskModelSerializer.java` - NBT serialization
- `client/gui/ValidationFeedback.java` - GUI validation indicators

**Modified:**
- `tasks/CobblemonTask.java` - Use model internally, new config types
- `client/config/ConfigActionType.java` - Show validation errors
- `lang/en_us.json` - Tooltips and descriptions for all fields

### Validation Rules Examples

```java
// Example validation rules
public class TaskValidator {
    public ValidationResult validate(CobblemonTaskModel model) {
        List<String> errors = new ArrayList<>();

        // Action-specific validations
        if (model.getAction().contains("mega_evolve") && model.getMegaForm().isEmpty()) {
            errors.add("Mega Evolution requires a mega_form (mega, mega-x, mega-y, or primal)");
        }

        if (model.getAction().contains("terastallize") && model.getTeraType().isEmpty()) {
            errors.add("Terastallize requires a tera_type");
        }

        // Incompatible combinations
        if (model.getAction().contains("defeat_npc") && !model.getPokemon().isEmpty()) {
            errors.add("defeat_npc ignores pokemon filter - use form field for NPC names");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
```

---

## Phase 4: Live Preview 🔲 PLANNED

**Goal:** Natural language preview of quest requirements
**Risk:** LOW
**Target Version:** 1.5.0

### Problem

Quest creators can't easily see what their quest will require without testing in-game. The current config fields are technical and don't clearly communicate the end result.

### Deliverables

1. **NaturalLanguageGenerator** - Converts task model → English
   - Human-readable quest requirement description
   - Supports all actions and conditions
   - Handles pluralization and grammar

2. **Preview panel widget in FTB-Quests GUI**
   - Shows below the config fields
   - Updates in real-time as fields change
   - Clear visual separation from config

3. **Real-time updates**
   - Subscribe to field changes
   - Debounce rapid changes
   - Smooth text transitions

### Files

**New:**
- `preview/NaturalLanguageGenerator.java` - Text generation
- `preview/PreviewPanel.java` - GUI widget
- `preview/PreviewUpdateListener.java` - Change listener

### Example Output

```
Quest Requirement Preview:
━━━━━━━━━━━━━━━━━━━━━━━━━━━
"Catch 5 shiny Pikachu or Raichu using a Premier Ball
in the Plains biome during daytime."

Conditions:
• Species: Pikachu, Raichu
• Must be shiny
• Ball: Premier Ball
• Biome: Plains
• Time: Day (0-12000 ticks)
```

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
| 1.4.0 | Phase 3 | Domain model, validation |
| 1.5.0 | Phase 4 | Live preview, natural language |
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
