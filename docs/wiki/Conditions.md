# Conditions

Conditions (also called filters) specify **which Pokémon** count toward quest completion. All conditions stack—a Pokémon must match **all** specified conditions to increment quest progress.

## How Conditions Work

When multiple conditions are set, they combine with **AND** logic:

```yml
{
    action: "catch"
    amount: 1L
    pokemon: "pikachu"
    shiny: true
    min_level: 25
}
```

This quest requires catching a Pokémon that is:
- A Pikachu **AND**
- Shiny **AND**
- Level 25 or higher

## Core Conditions

These conditions are available in all versions of Cobblemon.

### Actions

| Field | Type | Description |
|-------|------|-------------|
| `action` | List<String> | The action(s) that trigger the quest (see [Actions](Actions)) |

**Example:**
```yml
action: "catch, defeat"
```

Multiple actions are separated by commas. The quest increments if **any** of the listed actions occur.

### Amount

| Field | Type | Description |
|-------|------|-------------|
| `amount` | long | How many times the action must be completed |

**Example:**
```yml
amount: 10L
```

### Pokémon Species

| Field | Type | Description |
|-------|------|-------------|
| `pokemon` | List<String> | Specific Pokémon species that count for the quest |

**Format:** `<namespace>:<pokemon>` (namespace defaults to `cobblemon` if omitted)

**Examples:**
```yml
# Single Pokémon
pokemon: "pikachu"

# Multiple Pokémon
pokemon: "pikachu, raichu, pichu"

# With namespace
pokemon: "cobblemon:charizard"

# Any Pokémon (leave empty)
pokemon: ""
```

### Forms

| Field | Type | Description |
|-------|------|-------------|
| `form` | List<String> | Pokémon forms or aspects (also used for custom aspects) |

**Examples:**
```yml
# Galarian form
form: "galarian"

# Alolan form
form: "alolan"

# Hisuian form
form: "hisuian"

# Multiple forms
form: "galarian, alolan"

# Custom aspects
form: "custom_aspect"
```

