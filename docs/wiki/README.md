# GitHub Wiki Setup

This directory contains the wiki pages for the Cobblemon Quests Extended project.

## Wiki Pages

- **Home.md** - Wiki homepage with overview and quick links
- **Getting-Started.md** - Installation, setup, and basic configuration
- **Actions.md** - Complete reference for all quest actions
- **Conditions.md** - Complete reference for all filtering conditions
- **Examples.md** - Quest examples and templates
- **Action-Picker.md** - Documentation for the v1.2.0 action picker UI
- **_Sidebar.md** - Navigation sidebar for the wiki

## Publishing to GitHub Wiki

GitHub wikis are separate git repositories. To publish these pages:

### Option 1: Manual Upload (Easiest)

1. Go to the [Wiki tab](https://github.com/DeadshotOMEGA/cobblemon-quests-extended/wiki) on GitHub
2. Click "Create the first page" or "New Page" for each file
3. Copy the content from each `.md` file and paste into the GitHub editor
4. Save each page

### Option 2: Git Clone & Push

1. **Clone the wiki repository:**
   ```bash
   git clone https://github.com/DeadshotOMEGA/cobblemon-quests-extended.wiki.git
   cd cobblemon-quests-extended.wiki
   ```

2. **Copy wiki files:**
   ```bash
   cp /home/sauk/cobble-quests/cobblemon-quests-extended/docs/wiki/*.md .
   ```

3. **Commit and push:**
   ```bash
   git add .
   git commit -m "docs: add comprehensive wiki documentation"
   git push origin master
   ```

### Option 3: Automated Script

Create a script to sync the wiki:

```bash
#!/bin/bash
# sync-wiki.sh

WIKI_DIR="cobblemon-quests-extended.wiki"
DOCS_DIR="/home/sauk/cobble-quests/cobblemon-quests-extended/docs/wiki"

# Clone wiki if not exists
if [ ! -d "$WIKI_DIR" ]; then
    git clone https://github.com/DeadshotOMEGA/cobblemon-quests-extended.wiki.git
fi

cd "$WIKI_DIR"

# Pull latest changes
git pull

# Copy files
cp "$DOCS_DIR"/*.md .

# Commit and push
git add .
git commit -m "docs: update wiki from source"
git push origin master

echo "Wiki updated successfully!"
```

## Wiki Structure

The wiki is organized into the following sections:

### Getting Started
- **Home** - Overview, features, and quick links
- **Getting Started** - Installation and basic setup
- **Action Picker** - UI guide for v1.2.0+

### Quest Creation
- **Actions** - All available actions (catch, defeat, evolve, etc.)
- **Conditions** - Filtering options (species, shiny, level, etc.)
- **Examples** - Complete quest examples

### Navigation
- **_Sidebar** - Navigation menu (appears on all pages)

## Updating the Wiki

When you make changes to the wiki pages in this directory:

1. **Edit the files** in `/docs/wiki/`
2. **Test locally** by previewing the Markdown
3. **Sync to GitHub Wiki** using one of the methods above

## Linking Between Pages

Use relative links without the `.md` extension:

```markdown
[Getting Started](Getting-Started)
[Actions Reference](Actions)
```

## External Links

Link to main repository docs:

```markdown
[Contributing Guide](https://github.com/DeadshotOMEGA/cobblemon-quests-extended/blob/develop/docs/CONTRIBUTING.md)
```

## Troubleshooting

### Wiki Not Found Error
If `git clone` fails with "Repository not found":
1. Go to the repository on GitHub
2. Click the "Wiki" tab
3. Click "Create the first page"
4. Save it (this initializes the wiki)
5. Try cloning again

### Permission Denied
Make sure you have push access to the repository.

### Conflicts
If you get merge conflicts:
```bash
cd cobblemon-quests-extended.wiki
git pull --rebase
# Resolve conflicts if any
git push
```

## Notes

- Wiki pages are stored in a **separate git repository** from the main project
- The wiki URL is: `https://github.com/DeadshotOMEGA/cobblemon-quests-extended.wiki.git`
- Wiki pages use **GitHub Flavored Markdown**
- The sidebar is automatically shown on all pages when `_Sidebar.md` exists
