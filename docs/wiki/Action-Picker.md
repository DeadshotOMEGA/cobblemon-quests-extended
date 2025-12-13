# Action Picker (v1.2.0+)

The Action Picker is a new UI introduced in **v1.2.0** that makes creating Cobblemon quests easier and more intuitive.

## Overview

When creating or editing a Cobblemon Task in the FTB Quests editor, clicking on the **Actions** field opens the Action Picker screen, which provides:

- **Categorized Actions** - Actions organized into logical categories with color coding
- **Rich Tooltips** - Detailed information about each action on hover
- **Collapsible Categories** - Expand/collapse categories to focus on what you need
- **Visual Organization** - Color-coded categories make finding actions faster

## Opening the Action Picker

1. **Enter Edit Mode:**
   - In FTB Quests, click the edit button or run `/ftbquests edit_mode`

2. **Create or Edit a Quest:**
   - Right-click to create a new quest or click an existing quest

3. **Add/Edit Cobblemon Task:**
   - Add a new task and select `cobblemon_tasks:cobblemon_task`
   - OR click an existing Cobblemon task to edit it

4. **Click the Actions Field:**
   - In the task configuration screen, click the **Actions** input field
   - The Action Picker screen will open

## Action Categories

Actions are organized into color-coded categories:

### Obtaining Pokémon (Green)
Acquiring Pokémon through various means:
- `catch` - Catch a Pokémon
- `obtain` - Any of catch/trade_for/revive_fossil
- `select_starter` - Select a starter Pokémon
- `revive_fossil` - Revive a fossil
- `reel` - Reel in a Pokémon with a Poké Rod
- `trade_for` - Receive a Pokémon in a trade

### Battle (Red)
Combat-related actions:
- `defeat` - Defeat a Pokémon in battle
- `defeat_player` - Defeat a player in PvP
- `defeat_npc` - Defeat an NPC trainer
- `kill` - Kill a Pokémon
- `faint_pokemon` - Cause an opponent to faint
- `send_out` - Send a Pokémon into battle

### Evolution (Purple)
Evolution and leveling:
- `evolve` - Evolve FROM this species
- `evolve_into` - Evolve INTO this species
- `level_up` - Level up (delta)
- `level_up_to` - Reach a specific level
- `change_form` - Change form in battle

### Trading (Orange)
Trading Pokémon:
- `trade_away` - Give away a Pokémon
- `trade_for` - Receive a Pokémon

### Pokédex (Blue)
Pokédex registration:
- `scan` - Scan with a Pokédex
- `register` - Register a Pokémon
- `have_registered` - Already registered

### Battle Gimmicks (Pink)
Mega Evolution, Terastallization, and more:
- `mega_evolve` - Mega Evolve in battle
- `terastallize` - Terastallize in battle
- `use_z_move` - Use a Z-Move
- `dynamax` - Dynamax (requires Mega Showdown)
- `gigantamax` - Gigantamax (requires Mega Showdown)
- `ultra_burst` - Ultra Burst (requires Mega Showdown)

### Other (Gray)
Miscellaneous actions:
- `release` - Release a Pokémon
- `throw_ball` - Throw a Poké Ball
- `give_held_item` - Give a held item
- `heal` - Heal a Pokémon
- `hatch_egg` - Hatch an egg

## Using the Action Picker

### Expanding/Collapsing Categories

1. **Click the category header** to expand or collapse that category
2. **Collapsed categories** show only the header with the category name and icon
3. **Expanded categories** show all actions within that category

**Tip:** Collapse categories you're not using to focus on relevant actions.

### Viewing Action Information

**Hover over any action** to see a detailed tooltip containing:

- **Action Name** - The display name of the action
- **Category** - Which category it belongs to
- **Description** - What triggers this action in-game
- **Usage Example** - A code snippet showing how to use it
- **Requires Pokémon** - Whether the action needs a Pokémon parameter
- **Action ID** - The internal identifier used in quest files

**Example Tooltip:**
```
Mega Evolve
Category: Battle Gimmicks

Triggers when a Pokémon Mega Evolves in battle.

Example:
{
    action: "mega_evolve"
    amount: 1L
    pokemon: "charizard"
    mega_form: "mega-x"
    type: "cobblemon_tasks:cobblemon_task"
}

Requires Pokémon: Yes
ID: mega_evolve
```

### Selecting an Action

1. **Click an action button** to select it
2. **Multiple actions** can be selected by clicking multiple buttons
3. **Selected actions** appear highlighted
4. **Click again** to deselect an action

**Result:** When you close the Action Picker, the selected actions are added to the task's `action` field (comma-separated).

### Searching for Actions

While the Action Picker doesn't have a built-in search, you can:
- **Scan category headers** to find the type of action you need
- **Use tooltips** to verify the action does what you expect
- **Refer to the [Actions](Actions) wiki page** for a complete searchable list

## Quick Start Examples

### Example 1: Creating a Catch Quest