**Special Cases:**
- For `defeat_player` and `defeat_npc`, this field contains **player/NPC names** (case-sensitive)
- See [Getting Started - Custom Aspects](Getting-Started#custom-aspects) for custom aspect usage

### Genders

| Field | Type | Description |
|-------|------|-------------|
| `gender` | List<String> | The gender of the Pokémon |

**Values:** `male`, `female`, `genderless`

**Examples:**
```yml
# Only male
gender: "male"

# Male or female
gender: "male, female"

# Genderless Pokémon
gender: "genderless"
```

### Pokémon Types

| Field | Type | Description |
|-------|------|-------------|
| `pokemon_type` | List<String> | The type(s) of the Pokémon |

**Values:**
`normal`, `fire`, `water`, `grass`, `electric`, `ice`, `fighting`, `poison`, `ground`, `flying`, `psychic`, `bug`, `rock`, `ghost`, `dragon`, `dark`, `steel`, `fairy`

**Examples:**
```yml
# Single type
pokemon_type: "fire"

# Multiple types
pokemon_type: "fire, dragon"

# Any dual-type with Water
pokemon_type: "water"
```

**Note:** A Pokémon matches if it has **any** of the specified types (OR logic).

### Shiny

| Field | Type | Description |
|-------|------|-------------|
| `shiny` | boolean | Whether the Pokémon must be shiny |

**Examples:**
```yml
# Must be shiny
shiny: true

# Must NOT be shiny (or any)
shiny: false
```

### Min/Max Level

| Field | Type | Description |
|-------|------|-------------|
| `min_level` | int | Minimum Pokémon level (inclusive) |
| `max_level` | int | Maximum Pokémon level (inclusive) |

**Examples:**
```yml
# Level 50 or higher
min_level: 50
max_level: 0

# Level 30-40 (inclusive)
min_level: 30
max_level: 40

# Exactly level 25
min_level: 25
max_level: 25

# Any level
min_level: 0
max_level: 0
```

### Regions

| Field | Type | Description |
|-------|------|-------------|
| `region` | List<String> | The region/generation the Pokémon is from |

**Values:**
`kanto`, `johto`, `hoenn`, `sinnoh`, `unova`, `kalos`, `alola`, `galar`, `hisui`, `paldea`

**Examples:**
```yml
# Kanto Pokémon only
region: "kanto"

# Gen 1 or Gen 2
region: "kanto, johto"

# Custom region (requires manual editing)
region: "my_custom_region"
```

### Biomes

| Field | Type | Description |
|-------|------|-------------|
| `biome` | List<String> | The biome the player must be in when the action occurs |

**Format:** `<namespace>:<biome>`

**Examples:**
```yml
# Vanilla biomes
biome: "minecraft:plains"

# Multiple biomes
biome: "minecraft:plains, minecraft:forest"

# Cobblemon biomes
biome: "cobblemon:volcanic"

# Custom mod biomes
biome: "custom_mod:biome"
```

### Dimensions

| Field | Type | Description |
|-------|------|-------------|
| `dimension` | List<String> | The dimension the player must be in when the action occurs |

**Common Values:**
- `minecraft:overworld`
- `minecraft:the_nether`
- `minecraft:the_end`

**Examples:**
```yml
# Nether only
dimension: "minecraft:the_nether"

# Nether or End
dimension: "minecraft:the_nether, minecraft:the_end"

# Custom dimension
dimension: "custom_mod:dimension"
```

### Time of Day

| Field | Type | Description |
|-------|------|-------------|
| `time_min` | long | Minimum time of day (in ticks) |
| `time_max` | long | Maximum time of day (in ticks) |

**Range:** `time_min <= time < time_max`

**Common Time Values:**
- `0` - Sunrise (6:00 AM)
- `6000` - Noon (12:00 PM)
- `12000` - Sunset (6:00 PM)
- `18000` - Midnight (12:00 AM)
- `24000` - End of day cycle

**Examples:**
```yml
# Daytime only (6 AM - 6 PM)
time_min: 0L
time_max: 12000L

# Nighttime only (6 PM - 6 AM)
time_min: 12000L
time_max: 24000L

# Any time
time_min: 0L
time_max: 24000L
```

### Poké Balls Used

| Field | Type | Description |
|-------|------|-------------|
| `poke_ball_used` | List<String> | The Poké Ball used to catch or contains the Pokémon |

**Common Values:**
`poke_ball`, `great_ball`, `ultra_ball`, `master_ball`, `safari_ball`, `level_ball`, `lure_ball`, `moon_ball`, `friend_ball`, `love_ball`, `heavy_ball`, `fast_ball`, `sport_ball`, `premier_ball`, `repeat_ball`, `timer_ball`, `nest_ball`, `net_ball`, `dive_ball`, `luxury_ball`, `heal_ball`, `quick_ball`, `dusk_ball`, `cherish_ball`, `park_ball`, `dream_ball`, `beast_ball`

**Examples:**
```yml
# Must be caught with Master Ball
poke_ball_used: "master_ball"

# Any Ultra Ball or Master Ball
poke_ball_used: "ultra_ball, master_ball"

# Apricorn balls
poke_ball_used: "level_ball, lure_ball, moon_ball"
```

**Note:** This condition also applies to `trade` and `evolve` actions, checking the ball the Pokémon is currently in.

### Pokédex Progress

| Field | Type | Description |
|-------|------|-------------|
| `dex_progress` | String | Level of Pokédex registration required |

**Values:** `seen`, `caught`

**Examples:**
```yml
# Must be registered as caught
dex_progress: "caught"

# Only needs to be seen
dex_progress: "seen"
```

**Used With:** `register` and `have_registered` actions

## Extended Conditions (Cobblemon 1.7.0+)

These conditions require **Cobblemon 1.7.0 or later**.

### Tera Type

| Field | Type | Description |
|-------|------|-------------|
| `tera_type` | List<String> | The Tera type of the Pokémon |

**Values:**
`normal`, `fire`, `water`, `grass`, `electric`, `ice`, `fighting`, `poison`, `ground`, `flying`, `psychic`, `bug`, `rock`, `ghost`, `dragon`, `dark`, `steel`, `fairy`, `stellar`

**Examples:**
```yml
# Fire Tera type
tera_type: "fire"

# Fire or Dragon
tera_type: "fire, dragon"
```

**Used With:** `terastallize` action

### Mega Form

| Field | Type | Description |
|-------|------|-------------|
| `mega_form` | List<String> | The Mega Evolution form type |

**Values:** `mega`, `mega-x`, `mega-y`, `primal`

**Examples:**
```yml
# Any Mega Evolution
mega_form: "mega"

# Mega Charizard X specifically
pokemon: "charizard"
mega_form: "mega-x"

# Primal forms (Groudon/Kyogre)
mega_form: "primal"
```

**Used With:** `mega_evolve` action

### Z-Crystal

| Field | Type | Description |
|-------|------|-------------|
| `z_crystal` | List<String> | The Z-Crystal used for the Z-Move |

**Type-based Z-Crystals:**
`normalium-z`, `firium-z`, `waterium-z`, `grassium-z`, `electrium-z`, `icium-z`, `fightinium-z`, `poisonium-z`, `groundium-z`, `flyinium-z`, `psychium-z`, `buginium-z`, `rockium-z`, `ghostium-z`, `dragonium-z`, `darkinium-z`, `steelium-z`, `fairium-z`

**Pokémon-specific Z-Crystals:**
`aloraichium-z`, `decidium-z`, `eevium-z`, `incinium-z`, `kommonium-z`, `lunalium-z`, `lycanium-z`, `marshadium-z`, `mewnium-z`, `mimikium-z`, `pikanium-z`, `pikashunium-z`, `primarium-z`, `snorlium-z`, `solganium-z`, `tapunium-z`, `ultranecrozium-z`

**Examples:**
```yml
# Fire-type Z-Move
z_crystal: "firium-z"

# Pikachu's exclusive Z-Move
z_crystal: "pikanium-z"

# Partner Cap Pikachu's Z-Move
pokemon: "pikachu"
form: "partner-cap"
z_crystal: "pikashunium-z"
```

**Used With:** `use_z_move` action

### Dynamax Type (Mega Showdown)

| Field | Type | Description |
|-------|------|-------------|
| `dynamax_type` | List<String> | The Dynamax form type |

**Values:** `dynamax`, `gigantamax`

**Examples:**
```yml
# Regular Dynamax
dynamax_type: "dynamax"

# Gigantamax only
dynamax_type: "gigantamax"
```

**Used With:** `dynamax`, `gigantamax` actions (requires Mega Showdown mod)

## Condition Combination Examples

### Catch a Shiny Starter in the Overworld During Day
```yml
{
    action: "catch"
    amount: 1L
    pokemon: "bulbasaur, charmander, squirtle"
    shiny: true
    dimension: "minecraft:overworld"
    time_min: 0L
    time_max: 12000L
    type: "cobblemon_tasks:cobblemon_task"
}
```

### Defeat High-Level Dragon Types
```yml
{
    action: "defeat"
    amount: 10L
    pokemon_type: "dragon"
    min_level: 50
    max_level: 0
    type: "cobblemon_tasks:cobblemon_task"
}
```

### Hatch Shiny Baby Pokémon
```yml
{
    action: "hatch_egg"
    amount: 1L
    pokemon: "pichu, cleffa, igglybuff, togepi, tyrogue, smoochum, elekid, magby"
    shiny: true
    type: "cobblemon_tasks:cobblemon_task"
}
```

### Catch Alolan Forms with Luxury Ball
```yml
{
    action: "catch"
    amount: 5L
    form: "alolan"
    poke_ball_used: "luxury_ball"
    type: "cobblemon_tasks:cobblemon_task"
}
```

### Mega Evolve Specific Pokémon
```yml
{
    action: "mega_evolve"
    amount: 1L
    pokemon: "charizard"
    mega_form: "mega-x"
    type: "cobblemon_tasks:cobblemon_task"
}
```

## Condition Tips

### Empty vs. Specified
- **Empty field** - No restriction (matches all)
- **Specified field** - Must match one of the listed values

### List Behavior
For list fields (pokemon, types, forms, etc.), a Pokémon matches if it has **any** of the specified values:

```yml
pokemon: "pikachu, raichu"
```
Matches if the Pokémon is Pikachu **OR** Raichu.

### Combining Lists
When combining multiple list conditions, the Pokémon must match **at least one value** from **each list**:

```yml
pokemon: "pikachu, raichu"
pokemon_type: "electric"
```
Matches Electric-type Pokémon that are **either** Pikachu **or** Raichu.

### Level Ranges
- If `min_level = 0` and `max_level = 0`, **any level** is accepted
- If both are set to the **same non-zero value**, the Pokémon must be **exactly** that level
- Otherwise, the Pokémon's level must be **between** `min_level` and `max_level` (inclusive)

## See Also

- **[Actions](Actions)** - What actions trigger quest progress
- **[Examples](Examples)** - Complex quest examples combining conditions
- **[Getting Started](Getting-Started)** - Basic setup and installation
- **[Action Picker](Action-Picker)** - UI for configuring conditions in v1.2.0+
