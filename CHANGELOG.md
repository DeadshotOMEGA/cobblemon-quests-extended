# Changelog

All notable changes to Cobblemon Quests Extended will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

[1.2.0]: https://github.com/yourusername/cobblemon_quests_extended/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/yourusername/cobblemon_quests_extended/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/yourusername/cobblemon_quests_extended/releases/tag/v1.0.0
