# Actions

Actions define what the player must do to complete a quest. Each action is triggered by specific in-game events.

## Action Categories

Actions are organized into categories for easier navigation:

| Category | Description | Color |
|----------|-------------|-------|
| **Obtaining Pokémon** | Acquiring Pokémon through various means | Green |
| **Battle** | Combat-related actions | Red |
| **Evolution** | Evolution and leveling actions | Purple |
| **Trading** | Trading Pokémon with NPCs or players | Orange |
| **Pokédex** | Pokédex registration and scanning | Blue |
| **Battle Gimmicks** | Mega Evolution, Terastallization, Z-Moves, etc. | Pink |
| **Other** | Miscellaneous actions | Gray |

## Core Actions

These actions work with all versions of Cobblemon and do not require additional mods.

### Obtaining Pokémon

| Action | ID | Description |
|--------|----|----|
| **Catch** | `catch` | Catch a Pokémon with a Poké Ball |
| **Obtain** | `obtain` | Any of `catch`, `trade_for`, or `revive_fossil` |
| **Select Starter** | `select_starter` | Select a starter Pokémon (also triggers `catch`) |
| **Revive Fossil** | `revive_fossil` | Revive a fossil in the resurrection machine |
| **Reel** | `reel` | Use a Poké Rod to reel in a Pokémon |

### Battle Actions

