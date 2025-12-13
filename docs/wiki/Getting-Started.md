# Getting Started

This guide will help you install and configure Cobblemon Quests Extended.

## Installation

### Requirements

| Dependency | Version | Download |
|------------|---------|----------|
| Minecraft | 1.21.1 | [Minecraft](https://minecraft.net) |
| Cobblemon | 1.7.0+ | [Modrinth](https://modrinth.com/mod/cobblemon) |
| FTB Quests | Latest for 1.21.1 | [Modrinth](https://modrinth.com/mod/ftb-quests-fabric) |
| Fabric Loader | 0.18.1+ | [Fabric](https://fabricmc.net/use/) |
| Fabric API | 0.116.7+ | [Modrinth](https://modrinth.com/mod/fabric-api) |

### Optional Dependencies

| Mod | Purpose |
|-----|---------|
| [Cobblemon: Mega Showdown](https://modrinth.com/mod/cobblemon-mega-showdown) | Enables Dynamax, Gigantamax, and Ultra Burst quest actions |

### Download and Install

1. **Download the mod:**
   - Get the latest version from [Modrinth](https://modrinth.com/mod/cobblemon-quests-extended) or [GitHub Releases](https://github.com/DeadshotOMEGA/cobblemon-quests-extended/releases)

2. **Install on both client and server:**
   - Place the `.jar` file in your `mods/` folder
   - **Important:** Install on both client and server for multiplayer

3. **Do NOT install with original Cobblemon Quests:**
   - This mod **replaces** the original Cobblemon Quests
   - Remove `cobblemon_quests.jar` if present

## Basic Setup

### Creating Your First Quest

1. **Open FTB Quests:**
   - In-game, press the quests key (default: `Q`) or run `/ftbquests`

2. **Edit Mode:**
   - Click the edit button or run `/ftbquests edit_mode`

3. **Create a Chapter:**
   - Right-click and select "New Chapter"

4. **Create a Quest:**
   - Right-click in the chapter and select "New Quest"

5. **Add a Cobblemon Task:**
   - Click the quest, then "Add Task"
   - Select `cobblemon_tasks:cobblemon_task`

6. **Configure the Task:**
   - See the [Action Picker](Action-Picker) guide for using the new v1.2.0 UI
   - Or manually configure:
     - **Actions:** What the player must do (catch, defeat, evolve, etc.)
     - **Amount:** How many times to complete the action
     - **Conditions:** Filters for species, type, shiny, etc.

### Simple Example

**Quest:** Catch 3 Pikachu

```yml
{
    tasks: [{
        action: "catch"
        amount: 3L
        pokemon: "pikachu"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

## Configuration

### Config File Location

The configuration file is located at:
```
<minecraft_directory>/config/cobblemon_quests_extended/cobblemon_quests_extended.config
```

### Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `configVersion` | double | 1.0 | Config file version (do not modify) |
| `ignoredPokemon` | List | [] | Pokémon to exclude from quest tracking |
| `suppressWarnings` | boolean | false | Hide non-critical warnings in logs |

### Example Configuration

```
configVersion: 1.0
ignoredPokemon: pokemon1, pokemon2
suppressWarnings: false
```

## Commands

All commands require operator permissions (level 2).

### Blacklist Pokémon

Prevent specific Pokémon from counting towards quests:

```bash
/cobblemonquests blacklisted_pokemon add <pokemon>
/cobblemonquests blacklisted_pokemon remove <pokemon>
```

**Example:**
```bash
/cobblemonquests blacklisted_pokemon add pikachu
```

### Suppress Warnings

Toggle warning messages in the console:

```bash
/cobblemonquests suppress_warnings [true/false]
```

**Example:**
```bash
/cobblemonquests suppress_warnings true
```

### Give Pokémon (Quest Event)

Give a Pokémon and trigger quest events (unlike `/givepokemon`):

```bash
/cobblemonquests givepokemon <player> <should_give> <amount> {<actions>} <pokemon>
```

**Parameters:**
- `player` - Target player
- `should_give` - `true` to give Pokémon, `false` to only trigger actions
- `amount` - Progress amount (can be negative)
- `actions` - Comma-separated list (e.g., `{catch,defeat}`)
- `pokemon` - Pokémon builder (same syntax as `/givepokemon`)

**Example:**
```bash
/cobblemonquests givepokemon @s true 1 {catch,obtain} pikachu level=25 shiny=true
```

## Editing Chapter Files Manually

For advanced quest creation (custom regions, aspects, etc.), you can edit chapter files directly.

### Location

Quest files are stored in:
```
<minecraft_directory>/config/ftbquests/quests/chapters/
```

Each chapter is a `.snbt` file.

### Workflow

1. **Create a blank quest in-game:**
   - Use FTB Quests editor to create a basic quest
   - Right-click the quest and select "Copy ID"

2. **Close the game/server:**
   - Ensure changes won't be overwritten

3. **Edit the chapter file:**
   - Navigate to `config/ftbquests/quests/chapters/<chapter_name>.snbt`
   - Open with a text editor
   - Search for the quest ID you copied
   - Make your changes

4. **Restart and test:**
   - Launch the game/server
   - Verify your quest works

### Example Quest Structure

```yml
{
    id: "1234567890ABCDEF"
    tasks: [{
        action: "catch"
        amount: 1L
        biome: ""
        dimension: ""
        form: ""
        gender: ""
        id: "1234567890ABCDEF"
        poke_ball_used: ""
        pokemon: "pikachu"
        pokemon_type: ""
        region: ""
        shiny: false
        time_max: 24000L
        time_min: 0L
        type: "cobblemon_tasks:cobblemon_task"
    }]
    x: 0.0d
    y: 0.0d
}
```

## Custom Aspects

Custom aspects can be added to Pokémon using the `/spawnpokemon` command.

### Creating Pokémon with Custom Aspects

```bash
/spawnpokemon <pokemon> aspect=<aspect>
```

**Example:**
```bash
/spawnpokemon pikachu aspect=custom_aspect
```

### Quest for Custom Aspects

To create a quest that requires catching a Pokémon with a custom aspect, add the aspect to the `form` field:

```yml
{
    id: "1234567890ABCDEF"
    tasks: [{
        action: "catch"
        amount: 1L
        form: "custom_aspect"
        pokemon: "pikachu"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Multiple Aspects

Separate multiple aspects with commas:

```yml
form: "custom_aspect, custom_aspect2"
```

## Custom Regions, Pokémon, and Dimensions

If you have mods that add custom regions, biomes, or dimensions, you can reference them in quest files.

### Format

Use the namespace format: `<namespace>:<name>`

### Example

```yml
{
    id: "1234567890ABCDEF"
    tasks: [{
        action: "catch"
        amount: 1L
        biome: "custom_mod:biome"
        dimension: "minecraft:the_nether, custom_mod:dimension"
        region: "my_custom_region"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

**Common Vanilla Dimensions:**
- `minecraft:overworld`
- `minecraft:the_nether`
- `minecraft:the_end`

## Custom Pokémon Icons

By default, the quest icon shows the first Pokémon in the `pokemon` list or a Poké Ball if empty.

### Setting a Custom Icon

Use the `/give` command to create a specific Pokémon model:

```bash
/give @s cobblemon:pokemon_model[cobblemon:pokemon_item={species:"<namespace>:<pokemon_name>",aspects:[]}]
```

### Examples

**Galarian Zigzagoon:**
```bash
/give @s cobblemon:pokemon_model[cobblemon:pokemon_item={species:"cobblemon:zigzagoon",aspects:[galarian]}]
```

**Shiny Charizard:**
```bash
/give @s cobblemon:pokemon_model[cobblemon:pokemon_item={species:"cobblemon:charizard",aspects:[shiny]}]
```

**Alolan Raichu:**
```bash
/give @s cobblemon:pokemon_model[cobblemon:pokemon_item={species:"cobblemon:raichu",aspects:[alolan]}]
```

## Troubleshooting

### Quest Not Incrementing

**Common causes:**
1. **Player not in a team:**
   - FTB Quests requires players to be in a team
   - Run `/ftbteams create <team_name>` or join an existing team

2. **Quest not unlocked:**
   - Check quest dependencies and unlock requirements

3. **Conditions not met:**
   - Verify Pokémon matches all conditions (species, level, shiny, etc.)

4. **Blacklisted Pokémon:**
   - Check if the Pokémon is blacklisted

### Action Not Triggering

1. **Check action is registered:**
   - Look in the Action Picker for available actions

2. **Verify event subscription:**
   - Check logs for event registration messages

3. **Required mods:**
   - Some actions require Mega Showdown (dynamax, gigantamax, ultra_burst)

### Performance Issues

1. **Too many active quests:**
   - Consider breaking complex quest lines into smaller chapters

2. **Complex conditions:**
   - Simplify condition filters if possible

## Next Steps

- Learn about all available [Actions](Actions)
- Explore [Conditions](Conditions) for filtering quests
- Check out [Examples](Examples) for inspiration
- Try the new [Action Picker](Action-Picker) UI (v1.2.0+)

## Support

Need help?
- **GitHub Issues:** [Report bugs or request features](https://github.com/DeadshotOMEGA/cobblemon-quests-extended/issues)
- **Discord:** Join the Cobblemon Discord
- **Documentation:** [Full documentation on GitHub](https://github.com/DeadshotOMEGA/cobblemon-quests-extended)
