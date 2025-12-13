# Cobblemon Task Conditional Field Analysis

This document analyzes all conditional fields in CobblemonTask and defines when each should be visible based on selected actions.

## Current Fields Inventory

### Always Required
| Field | Type | Description |
|-------|------|-------------|
| `actions` | List<String> | Selected action types (MUST always show) |

### Basic Condition Fields (Currently Always Shown)
| Field | Type | Description | Should Always Show? |
|-------|------|-------------|---------------------|
| `amount` | long | Quantity required | YES - universal |
| `shiny` | boolean | Require shiny Pokemon | NO - only for Pokemon-involving actions |
| `pokemons` | List<String> | Species filter | NO - only for Pokemon-involving actions |
| `pokemonTypes` | List<String> | Type filter | NO - only for Pokemon-involving actions |
| `natures` | List<String> | Nature filter | NO - only for Pokemon-involving actions |
| `regions` | List<String> | Region filter (Kanto, etc.) | NO - only for Pokemon-involving actions |

### Level Fields (Currently Conditional)
| Field | Type | Current Visibility | Correct? |
|-------|------|-------------------|----------|
| `minLevel` | int | level_up, level_up_to | YES |
| `maxLevel` | int | level_up, level_up_to | YES |

### Pokédex Fields (Currently Conditional)
| Field | Type | Current Visibility | Correct? |
|-------|------|-------------------|----------|
| `dexProgress` | String | register, have_registered, scan | YES |

### Location/Time Fields (Currently Conditional)
| Field | Type | Current Visibility | Should Show For |
|-------|------|-------------------|-----------------|
| `timeMin` | long | catch/battle actions | catch, reel, hatch_egg, defeat (wild) |
| `timeMax` | long | catch/battle actions | catch, reel, hatch_egg, defeat (wild) |
| `biomes` | List<String> | catch/battle actions | catch, reel, defeat (wild) |
| `dimensions` | List<String> | catch/battle actions | catch, reel, defeat (wild), hatch_egg |
| `pokeBallsUsed` | List<String> | catch/battle actions | catch ONLY |

### Form Fields (Currently Conditional)
| Field | Type | Current Visibility | Needs Review |
|-------|------|-------------------|--------------|
| `forms` | List<String> | catch, obtain, evolution, pokedex | Also used for NPC names in defeat_npc |
| `genders` | List<String> | any action selected | Should be more restricted |

### Battle Gimmick Fields (Currently Conditional)
| Field | Type | Current Visibility | Correct? |
|-------|------|-------------------|----------|
| `megaForms` | List<String> | mega_evolve | YES |
| `teraTypes` | List<String> | terastallize | YES |
| `zCrystals` | List<String> | use_z_move | YES |
| `dynamaxTypes` | List<String> | dynamax, gigantamax, ultra_burst | YES |

---

## Action-by-Action Field Matrix

### Legend
- ✅ = Should be visible
- ❌ = Should be hidden
- ⚠️ = Special case (see notes)

| Field | catch | defeat | defeat_npc | defeat_player | evolve | evolve_into | faint_pokemon | hatch_egg |
|-------|-------|--------|------------|---------------|--------|-------------|---------------|-----------|
| amount | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| shiny | ✅ | ✅ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ |
| pokemons | ✅ | ✅ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ |
| pokemonTypes | ✅ | ✅ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ |
| natures | ✅ | ✅ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ |
| regions | ✅ | ✅ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ |
| minLevel | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| maxLevel | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| dexProgress | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| timeMin | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| timeMax | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| biomes | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| dimensions | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| pokeBallsUsed | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| forms | ✅ | ✅ | ⚠️ | ❌ | ✅ | ✅ | ✅ | ✅ |
| genders | ✅ | ✅ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ |
| megaForms | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| teraTypes | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| zCrystals | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| dynamaxTypes | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |

| Field | have_registered | kill | level_up | level_up_to | obtain | reel | register | revive_fossil |
|-------|-----------------|------|----------|-------------|--------|------|----------|---------------|
| amount | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| shiny | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| pokemons | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| pokemonTypes | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| natures | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| regions | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| minLevel | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| maxLevel | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| dexProgress | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ |
| timeMin | ❌ | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ |
| timeMax | ❌ | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ |
| biomes | ❌ | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ |
| dimensions | ❌ | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ |
| pokeBallsUsed | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ |
| forms | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| genders | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| megaForms | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| teraTypes | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| zCrystals | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| dynamaxTypes | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |

| Field | scan | select_starter | trade_away | trade_for | change_form |
|-------|------|----------------|------------|-----------|-------------|
| amount | ✅ | ✅ | ✅ | ✅ | ✅ |
| shiny | ✅ | ✅ | ✅ | ✅ | ✅ |
| pokemons | ✅ | ✅ | ✅ | ✅ | ✅ |
| pokemonTypes | ✅ | ✅ | ✅ | ✅ | ✅ |
| natures | ✅ | ✅ | ✅ | ✅ | ✅ |
| regions | ✅ | ✅ | ✅ | ✅ | ✅ |
| minLevel | ❌ | ❌ | ❌ | ❌ | ❌ |
| maxLevel | ❌ | ❌ | ❌ | ❌ | ❌ |
| dexProgress | ✅ | ❌ | ❌ | ❌ | ❌ |
| timeMin | ❌ | ❌ | ❌ | ❌ | ❌ |
| timeMax | ❌ | ❌ | ❌ | ❌ | ❌ |
| biomes | ❌ | ❌ | ❌ | ❌ | ❌ |
| dimensions | ❌ | ❌ | ❌ | ❌ | ❌ |
| pokeBallsUsed | ❌ | ❌ | ❌ | ❌ | ❌ |
| forms | ✅ | ✅ | ✅ | ✅ | ✅ |
| genders | ✅ | ✅ | ✅ | ✅ | ✅ |
| megaForms | ❌ | ❌ | ❌ | ❌ | ❌ |
| teraTypes | ❌ | ❌ | ❌ | ❌ | ❌ |
| zCrystals | ❌ | ❌ | ❌ | ❌ | ❌ |
| dynamaxTypes | ❌ | ❌ | ❌ | ❌ | ❌ |

