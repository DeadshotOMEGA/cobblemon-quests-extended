#!/bin/bash
# sync-wiki.sh - Sync local wiki files to GitHub Wiki

set -e

WIKI_REPO="https://github.com/DeadshotOMEGA/cobblemon-quests-extended.wiki.git"
WIKI_DIR="cobblemon-quests-extended.wiki"
DOCS_DIR="$(dirname "$0")"

echo "=== Cobblemon Quests Extended - Wiki Sync ==="
echo ""

# Check if wiki directory exists
if [ ! -d "$WIKI_DIR" ]; then
    echo "Cloning wiki repository..."
    git clone "$WIKI_REPO" "$WIKI_DIR"
    echo "✓ Wiki repository cloned"
else
    echo "Wiki repository already exists"
    cd "$WIKI_DIR"
    echo "Pulling latest changes..."
    git pull
    cd ..
fi

echo ""
echo "Copying wiki files..."

# Copy all markdown files except README and SUMMARY
cd "$DOCS_DIR"
for file in *.md; do
    if [ "$file" != "README.md" ] && [ "$file" != "WIKI-SETUP-SUMMARY.md" ]; then
        cp -v "$file" "$WIKI_DIR/"
        echo "  ✓ Copied $file"
    fi
done

echo ""
echo "Committing changes..."
cd "$WIKI_DIR"

# Check if there are changes
if [ -z "$(git status --porcelain)" ]; then
    echo "No changes to commit"
    exit 0
fi

# Show what will be committed
echo "Changes to be committed:"
git status --short

echo ""
read -p "Commit and push these changes? (y/n) " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    git add .
    git commit -m "docs: update wiki documentation

Updated from /docs/wiki/ in main repository"

    echo "Pushing to GitHub..."
    git push origin master

    echo ""
    echo "✓ Wiki updated successfully!"
    echo ""
    echo "View at: https://github.com/DeadshotOMEGA/cobblemon-quests-extended/wiki"
else
    echo "Sync cancelled"
    exit 1
fi