| Action | ID | Description |
|--------|----|----|
| **Defeat** | `defeat` | Defeat a Pokémon in battle |
| **Defeat Player** | `defeat_player` | Defeat a player in battle (see [special notes](#defeat-player-and-npc)) |
| **Defeat NPC** | `defeat_npc` | Defeat an NPC trainer in battle (see [special notes](#defeat-player-and-npc)) |
| **Kill** | `kill` | Kill a Pokémon (causes it to faint permanently) |

### Evolution Actions

| Action | ID | Description |
|--------|----|----|
| **Evolve** | `evolve` | Triggers when a Pokémon evolves FROM this species (e.g., Bulbasaur → Ivysaur counts as Bulbasaur) |
| **Evolve Into** | `evolve_into` | Triggers when a Pokémon evolves INTO this species (e.g., Bulbasaur → Ivysaur counts as Ivysaur; also triggers `catch`) |
| **Level Up** | `level_up` | Triggers on the delta level (e.g., level 10 → 13 increases by 3) |
| **Level Up To** | `level_up_to` | Triggers on the resulting level (e.g., level 10 → 13 sets progress to 13 out of `amount`) |

### Trading Actions

| Action | ID | Description |
|--------|----|----|
| **Trade Away** | `trade_away` | Triggers when a Pokémon leaves the player's possession |
| **Trade For** | `trade_for` | Triggers when a Pokémon enters the player's possession |

### Pokédex Actions

| Action | ID | Description |
|--------|----|----|
| **Scan** | `scan` | Use a Pokédex to scan a Pokémon |
| **Register** | `register` | Register a Pokémon in the Pokédex (use `dex_progress` condition for seen/caught) |
| **Have Registered** | `have_registered` | Pokémon already registered in the Pokédex (checked on player login and dex update; use `dex_progress` condition) |

### Other Actions

| Action | ID | Description |
|--------|----|----|
| **Release** | `release` | Release a Pokémon into the wild from the PC |
| **Throw Poké Ball** | `throw_ball` | Throw the selected Poké Ball at a Pokémon (triggers regardless of capture success) |

## Extended Actions (Cobblemon 1.7.0+)

These actions require **Cobblemon 1.7.0 or later**.

### Battle Gimmicks

| Action | ID | Description | Required Condition |
|--------|----|----|-------------------|
| **Mega Evolve** | `mega_evolve` | Triggers when a Pokémon Mega Evolves in battle | Use with `mega_form` condition |
| **Terastallize** | `terastallize` | Triggers when a Pokémon Terastallizes in battle | Use with `tera_type` condition |
| **Use Z-Move** | `use_z_move` | Triggers when a Pokémon uses a Z-Move in battle | Use with `z_crystal` condition |
| **Change Form** | `change_form` | Triggers when a Pokémon changes form during battle | - |

### Battle Actions (Extended)

| Action | ID | Description |
|--------|----|----|
| **Faint Pokémon** | `faint_pokemon` | Triggers when the player's Pokémon causes an opponent to faint |
| **Send Out** | `send_out` | Triggers when a Pokémon is sent into battle |

### Care & Management

| Action | ID | Description |
|--------|----|----|
| **Give Held Item** | `give_held_item` | Triggers when a held item is given to a Pokémon |
| **Heal** | `heal` | Triggers when a Pokémon is healed (Pokémon Center, items, etc.) |
| **Hatch Egg** | `hatch_egg` | Triggers when an egg hatches |

## Mega Showdown Integration Actions

These actions require the **[Cobblemon: Mega Showdown](https://modrinth.com/mod/cobblemon-mega-showdown)** mod to be installed.

| Action | ID | Description | Required Condition |
|--------|----|----|-------------------|
| **Dynamax** | `dynamax` | Triggers when a Pokémon Dynamaxes in battle | Use with `dynamax_type` condition |
| **Gigantamax** | `gigantamax` | Triggers when a Pokémon Gigantamaxes in battle | Use with `dynamax_type` condition |
| **Ultra Burst** | `ultra_burst` | Triggers when Necrozma uses Ultra Burst | - |

## Special Action Notes

### Defeat Player and NPC

The `defeat_player` and `defeat_npc` actions work differently from other actions:

**Unique Behavior:**
- Only the `form` condition and `amount` parameter matter
- All other conditions (pokemon, type, shiny, etc.) are **ignored**
- The `form` field contains **player/NPC names** (comma-separated)
- **Case-sensitive:** "Steve" ≠ "steve"

**Example:**

```yml
{
    id: "1234567890ABCDEF"
    tasks: [{
        action: "defeat_npc, defeat_player"
        amount: 1L
        form: "Steve, Ash"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

This quest completes when the player defeats a player named "Steve" or "Ash", OR an NPC named "Steve" or "Ash".

### Actions That Trigger Multiple Events

Some actions automatically trigger other actions:

| Primary Action | Also Triggers |
|----------------|---------------|
| `select_starter` | `catch` |
| `evolve_into` | `catch` (for the new evolved form) |
| `obtain` | One of: `catch`, `trade_for`, or `revive_fossil` |

**Example:** A quest with `action: "catch"` will complete when:
- The player catches a Pokémon normally
- The player selects a starter
- The player evolves a Pokémon (via `evolve_into`)

### Level Up Actions

There are two level-up actions with different behaviors:

**Level Up (Delta):**
```yml
action: "level_up"
amount: 10L
```
Tracks the **total levels gained**. If a Pokémon goes from level 10 → 13, this adds **3** to the quest progress.

**Level Up To (Target):**
```yml
action: "level_up_to"
amount: 50L
```
Tracks the **resulting level**. If a Pokémon reaches level 50, the quest sets progress to **50 out of 50**.

## Using Multiple Actions

You can require **multiple different actions** for a single task by listing them comma-separated:

```yml
action: "catch, defeat, evolve"
```

This quest increments when the player:
- Catches a Pokémon, OR
- Defeats a Pokémon, OR
- Evolves a Pokémon

## Action Examples

### Simple Catch Quest
```yml
{
    tasks: [{
        action: "catch"
        amount: 10L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Defeat Specific Pokémon
```yml
{
    tasks: [{
        action: "defeat"
        amount: 5L
        pokemon: "gyarados"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Level Up to 50
```yml
{
    tasks: [{
        action: "level_up_to"
        amount: 50L
        pokemon: "pikachu"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Mega Evolution
```yml
{
    tasks: [{
        action: "mega_evolve"
        amount: 1L
        pokemon: "charizard"
        mega_form: "mega-x"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Hatch Shiny Egg
```yml
{
    tasks: [{
        action: "hatch_egg"
        amount: 1L
        shiny: true
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

## See Also

- **[Conditions](Conditions)** - Filter which Pokémon count for actions
- **[Examples](Examples)** - More complex quest examples
- **[Action Picker](Action-Picker)** - UI for selecting actions in v1.2.0+
- **[Getting Started](Getting-Started)** - Installation and setup
