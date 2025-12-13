#!/bin/bash
# Test script to validate release build locally
# Run this before pushing tags to catch issues early

set -e  # Exit on error

echo "================================"
echo "Release Build Test"
echo "================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get current version from gradle.properties
VERSION=$(grep '^mod_version=' gradle.properties | cut -d'=' -f2)
echo -e "${YELLOW}Current version: $VERSION${NC}"
echo ""

# Check if CHANGELOG.md has entry for this version
if grep -q "\[$VERSION\]" CHANGELOG.md; then
    echo -e "${GREEN}✓ CHANGELOG.md has entry for version $VERSION${NC}"
else
    echo -e "${RED}✗ CHANGELOG.md missing entry for version $VERSION${NC}"
    echo "  Add a section like:"
    echo "  ## [$VERSION] - $(date +%Y-%m-%d)"
    exit 1
fi

echo ""
echo "================================"
echo "Building with Gradle..."
echo "================================"
echo ""

# Clean previous builds
./gradlew clean --no-daemon

# Build both platforms
./gradlew build --no-daemon

echo ""
echo "================================"
echo "Checking artifacts..."
echo "================================"
echo ""

# Find built artifacts
FABRIC_JAR=$(find fabric/build/libs -name "*fabric-$VERSION.jar" -not -name "*dev*" | head -1)
NEOFORGE_JAR=$(find neoforge/build/libs -name "*neoforge-$VERSION.jar" -not -name "*dev*" | head -1)

if [ -f "$FABRIC_JAR" ]; then
    SIZE=$(du -h "$FABRIC_JAR" | cut -f1)
    echo -e "${GREEN}✓ Fabric JAR built: $FABRIC_JAR ($SIZE)${NC}"
else
    echo -e "${RED}✗ Fabric JAR not found${NC}"
    echo "  Expected pattern: fabric/build/libs/*fabric-$VERSION.jar"
    exit 1
fi

if [ -f "$NEOFORGE_JAR" ]; then
    SIZE=$(du -h "$NEOFORGE_JAR" | cut -f1)
    echo -e "${GREEN}✓ NeoForge JAR built: $NEOFORGE_JAR ($SIZE)${NC}"
else
    echo -e "${RED}✗ NeoForge JAR not found${NC}"
    echo "  Expected pattern: neoforge/build/libs/*neoforge-$VERSION.jar"
    exit 1
fi

echo ""
echo "================================"
echo "Validating JAR contents..."
echo "================================"
echo ""

# Check Fabric JAR has fabric.mod.json
if unzip -l "$FABRIC_JAR" | grep -q "fabric.mod.json"; then
    echo -e "${GREEN}✓ Fabric JAR contains fabric.mod.json${NC}"
else
    echo -e "${RED}✗ Fabric JAR missing fabric.mod.json${NC}"
    exit 1
fi

# Check NeoForge JAR has META-INF/neoforge.mods.toml
if unzip -l "$NEOFORGE_JAR" | grep -q "META-INF/neoforge.mods.toml"; then
    echo -e "${GREEN}✓ NeoForge JAR contains META-INF/neoforge.mods.toml${NC}"
else
    echo -e "${RED}✗ NeoForge JAR missing META-INF/neoforge.mods.toml${NC}"
    exit 1
fi

echo ""
echo "================================"
echo "Environment check..."
echo "================================"
echo ""

# Check for required environment variables (won't fail, just warn)
if [ -z "$MODRINTH_TOKEN" ]; then
    echo -e "${YELLOW}⚠ MODRINTH_TOKEN not set (required for publishing)${NC}"
else
    echo -e "${GREEN}✓ MODRINTH_TOKEN is set${NC}"
fi

if [ -z "$MODRINTH_PROJECT_ID" ]; then
    echo -e "${YELLOW}⚠ MODRINTH_PROJECT_ID not set (required for publishing)${NC}"
else
    echo -e "${GREEN}✓ MODRINTH_PROJECT_ID is set${NC}"
fi

echo ""
echo "================================"
echo -e "${GREEN}Build test passed!${NC}"
echo "================================"
echo ""
echo "Ready to release version $VERSION"
echo ""
echo "Next steps:"
echo "  1. Commit changes: git add . && git commit -m 'chore: bump version to $VERSION'"
echo "  2. Push commits: git push"
echo "  3. Create tag: git tag v$VERSION"
echo "  4. Push tag: git push origin v$VERSION"
echo ""
echo "The GitHub Actions workflow will automatically:"
echo "  - Build both Fabric and NeoForge jars"
echo "  - Publish to Modrinth"
echo "  - Create GitHub Release with changelog"
echo ""
