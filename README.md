# Cobblemon Quests Extended

Extended FTB Quests integration for Cobblemon with mega evolution, terastallization, Z-moves, and more.

**Based on [Cobblemon Quests](https://github.com/WinterWolfSV/Cobblemon_Quests) by [WinterWolfSV](https://github.com/WinterWolfSV).**

This fork extends the original mod with new features for Cobblemon 1.7.0+ including battle gimmicks (Mega Evolution, Terastallization, Z-Moves), form changes, egg hatching, and an extensible ActionRegistry system for add-on mods.

## Features

### Core Features (Original)

- **Catch & Obtain**: Create quests for catching, trading, selecting starters, and reviving fossils
- **Battle Tasks**: Defeat wild Pokemon, trainers, and NPCs
- **Evolution**: Track Pokemon evolution and evolution targets
- **Level Tracking**: Monitor Pokemon leveling up or reaching specific levels
- **Pokedex Integration**: Register Pokemon and track Pokedex completion
- **Advanced Conditions**: Filter by Pokemon species, type, gender, shiny status, form, biome, dimension, time, and more
- **Ball Tracking**: Specify which Poke Balls must be used
- **Custom Aspects**: Support for modded Pokemon aspects and forms

### Extended Features (New in 1.7.0+)

#### Battle Gimmicks
- **Mega Evolution**: Track when Pokemon Mega Evolve (including Mega X/Y and Primal forms)
- **Terastallization**: Monitor Tera type changes with all 19 Tera types
- **Z-Moves**: Require specific Z-Crystals (type-based or Pokemon-specific)
- **Form Changes**: Detect mid-battle form transformations

#### Battle Actions
- **Faint Pokemon**: Track when your Pokemon causes opponents to faint
- **Send Out**: Monitor when Pokemon are sent into battle
- **Give Held Item**: Track when held items are given to Pokemon
- **Heal**: Detect when Pokemon are healed

#### Breeding & Care
- **Hatch Egg**: Create quests for hatching eggs (with full condition support including shiny)

#### Mega Showdown Integration (Optional)
When [Cobblemon: Mega Showdown](https://modrinth.com/mod/mega-showdown) is installed:
- **Dynamax**: Track Dynamax transformations
- **Gigantamax**: Require specific Gigantamax forms
- **Ultra Burst**: Monitor Necrozma's Ultra Burst

### Developer Features

#### ActionRegistry System
An extensible registry system allowing add-on mods to register custom quest actions:

```java
ActionRegistry.register("my_custom_action", ActionDefinition.of(
    "my_custom_action",
    true,  // requires Pokemon
    ActionCategory.OTHER
));
```

Categories: `CATCH`, `BATTLE`, `EVOLUTION`, `TRADE`, `POKEDEX`, `GIMMICK`, `OTHER`

## Requirements

### Required Dependencies
- **Minecraft**: 1.21.1
- **Cobblemon**: 1.7.0 or later
- **FTB Quests**: Latest version for MC 1.21.1
- **Fabric Loader**: 0.18.1+
- **Fabric API**: 0.116.7+

### Optional Dependencies
- **Cobblemon: Mega Showdown**: Enables Dynamax, Gigantamax, and Ultra Burst actions

### Installation Type
This mod must be installed on **both client and server**.

## Installation

1. Download the latest release from the [Releases](https://github.com/yourusername/cobblemon_quests_extended/releases) page
2. Place the mod JAR file in your `mods` folder
3. Ensure Cobblemon 1.7.0+ and FTB Quests are also installed
4. (Optional) Install Cobblemon: Mega Showdown for additional features
5. Launch the game

## Usage

### For Players
Simply play the game and check your quest book. Quest tasks will automatically trigger as you catch, battle, and interact with Pokemon. The quest descriptions should be self-explanatory.

### For Quest Creators
Comprehensive documentation for creating quests is available in [quest-creation.md](/home/sauk/cobble-quests/cobblemon-quests-extended/quest-creation.md).

**Quick Examples:**

**Mega Evolution Quest:**
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

**Terastallization Quest:**
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

**Shiny Egg Hatching Quest:**
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

See the [quest creation documentation](/home/sauk/cobble-quests/cobblemon-quests-extended/quest-creation.md) for all available actions, conditions, and detailed examples.

## Documentation

- **[Quest Creation Guide](/home/sauk/cobble-quests/cobblemon-quests-extended/quest-creation.md)**: Complete documentation for quest creators
- **[Original Cobblemon Quests](https://github.com/WinterWolfSV/Cobblemon_Quests)**: Upstream project by WinterWolfSV

## FAQ

**Q: Do I need to install this on both server and client?**
A: Yes, this mod is required on both sides.

**Q: No quests are showing up in the quest book. What do I do?**
A: This mod does not add any quests by itself. It allows quest creators to create Cobblemon-related quests. You need to create quests using FTB Quests or install a modpack that includes Cobblemon quests.

**Q: Can I use this with the original Cobblemon Quests?**
A: No, this is a fork/replacement. Only install one version.

**Q: Do all features work without Mega Showdown?**
A: Yes! All core features work independently. Mega Showdown is only required for Dynamax, Gigantamax, and Ultra Burst features.

**Q: Can I add my own custom actions?**
A: Yes! Use the ActionRegistry system to register custom actions from your add-on mod. See the developer documentation for details.

## Commands

### Blacklist Pokemon
```bash
/cobblemonquests blacklisted_pokemon [add/remove] <pokemon>
```

### Suppress Console Warnings
```bash
/cobblemonquests suppress_warnings [true/false]
```

### Give Pokemon (Triggers Quest Events)
```bash
/cobblemonquests givepokemon <player> <should_give> <amount> {<actions>} <pokemon>
```

See [quest-creation.md](/home/sauk/cobble-quests/cobblemon-quests-extended/quest-creation.md) for command details.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## License

This mod is released under the **Creative Commons Attribution-NonCommercial 4.0 International (CC-BY-NC-4.0)** License.

This license allows you to:
- Use, modify, and distribute the mod for **non-commercial purposes**
- Create derivative works based on this mod

You must:
- Credit the original author (WinterWolfSV) and this fork
- Include a copy of the license in redistributed versions
- Indicate if modifications were made

**Original Work**: [Cobblemon Quests](https://github.com/WinterWolfSV/Cobblemon_Quests) by WinterWolfSV
**Extended By**: This fork

See the [LICENSE](/home/sauk/cobble-quests/cobblemon-quests-extended/LICENSE) file for full license terms.

## Credits

- **Original Author**: [WinterWolfSV](https://github.com/WinterWolfSV) - Creator of Cobblemon Quests
- **Extended Version**: Contributors to this fork
- **Cobblemon Team**: For the amazing Cobblemon mod
- **FTB Team**: For FTB Quests

## Support & Feedback

### For This Fork
- **Issues**: Submit bug reports and feature requests on the [GitHub Issues](https://github.com/yourusername/cobblemon_quests_extended/issues) page

### For Original Features
- **Discord**: Contact WinterWolfSV directly (winterwolfsv)
- **GitHub**: [Original Cobblemon Quests Issues](https://github.com/WinterWolfSV/Cobblemon_Quests/issues)

When reporting bugs, please provide:
- Minecraft version
- Cobblemon version
- Mod version
- Steps to reproduce
- Error messages and log files
- Screenshots if applicable
