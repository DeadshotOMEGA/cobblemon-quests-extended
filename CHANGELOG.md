# Changelog

All notable changes to Cobblemon Quests Extended will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.5.0] - 2025-12-13

### Added
- **Live Preview Panel**: Real-time quest preview alongside config fields
  - Side-by-side layout with config on left, preview on right
  - Validation errors displayed in red with word wrapping
  - Validation warnings displayed in yellow
  - Updates automatically as you edit fields
- **Natural Language Generator**: Human-readable quest descriptions
  - Converts task configuration to plain English
  - Supports all 28 actions and 19 condition fields
  - Smart pluralization and grammar handling
  - Shows only active conditions
- **Enhanced Selector Screens**: All 11 custom selector screens fully integrated
  - Pokemon, Type, Nature, Biome, Dimension, Region, Gender, MegaForm, Form, TeraType, PokeBall
  - Rich category organization and color coding
  - Search filtering support
- **Collapsible Config Groups**: Organized field sections
  - Action, Pokemon, Level, Location, Capture, Pokedex, Gimmicks groups
  - Cleaner navigation through configuration options

### Changed
- Pokemon selector now displays flat alphabetical list (removed generation grouping)
- Improved name formatting for Pokemon with special characters (Mr. Mime, Ho-Oh, etc.)
- Config fields now use custom ConfigValue types with specialized selectors

### Technical
- Phase 4 implementation of the Quest Visual Designer roadmap
- `LivePreviewPanel` with custom layout management
- `NaturalLanguageGenerator` for quest text generation
- `ConditionFormatter` for individual condition display
- Custom `alignWidgets()` override for side-by-side screen layout

## [1.3.0] - 2025-12-13

### Added
- **Smart Conditional Fields**: Dynamic field visibility based on selected actions
  - Condition fields now show/hide based on which actions are selected
  - Level fields only appear for `level_up` and `level_up_to` actions
  - Pokédex progress only appears for `register`, `have_registered`, and `scan` actions
  - Time/location fields only appear for catch and battle actions
  - Form fields only appear for evolution and form-related actions
  - Mega/Tera/Z-Move/Dynamax fields only appear when relevant actions are selected
- **"Update Fields" Button**: Dynamic UI feedback when actions change
  - Accept button changes to "Update Fields" when actions are modified
  - Clicking updates the form to show/hide conditional fields
  - Clear UX indication that field visibility has changed
- Custom `CobblemonTaskEditScreen` for enhanced task configuration
- Custom `CobblemonTaskGuiProvider` for new task creation flow

### Technical
- Phase 2 implementation of the UX Improvement Roadmap
- Static visibility rule sets for efficient field filtering
- Recursive ConfigGroup traversal for action change detection

## [1.2.0] - 2025-12-13

### Added
- **Action Picker UI**: New categorized action selection screen with rich tooltips
  - Actions grouped by category: Obtaining Pokémon, Battle, Evolution, Trading, Pokédex, Battle Gimmicks, Other
  - Collapsible category sections for easier navigation
  - Tooltips showing action description, usage example, and requirements indicator
  - Category-specific color coding for visual clarity
- Action localization with descriptions and examples for all 28 actions
- `ActionDefinition` record extended with `descriptionKey` and `exampleKey` fields

### Fixed
- **Critical**: Fixed inability to add actions to quest tasks
  - Root cause: FTB Library's `EnumConfig` doesn't work with `addList()` method
  - Solution: Replaced with custom `ConfigActionType` that provides enhanced UI

## [1.1.0] - 2025-12-13

### Added
- Mega Showdown integration localization (i18n support for mega evolution actions)

### Changed
- Migrated FTB mods from CurseForge to official Maven repositories for improved reliability
- Updated Mega Showdown dependency

### Fixed
- NeoForge mod ID mismatch causing load failures
- Added missing action translations for quest tasks

## [1.0.0] - 2025-12-12

### Added
- Initial release of Cobblemon Quests Extended
- Extended FTB Quests integration for Cobblemon
- Support for mega evolution quest tasks
- Support for terastallization quest tasks
- Support for Z-move quest tasks
- Comprehensive quest creation documentation

[1.5.0]: https://github.com/DeadshotOMEGA/cobblemon-quests-extended/compare/v1.3.0...v1.5.0
[1.3.0]: https://github.com/DeadshotOMEGA/cobblemon-quests-extended/compare/v1.2.0...v1.3.0
[1.2.0]: https://github.com/DeadshotOMEGA/cobblemon-quests-extended/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/DeadshotOMEGA/cobblemon-quests-extended/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/DeadshotOMEGA/cobblemon-quests-extended/releases/tag/v1.0.0