1. Open Action Picker
2. Expand "Obtaining Pokémon" (green category)
3. Click the `catch` action
4. Close the picker
5. Set `amount: 10L`
6. Leave `pokemon` empty to allow any Pokémon

### Example 2: Creating a Mega Evolution Quest

1. Open Action Picker
2. Expand "Battle Gimmicks" (pink category)
3. Hover over `mega_evolve` to see the tooltip
4. Click `mega_evolve`
5. Close the picker
6. Set:
   - `amount: 1L`
   - `pokemon: "charizard"`
   - `mega_form: "mega-x"`

### Example 3: Creating a Multi-Action Quest

1. Open Action Picker
2. Expand "Obtaining Pokémon"
3. Click `catch`
4. Expand "Trading"
5. Click `trade_for`
6. Expand "Obtaining Pokémon" again
7. Click `revive_fossil`
8. Close the picker

**Result:** The quest accepts ANY of these three actions (catch, trade_for, or revive_fossil).

## Visual Guide

### Category Colors

| Category | Color | Use Case |
|----------|-------|----------|
| **Obtaining Pokémon** | Green | Acquiring new Pokémon |
| **Battle** | Red | Combat and defeating opponents |
| **Evolution** | Purple | Evolution and leveling |
| **Trading** | Orange | Trading with NPCs/players |
| **Pokédex** | Blue | Pokédex completion |
| **Battle Gimmicks** | Pink | Mega Evolution, Terastallization, etc. |
| **Other** | Gray | Miscellaneous actions |

### Action Button States

- **Normal** - Action is available but not selected
- **Highlighted** - Action is selected for the quest
- **Disabled** - Action is not available (e.g., requires optional mod)

## Advanced Usage

### Combining Actions with Conditions

After selecting actions in the Action Picker, enhance your quest with conditions:

1. **Select actions** using the Action Picker
2. **Close the picker**
3. **Set conditions** in the task config:
   - `pokemon` - Specific species
   - `shiny` - Must be shiny
   - `min_level` / `max_level` - Level requirements
   - `biome` - Specific biome
   - And more...

**Example:** Create a quest to catch OR defeat a shiny Charizard:
1. Select `catch` and `defeat` in Action Picker
2. Set `pokemon: "charizard"`
3. Set `shiny: true`

### Actions Requiring Specific Conditions

Some actions work best with specific conditions:

| Action | Recommended Condition | Example |
|--------|---------------------|---------|
| `mega_evolve` | `mega_form` | `mega_form: "mega-x"` |
| `terastallize` | `tera_type` | `tera_type: "fire"` |
| `use_z_move` | `z_crystal` | `z_crystal: "firium-z"` |
| `dynamax` / `gigantamax` | `dynamax_type` | `dynamax_type: "gigantamax"` |
| `register` / `have_registered` | `dex_progress` | `dex_progress: "caught"` |

### Actions NOT in the Picker

If you need an action that doesn't appear in the Action Picker:
1. It may require a specific mod (e.g., Mega Showdown)
2. It may be deprecated or removed
3. You may need to update your mod version

**Check the [Actions](Actions) wiki page** for the complete list of available actions and their requirements.

## Troubleshooting

### Action Picker Won't Open
- **Verify** you're in edit mode (`/ftbquests edit_mode`)
- **Check** you're clicking on a Cobblemon Task (not a different task type)
- **Ensure** you have the correct version (v1.2.0+)

### Actions Are Grayed Out
- Some actions require **optional mods** (e.g., Mega Showdown for Dynamax)
- Install the required mod or choose a different action

### Selected Actions Not Saving
- **Close the Action Picker** by clicking outside or pressing a close button
- **Save the quest** after configuring all fields
- **Verify** the actions appear in the task's `action` field

### Quest Not Triggering
- **Check conditions** - Ensure the Pokémon matches all conditions
- **Verify action is correct** - Re-read the tooltip to confirm behavior
- **Test in-game** - Trigger the action and check quest progress
- See **[Getting Started - Troubleshooting](Getting-Started#troubleshooting)** for more help

## Benefits Over Manual Entry

### Before Action Picker (Manual Entry)
```yml
# Had to know exact action IDs
action: "mega_evolve"  # Did I spell this right?

# No tooltips or descriptions
# Risk of typos
# No organization
```

### With Action Picker (v1.2.0+)
- **Browse** actions by category
- **Hover** to see descriptions and examples
- **Click** to select (no typing)
- **Color-coded** for faster navigation
- **Tooltips** provide instant documentation

## See Also

- **[Actions](Actions)** - Complete list of all actions
- **[Conditions](Conditions)** - Filtering options for quests
- **[Examples](Examples)** - Quest examples using the Action Picker
- **[Getting Started](Getting-Started)** - Installation and basic usage

## Feedback

Found a bug or have suggestions for the Action Picker?
- **GitHub Issues:** [Report issues](https://github.com/DeadshotOMEGA/cobblemon-quests-extended/issues)
- **Suggestions:** Request new features or UI improvements
