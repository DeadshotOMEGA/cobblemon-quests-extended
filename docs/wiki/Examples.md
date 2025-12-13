# Quest Examples

This page provides complete quest examples showcasing different features and combinations of actions and conditions.

## Table of Contents

- [Basic Examples](#basic-examples)
- [Battle Gimmick Examples](#battle-gimmick-examples)
- [Advanced Filtering Examples](#advanced-filtering-examples)
- [Pokédex Examples](#pokédex-examples)
- [Trading & Breeding Examples](#trading--breeding-examples)
- [Multi-Condition Examples](#multi-condition-examples)
- [Mega Showdown Examples](#mega-showdown-examples)

## Basic Examples

### Catch 10 Pokémon

The simplest quest—catch any 10 Pokémon.

```yml
{
    id: "catch_10_pokemon"
    tasks: [{
        action: "catch"
        amount: 10L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Defeat 5 Wild Pokémon

Defeat any 5 wild Pokémon in battle.

```yml
{
    id: "defeat_5_wild"
    tasks: [{
        action: "defeat"
        amount: 5L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Evolve Any Pokémon

Evolve a single Pokémon.

```yml
{
    id: "evolve_pokemon"
    tasks: [{
        action: "evolve"
        amount: 1L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Catch a Starter

Catch any of the three Kanto starters.

```yml
{
    id: "catch_starter"
    tasks: [{
        action: "catch"
        amount: 1L
        pokemon: "bulbasaur, charmander, squirtle"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

## Battle Gimmick Examples

### Mega Evolution - Charizard X

Mega Evolve Charizard into Mega Charizard X.

```yml
{
    id: "mega_charizard_x_quest"
    tasks: [{
        action: "mega_evolve"
        amount: 1L
        pokemon: "charizard"
        mega_form: "mega-x"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Mega Evolution - Any Mega

Mega Evolve any Pokémon 5 times.

```yml
{
    id: "mega_evolve_5_times"
    tasks: [{
        action: "mega_evolve"
        amount: 5L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Terastallization - Fire Type

Terastallize any Pokémon with Fire Tera type 5 times.

```yml
{
    id: "fire_tera_quest"
    tasks: [{
        action: "terastallize"
        amount: 5L
        tera_type: "fire"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Terastallization - Specific Pokémon

Terastallize a Pikachu with Electric Tera type.

```yml
{
    id: "pikachu_tera"
    tasks: [{
        action: "terastallize"
        amount: 1L
        pokemon: "pikachu"
        tera_type: "electric"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Z-Move - Generic Type

Use any Fire-type Z-Move.

```yml
{
    id: "fire_z_move"
    tasks: [{
        action: "use_z_move"
        amount: 1L
        z_crystal: "firium-z"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Z-Move - Pokémon-Specific

Use Pikachu's exclusive Z-Move (10,000,000 Volt Thunderbolt) with Partner Cap Pikachu.

```yml
{
    id: "pikashunium_z_quest"
    tasks: [{
        action: "use_z_move"
        amount: 1L
        z_crystal: "pikashunium-z"
        pokemon: "pikachu"
        form: "partner-cap"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Form Change Quest

Trigger form changes in battle 3 times.

```yml
{
    id: "form_change_quest"
    tasks: [{
        action: "change_form"
        amount: 3L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

## Advanced Filtering Examples

### Catch Shiny Pokémon

Catch any shiny Pokémon.

```yml
{
    id: "catch_shiny"
    tasks: [{
        action: "catch"
        amount: 1L
        shiny: true
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Catch High-Level Pokémon

Catch a Pokémon that is level 50 or higher.

```yml
{
    id: "catch_high_level"
    tasks: [{
        action: "catch"
        amount: 1L
        min_level: 50
        max_level: 0
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Catch Water Types with Specific Ball

Catch 10 Water-type Pokémon using a Net Ball.

```yml
{
    id: "water_net_ball"
    tasks: [{
        action: "catch"
        amount: 10L
        pokemon_type: "water"
        poke_ball_used: "net_ball"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Catch Pokémon at Night

Catch 5 Pokémon during nighttime (6 PM - 6 AM).

```yml
{
    id: "catch_at_night"
    tasks: [{
        action: "catch"
        amount: 5L
        time_min: 12000L
        time_max: 24000L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Catch Pokémon in Specific Biome

Catch a Fire-type Pokémon in a desert biome.

```yml
{
    id: "fire_in_desert"
    tasks: [{
        action: "catch"
        amount: 1L
        pokemon_type: "fire"
        biome: "minecraft:desert"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Catch Pokémon in the Nether

Catch any Pokémon in the Nether dimension.

```yml
{
    id: "catch_in_nether"
    tasks: [{
        action: "catch"
        amount: 1L
        dimension: "minecraft:the_nether"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Catch Regional Forms

Catch 3 Alolan form Pokémon.

```yml
{
    id: "catch_alolan_forms"
    tasks: [{
        action: "catch"
        amount: 3L
        form: "alolan"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Catch Specific Gender

Catch a female Pikachu.

```yml
{
    id: "female_pikachu"
    tasks: [{
        action: "catch"
        amount: 1L
        pokemon: "pikachu"
        gender: "female"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Catch Generation-Specific Pokémon

Catch 10 Kanto region Pokémon.

```yml
{
    id: "catch_kanto"
    tasks: [{
        action: "catch"
        amount: 10L
        region: "kanto"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

## Pokédex Examples

### Scan 20 Pokémon

Scan 20 different Pokémon with a Pokédex.

```yml
{
    id: "scan_20"
    tasks: [{
        action: "scan"
        amount: 20L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Register Pokémon as Caught

Register 50 Pokémon as caught in the Pokédex.

```yml
{
    id: "register_50_caught"
    tasks: [{
        action: "register"
        amount: 50L
        dex_progress: "caught"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Have Specific Pokémon Registered

Check if the player has all three Kanto starters registered as caught.

```yml
{
    id: "starters_registered"
    tasks: [{
        action: "have_registered"
        amount: 3L
        pokemon: "bulbasaur, charmander, squirtle"
        dex_progress: "caught"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

## Trading & Breeding Examples

### Trade Any Pokémon

Trade away any Pokémon.

```yml
{
    id: "trade_pokemon"
    tasks: [{
        action: "trade_away"
        amount: 1L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Receive Specific Pokémon in Trade

Receive a Machamp through trading.

```yml
{
    id: "trade_for_machamp"
    tasks: [{
        action: "trade_for"
        amount: 1L
        pokemon: "machamp"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Hatch Eggs

Hatch 5 eggs.

```yml
{
    id: "hatch_5_eggs"
    tasks: [{
        action: "hatch_egg"
        amount: 5L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Hatch Shiny Egg

Hatch a shiny Pokémon from an egg.

```yml
{
    id: "shiny_hatch_quest"
    tasks: [{
        action: "hatch_egg"
        amount: 1L
        shiny: true
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Hatch Specific Species

Hatch a Riolu from an egg.

```yml
{
    id: "hatch_riolu"
    tasks: [{
        action: "hatch_egg"
        amount: 1L
        pokemon: "riolu"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

## Multi-Condition Examples

### Catch Shiny Starter at Night in Forest

Catch a shiny Bulbasaur at night in a forest biome.

```yml
{
    id: "shiny_bulbasaur_night_forest"
    tasks: [{
        action: "catch"
        amount: 1L
        pokemon: "bulbasaur"
        shiny: true
        biome: "minecraft:forest"
        time_min: 12000L
        time_max: 24000L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Defeat High-Level Dragon Types

Defeat 10 Dragon-type Pokémon that are level 50 or higher.

```yml
{
    id: "defeat_high_dragons"
    tasks: [{
        action: "defeat"
        amount: 10L
        pokemon_type: "dragon"
        min_level: 50
        max_level: 0
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Catch Legendary with Master Ball

Catch any legendary Pokémon using a Master Ball.

```yml
{
    id: "catch_legendary_master"
    tasks: [{
        action: "catch"
        amount: 1L
        pokemon: "articuno, zapdos, moltres, mewtwo, mew"
        poke_ball_used: "master_ball"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Evolve Specific Pokémon to High Level

Evolve an Eevee and level it up to level 50.

```yml
{
    id: "evolve_eevee_to_50"
    tasks: [{
        action: "evolve"
        amount: 1L
        pokemon: "eevee"
        type: "cobblemon_tasks:cobblemon_task"
    },
    {
        action: "level_up_to"
        amount: 50L
        pokemon: "vaporeon, jolteon, flareon, espeon, umbreon, leafeon, glaceon, sylveon"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Catch Multiple Regional Variants

Catch all Alolan starter final evolutions.

```yml
{
    id: "alolan_starters"
    tasks: [{
        action: "catch"
        amount: 3L
        pokemon: "decidueye, incineroar, primarina"
        form: "alolan"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

## Mega Showdown Examples

These examples require the [Cobblemon: Mega Showdown](https://modrinth.com/mod/cobblemon-mega-showdown) mod.

### Dynamax Any Pokémon

Dynamax any Pokémon in battle.

```yml
{
    id: "dynamax_quest"
    tasks: [{
        action: "dynamax"
        amount: 1L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Gigantamax Specific Pokémon

Gigantamax a Charizard in battle.

```yml
{
    id: "gigantamax_charizard"
    tasks: [{
        action: "gigantamax"
        amount: 1L
        pokemon: "charizard"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Gigantamax vs. Dynamax

Gigantamax any Pokémon (not regular Dynamax).

```yml
{
    id: "gigantamax_only"
    tasks: [{
        action: "gigantamax"
        amount: 1L
        dynamax_type: "gigantamax"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Ultra Burst

Trigger Ultra Burst with Necrozma.

```yml
{
    id: "ultra_burst_quest"
    tasks: [{
        action: "ultra_burst"
        amount: 1L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

## Special Action Examples

### Defeat Specific NPC

Defeat an NPC trainer named "Ash" or "Gary".

```yml
{
    id: "defeat_ash_gary"
    tasks: [{
        action: "defeat_npc"
        amount: 1L
        form: "Ash, Gary"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

**Note:** Only `form` and `amount` matter for `defeat_npc` and `defeat_player` actions.

### Defeat Player

Defeat a player named "Steve" in battle.

```yml
{
    id: "defeat_steve"
    tasks: [{
        action: "defeat_player"
        amount: 1L
        form: "Steve"
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Release Pokémon

Release 5 Pokémon from the PC.

```yml
{
    id: "release_5"
    tasks: [{
        action: "release"
        amount: 5L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Throw Poké Balls

Throw 50 Poké Balls at Pokémon (regardless of capture success).

```yml
{
    id: "throw_50_balls"
    tasks: [{
        action: "throw_ball"
        amount: 50L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Reel in Pokémon with Fishing Rod

Reel in 10 Pokémon using a Poké Rod.

```yml
{
    id: "reel_10"
    tasks: [{
        action: "reel"
        amount: 10L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Revive Fossil

Revive 3 fossil Pokémon.

```yml
{
    id: "revive_3_fossils"
    tasks: [{
        action: "revive_fossil"
        amount: 3L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Heal Pokémon

Heal 20 Pokémon at a Pokémon Center or with items.

```yml
{
    id: "heal_20"
    tasks: [{
        action: "heal"
        amount: 20L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

### Give Held Items

Give held items to 5 Pokémon.

```yml
{
    id: "give_held_items"
    tasks: [{
        action: "give_held_item"
        amount: 5L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

## Quest Progression Examples

### Starter Quest Line

**Step 1: Choose Your Starter**
```yml
{
    id: "choose_starter"
    tasks: [{
        action: "select_starter"
        amount: 1L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

**Step 2: Level Up to 16**
```yml
{
    id: "level_starter_16"
    tasks: [{
        action: "level_up_to"
        amount: 16L
        pokemon: "bulbasaur, charmander, squirtle"
        type: "cobblemon_tasks:cobblemon_task"
    }]
    dependencies: ["choose_starter"]
}
```

**Step 3: Evolve Your Starter**
```yml
{
    id: "evolve_starter"
    tasks: [{
        action: "evolve"
        amount: 1L
        pokemon: "bulbasaur, charmander, squirtle"
        type: "cobblemon_tasks:cobblemon_task"
    }]
    dependencies: ["level_starter_16"]
}
```

### Pokédex Quest Line

**Step 1: Get a Pokédex**
```yml
{
    id: "get_pokedex"
    tasks: [{
        action: "scan"
        amount: 1L
        type: "cobblemon_tasks:cobblemon_task"
    }]
}
```

**Step 2: Scan 10 Pokémon**
```yml
{
    id: "scan_10"
    tasks: [{
        action: "scan"
        amount: 10L
        type: "cobblemon_tasks:cobblemon_task"
    }]
    dependencies: ["get_pokedex"]
}
```

**Step 3: Complete Kanto Dex**
```yml
{
    id: "complete_kanto_dex"
    tasks: [{
        action: "have_registered"
        amount: 151L
        region: "kanto"
        dex_progress: "caught"
        type: "cobblemon_tasks:cobblemon_task"
    }]
    dependencies: ["scan_10"]
}
```

## Tips for Quest Design

### Progressive Difficulty
Start with simple requirements and gradually add conditions:
1. Catch any Pokémon → Catch specific species → Catch with specific ball → Catch shiny with specific ball

### Multiple Tasks
Create quests with multiple tasks for complex objectives:
```yml
{
    id: "master_trainer"
    tasks: [
        {
            action: "catch"
            amount: 100L
            type: "cobblemon_tasks:cobblemon_task"
        },
        {
            action: "defeat"
            amount: 50L
            type: "cobblemon_tasks:cobblemon_task"
        },
        {
            action: "evolve"
            amount: 10L
            type: "cobblemon_tasks:cobblemon_task"
        }
    ]
}
```

### Testing Quests
Always test your quests in-game:
1. Create the quest in creative mode
2. Test each condition individually
3. Verify progress increments correctly
4. Check quest completion triggers rewards

## See Also

- **[Actions](Actions)** - Complete list of available actions
- **[Conditions](Conditions)** - Complete list of filtering conditions
- **[Getting Started](Getting-Started)** - Basic setup and installation
- **[Action Picker](Action-Picker)** - UI for creating quests in v1.2.0+
