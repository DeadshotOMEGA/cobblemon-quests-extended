# Cobblemon Quests Extended

**Supercharge your FTB Quests integration with Cobblemon 1.7.0+** — featuring Mega Evolution, Terastallization, Z-Moves, egg hatching, and more!

Based on [Cobblemon Quests](https://modrinth.com/mod/cobblemon-quests) by WinterWolfSV, this extended fork adds battle gimmick tracking and an extensible ActionRegistry for add-on mods.

---

## ✨ Features

### Battle Gimmicks
- **Mega Evolution** — Track Mega Evolutions including Mega X/Y and Primal forms
- **Terastallization** — Monitor Tera type changes with all 19 Tera types
- **Z-Moves** — Require specific Z-Crystals (type-based or Pokémon-specific)
- **Form Changes** — Detect mid-battle form transformations

### Battle Actions
- **Faint Pokémon** — Track when your Pokémon causes opponents to faint
- **Send Out** — Monitor when Pokémon are sent into battle
- **Give Held Item** — Track when held items are given
- **Heal** — Detect when Pokémon are healed

### Breeding & Care
- **Hatch Egg** — Create quests for hatching eggs with full condition support (including shiny!)

### Core Features
- Catch, trade, evolve, and level up tracking
- Pokédex integration and completion tracking
- Advanced filters: species, type, gender, shiny, form, biome, dimension, time
- Poké Ball requirements and custom aspects

### 🔌 Mega Showdown Integration
When [Cobblemon: Mega Showdown](https://modrinth.com/mod/cobblemon-mega-showdown) is installed:
- **Dynamax** & **Gigantamax** tracking
- **Ultra Burst** for Necrozma

---

## 📋 Requirements

Available for both **Fabric** and **NeoForge**!

### Common Requirements

| Dependency | Version |
|------------|---------|
| Minecraft | 1.21.1 |
| Cobblemon | 1.7.0+ |
| FTB Quests | Latest for 1.21.1 |

### Fabric Version

| Dependency | Version |
|------------|---------|
| Fabric Loader | 0.18.1+ |
| Fabric API | 0.116.7+ |

### NeoForge Version

| Dependency | Version |
|------------|---------|
| NeoForge | 21.1.x |
| Kotlin for Forge | 5.10.0+ |

**Installation:** Required on both **client and server**.

---

## 🛠️ For Quest Creators

Full documentation available on [GitHub](https://github.com/DeadshotOMEGA/cobblemon_quests_extended).

**Quick Example — Mega Evolution Quest:**
```snbt
{
    action: "mega_evolve"
    amount: 1L
    pokemon: "charizard"
    mega_form: "mega-x"
    type: "cobblemon_tasks:cobblemon_task"
}
```

---

## ⚠️ Important Notes

- This mod **does not add quests** — it provides task types for quest creators
- **Do not install alongside the original Cobblemon Quests** — this is a replacement
- All core features work without Mega Showdown

---

## 📜 Credits

- **Original Author:** [WinterWolfSV](https://github.com/WinterWolfSV)
- **Extended by:** DeadshotOMEGA

Licensed under CC-BY-NC-4.0
