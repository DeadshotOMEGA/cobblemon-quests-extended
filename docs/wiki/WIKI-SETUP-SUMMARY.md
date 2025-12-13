# Wiki Setup Summary

## Files Created

All wiki files have been created in `/home/sauk/cobble-quests/cobblemon-quests-extended/docs/wiki/`:

| File | Size | Description |
|------|------|-------------|
| **Home.md** | 3.3 KB | Wiki homepage with overview, features, and quick links |
| **Getting-Started.md** | 8.8 KB | Installation, configuration, commands, and troubleshooting |
| **Actions.md** | 8.1 KB | Complete reference for all quest actions (40+ actions) |
| **Conditions.md** | 12 KB | Complete reference for all filtering conditions |
| **Examples.md** | 16 KB | 30+ quest examples covering all features |
| **Action-Picker.md** | 9.4 KB | Documentation for the v1.2.0 action picker UI |
| **_Sidebar.md** | 1 KB | Navigation sidebar for the wiki |
| **README.md** | 3.8 KB | Instructions for publishing to GitHub Wiki |

**Total:** 8 files, ~62 KB of documentation

## Content Overview

### Home.md
- Project overview and tagline
- Features at a glance (battle gimmicks, breeding, core features)
- Requirements table
- Quick links to all wiki pages
- Credits and support information

### Getting-Started.md
- Installation instructions and requirements
- Basic quest creation tutorial
- Configuration options
- All admin commands with examples
- Manual chapter file editing guide
- Custom aspects and regions setup
- Custom Pokémon icons
- Troubleshooting section

### Actions.md
- 40+ actions organized by category
- Core actions (catch, defeat, evolve, etc.)
- Extended actions (Cobblemon 1.7.0+)
- Mega Showdown integration actions
- Special action notes (defeat_player/NPC)
- Action combinations and multi-trigger behavior
- Quick examples for each action type

### Conditions.md
- All filtering conditions with descriptions
- Core conditions (species, shiny, level, etc.)
- Extended conditions (tera type, mega form, etc.)
- Condition combination examples
- Tips for using conditions effectively
- List behavior and level range explanations

### Examples.md
- 30+ complete quest examples
- Basic examples (catch, defeat, evolve)
- Battle gimmick examples (Mega Evolution, Terastallization, Z-Moves)
- Advanced filtering examples (shiny, biome, time, level)
- Pokédex examples
- Trading & breeding examples
- Multi-condition examples
- Mega Showdown examples
- Quest progression chains

### Action-Picker.md
- Overview of the v1.2.0 Action Picker UI
- How to open and use the Action Picker
- Category organization and color coding
- Tooltip information guide
- Quick start examples
- Advanced usage tips
- Troubleshooting guide
- Benefits over manual entry

### _Sidebar.md
- Navigation menu for all wiki pages
- Links to external documentation
- Links to related mods
- Quick access to GitHub resources

## Publishing to GitHub Wiki

### Quick Method (Recommended for First Time)

1. **Visit the Wiki tab:** https://github.com/DeadshotOMEGA/cobblemon-quests-extended/wiki

2. **Create the first page** (if wiki is empty):
   - Click "Create the first page"
   - Title: `Home`
   - Copy content from `Home.md`
   - Save

3. **Add remaining pages:**
   - Click "New Page" for each file
   - Use the filename (without `.md`) as the page title:
     - `Getting-Started`
     - `Actions`
     - `Conditions`
     - `Examples`
     - `Action-Picker`
     - `_Sidebar`
   - Copy content from each file
   - Save each page

### Git Method (For Updates)

```bash
# Clone the wiki repository
git clone https://github.com/DeadshotOMEGA/cobblemon-quests-extended.wiki.git
cd cobblemon-quests-extended.wiki

# Copy all wiki files
cp /home/sauk/cobble-quests/cobblemon-quests-extended/docs/wiki/*.md .
rm README.md WIKI-SETUP-SUMMARY.md  # Don't publish these

# Commit and push
git add .
git commit -m "docs: add comprehensive wiki documentation

- Add Home page with project overview
- Add Getting Started guide
- Add Actions reference (40+ actions)
- Add Conditions reference
- Add Examples (30+ quest examples)
- Add Action Picker UI guide
- Add navigation sidebar"
git push origin master
```

## Verification Checklist

After publishing, verify:

- [ ] Home page displays correctly
- [ ] All internal links work (Getting-Started, Actions, etc.)
- [ ] Sidebar appears on all pages
- [ ] Code blocks render with syntax highlighting
- [ ] Tables display properly
- [ ] External links work (GitHub, Modrinth)
- [ ] Images display (if any were added later)

## Maintenance

### When to Update the Wiki

- **New action added** → Update Actions.md and Examples.md
- **New condition added** → Update Conditions.md and Examples.md
- **New feature** → Update Home.md and Getting-Started.md
- **Bug fix** → Update Troubleshooting in Getting-Started.md
- **Version bump** → Update requirements in Home.md and Getting-Started.md

### Update Workflow

1. Edit files in `/docs/wiki/`
2. Test locally by previewing Markdown
3. Commit to main repository
4. Sync to wiki using git clone/push method

## Statistics

- **Total Documentation:** ~62 KB
- **Total Pages:** 7 wiki pages + 1 sidebar
- **Total Examples:** 30+ quest examples
- **Total Actions Documented:** 40+ actions
- **Total Conditions Documented:** 15+ conditions
- **Categories:** 7 action categories
- **Cross-references:** 50+ internal links

## Next Steps

1. **Publish to GitHub Wiki** using one of the methods above
2. **Update README.md** in main repo to link to wiki
3. **Update Modrinth description** to link to wiki
4. **Announce wiki** in Discord/community channels
5. **Monitor feedback** and update as needed

## Support

If you need help publishing the wiki:
- Check the [README.md](README.md) in this directory
- Reference GitHub's [Wiki documentation](https://docs.github.com/en/communities/documenting-your-project-with-wikis)
- Open an issue on GitHub if you encounter problems

---

**Created:** 2025-12-13
**Version:** Initial setup for v1.2.0+
**Status:** Ready to publish