| Field | mega_evolve | terastallize | use_z_move | dynamax | gigantamax | ultra_burst |
|-------|-------------|--------------|------------|---------|------------|-------------|
| amount | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| shiny | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| pokemons | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| pokemonTypes | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| natures | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| regions | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| minLevel | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| maxLevel | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| dexProgress | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| timeMin | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| timeMax | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| biomes | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| dimensions | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| pokeBallsUsed | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| forms | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| genders | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| megaForms | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| teraTypes | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ |
| zCrystals | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ |
| dynamaxTypes | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |

---

## Special Cases & Notes

### defeat_npc
- Uses `forms` field to specify NPC trainer names (not Pokemon forms)
- Should hide all Pokemon-related filters
- Only show: amount, forms (renamed to "NPC Names" in UI?)

### defeat_player
- Player vs Player battle
- Minimal filters needed - just amount
- Could potentially filter by Pokemon used, but not typically needed

### obtain (umbrella action)
- Triggers for: catch, evolve_into, trade_for, revive_fossil
- Should show union of all fields those actions need

### reel (fishing)
- Location-dependent (biome, dimension, time)
- Uses pokeball for the catch
- Full Pokemon filters

---

## Recommended Visibility Rule Groups

### Group 1: Pokemon Filters
**Fields:** pokemons, pokemonTypes, shiny, natures, regions, genders, forms
**Show when:** Any action EXCEPT defeat_npc, defeat_player

### Group 2: Location Filters
**Fields:** biomes, dimensions, timeMin, timeMax
**Show when:** catch, defeat, kill, faint_pokemon, reel, hatch_egg

### Group 3: Capture Filters
**Fields:** pokeBallsUsed
**Show when:** catch, reel ONLY

### Group 4: Level Filters
**Fields:** minLevel, maxLevel
**Show when:** level_up, level_up_to ONLY

### Group 5: Pokédex Filters
**Fields:** dexProgress
**Show when:** register, have_registered, scan ONLY

### Group 6: Battle Gimmicks
**Fields:** megaForms, teraTypes, zCrystals, dynamaxTypes
**Show when:** Their respective actions ONLY (already correct)

---

## Current Implementation Issues

### Issue 1: Basic fields always shown
**Problem:** shiny, pokemons, pokemonTypes, natures, regions are ALWAYS shown even when no action is selected or when action doesn't involve Pokemon (defeat_npc, defeat_player).

**Fix:** Only show these when at least one Pokemon-involving action is selected.

### Issue 2: Gender field too permissive
**Problem:** genders shown for any action, but doesn't make sense for defeat_npc, defeat_player, or gimmick actions.

**Fix:** Restrict to Pokemon-involving actions only.

### Issue 3: Pokeball filter too broad
**Problem:** pokeBallsUsed shown for all catch/battle actions, but only catch and reel actually use pokeballs.

**Fix:** Only show for catch, reel actions.

### Issue 4: No empty state
**Problem:** When no actions selected, all basic fields still show.

**Fix:** Show only actions field until at least one action is selected. Display helper text: "Select an action to see available conditions."

---

## Implementation Priority for Phase 3

1. **High Priority:** Fix empty state - hide everything except actions until action selected
2. **High Priority:** Fix Pokemon filters visibility (Issue 1)
3. **Medium Priority:** Fix pokeball filter scope (Issue 3)
4. **Medium Priority:** Fix gender filter scope (Issue 2)
5. **Low Priority:** Consider renaming "forms" to "NPC Names" when defeat_npc selected

---

## Proposed Static Sets for Phase 3

```java
// Actions that involve Pokemon (show Pokemon filters)
private static final Set<String> POKEMON_ACTIONS = Set.of(
    "catch", "obtain", "revive_fossil", "reel", "hatch_egg", "select_starter",
    "defeat", "kill", "faint_pokemon",
    "evolve", "evolve_into", "change_form",
    "register", "have_registered", "scan",
    "trade_away", "trade_for",
    "level_up", "level_up_to",
    "mega_evolve", "terastallize", "use_z_move",
    "dynamax", "gigantamax", "ultra_burst"
);

// Actions that DON'T involve Pokemon filters
private static final Set<String> NON_POKEMON_ACTIONS = Set.of(
    "defeat_npc", "defeat_player"
);

// Actions where location/time matters
private static final Set<String> LOCATION_ACTIONS = Set.of(
    "catch", "defeat", "kill", "faint_pokemon", "reel", "hatch_egg"
);

// Actions where pokeball matters
private static final Set<String> POKEBALL_ACTIONS = Set.of(
    "catch", "reel"
);

// Actions where nature/region matter (excludes gimmicks)
private static final Set<String> NATURE_REGION_ACTIONS = Set.of(
    "catch", "obtain", "revive_fossil", "reel", "hatch_egg", "select_starter",
    "defeat", "kill", "faint_pokemon",
    "evolve", "evolve_into", "change_form",
    "register", "have_registered", "scan",
    "trade_away", "trade_for",
    "level_up", "level_up_to"
);
```
