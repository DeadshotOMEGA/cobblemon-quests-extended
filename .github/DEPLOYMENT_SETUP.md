# Deployment Setup Summary

This document summarizes the automated deployment configuration for Cobblemon Quests Extended.

## Files Created/Modified

### GitHub Actions Workflow
- `.github/workflows/release.yml` - Main CI/CD pipeline for automated releases

### Build Configuration
- `build.gradle.kts` - Added Minotaur plugin
- `fabric/build.gradle.kts` - Added Minotaur configuration for Fabric
- `neoforge/build.gradle.kts` - Added Minotaur configuration for NeoForge

### Documentation
- `DEPLOYMENT.md` - Comprehensive deployment guide
- `.github/RELEASE_PROCESS.md` - Quick release reference
- `.github/DEPLOYMENT_SETUP.md` - This file

### Scripts
- `scripts/test-release.sh` - Local build validation script

## Architecture Overview

```
Developer Push Tag (v1.2.0)
           ↓
    GitHub Actions Triggered
           ↓
    ┌──────────────────┐
    │  Parse Version   │
    │  Extract Changes │
    └────────┬─────────┘
             ↓
    ┌──────────────────┐
    │   Build Jars     │
    │  Fabric + NeoF   │
    └────────┬─────────┘
             ↓
      ┌─────┴──────┐
      ↓            ↓
┌──────────┐  ┌────────────┐
│ Modrinth │  │   GitHub   │
│ Publish  │  │   Release  │
└──────────┘  └────────────┘
```

## Key Features

### 1. Zero-Manual-Deployment
- Push a git tag → Everything else is automatic
- No manual file uploads
- No manual changelog copying

### 2. Multi-Platform Support
- Single workflow builds both Fabric and NeoForge
- Both uploaded to Modrinth automatically
- Correct loader tags and dependencies per platform

### 3. Release Channel Detection
- `v1.2.0` → Release channel
- `v1.2.0-beta.1` → Beta channel
- `v1.2.0-alpha.1` → Alpha channel
- Automatic detection from tag format

### 4. Changelog Automation
- Extracts relevant section from CHANGELOG.md
- Includes in both Modrinth and GitHub releases
- Supports Keep a Changelog format

### 5. Dependency Management
- Required: Cobblemon, FTB Quests (platform-specific)
- Optional: FTB Teams, FTB Library (platform-specific)
- Quilt compatibility for Fabric build

## Security Configuration

### GitHub Secrets Required

Set in repository Settings → Secrets and variables → Actions:

1. **MODRINTH_TOKEN**
   - Generate at: https://modrinth.com/settings/pats
   - Required permissions: "Upload versions"
   - Never commit this to code

2. **MODRINTH_PROJECT_ID**
   - Found in your Modrinth project settings
   - Format: 8-character alphanumeric (e.g., `abc12345`)

### Permissions

The workflow has these permissions:
- `contents: write` - To create GitHub releases
- Uses `GITHUB_TOKEN` automatically for GitHub API

## Build Process Details

### Gradle Tasks
```bash
./gradlew build          # Build both platforms
./gradlew modrinth       # Publish to Modrinth
./gradlew tasks          # See all available tasks
```

### Artifact Paths
```
fabric/build/libs/cobblemon_quests_extended-1.21.1-fabric-{version}.jar
neoforge/build/libs/cobblemon_quests_extended-1.21.1-neoforge-{version}.jar
```

### Version Resolution
- Version number: From `gradle.properties` `mod_version`
- Game version: From `gradle.properties` `minecraft_version`
- Tag format: Must match `v{version}` (e.g., `v1.2.0`)

## Environment Variables

| Variable | Source | Purpose |
|----------|--------|---------|
| `MODRINTH_TOKEN` | GitHub Secret | Authenticate with Modrinth API |
| `MODRINTH_PROJECT_ID` | GitHub Secret | Target Modrinth project |
| `RELEASE_CHANNEL` | Computed from tag | Set version type (release/beta/alpha) |
| `GITHUB_TOKEN` | Auto-provided | Create GitHub releases |

## Workflow Steps

1. **Checkout** - Clone repository with full history
2. **Setup JDK 21** - Install Java for building
3. **Parse version** - Extract version and channel from tag
4. **Extract changelog** - Pull relevant section from CHANGELOG.md
5. **Validate Gradle** - Security check for Gradle wrapper
6. **Build** - `./gradlew build` both platforms
7. **Publish to Modrinth** - `./gradlew modrinth` upload
8. **Find artifacts** - Locate built JAR files
9. **Create GitHub Release** - Attach JARs with changelog
10. **Upload artifacts** - Archive for debugging (30 days)

## Testing Before Release

Run the test script to validate build locally:

```bash
./scripts/test-release.sh
```

This checks:
- CHANGELOG.md has entry for current version
- Build completes successfully
- Artifacts are created in expected locations
- JARs contain required metadata files
- Environment variables are set (warning only)

## Rollback Procedures

### Quick Rollback
```bash
# Delete release from GitHub
gh release delete v1.2.0 --yes

# Delete tag
git tag -d v1.2.0
git push origin :refs/tags/v1.2.0

# Delete from Modrinth (manual)
# Go to project → Versions → Delete version
```

### Full Rollback
```bash
# Revert code changes
git revert HEAD
git push

# Follow quick rollback steps above
```

## Monitoring

### GitHub Actions
- View workflow runs: Repository → Actions tab
- Check logs for each step
- Download build artifacts for debugging

### Modrinth
- Project page shows new version immediately
- Check dependency declarations
- Verify loader tags (fabric/quilt/neoforge)

### GitHub Releases
- Shows up on repository Releases page
- Artifacts attached automatically
- Changelog from CHANGELOG.md

## Troubleshooting Quick Reference

| Issue | Solution |
|-------|----------|
| Build fails | Check Actions logs, test locally with `./gradlew build` |
| Modrinth upload fails | Verify secrets are set correctly |
| Artifacts not found | Check version matches between tag and gradle.properties |
| Changelog missing | Ensure CHANGELOG.md has section for version |
| Wrong release channel | Check tag format (v1.2.0 vs v1.2.0-beta.1) |

## Best Practices

1. **Always test locally first**
   ```bash
   ./scripts/test-release.sh
   ```

2. **Keep CHANGELOG.md updated**
   - Update before creating tag
   - Follow Keep a Changelog format
   - Include all user-facing changes

3. **Version numbering**
   - Use semantic versioning (MAJOR.MINOR.PATCH)
   - Match version in gradle.properties and tag
   - Don't skip versions

4. **Release cadence**
   - Use alpha for experimental features
   - Use beta for testing before release
   - Use release for stable versions

5. **Git workflow**
   - Commit version bump separately
   - Tag after commit is pushed
   - Don't tag uncommitted changes

## Future Enhancements

Potential improvements to consider:

- [ ] Add CurseForge publishing
- [ ] Automated version bumping
- [ ] Discord/Reddit release announcements
- [ ] Automated testing before release
- [ ] Multi-Minecraft-version support
- [ ] Release notes templating
- [ ] Dependency version checking

## Support Resources

- **Minotaur documentation**: https://github.com/modrinth/minotaur
- **GitHub Actions docs**: https://docs.github.com/actions
- **Modrinth API**: https://docs.modrinth.com/
- **Keep a Changelog**: https://keepachangelog.com/

## Configuration Files Reference

### Gradle Plugin Versions
- Minotaur: `2.+` (latest 2.x)
- Architectury Loom: `1.11-SNAPSHOT`
- Shadow: `8.1.1`

### Java Version
- Build: JDK 21 (Temurin distribution)
- Source/Target compatibility: Set by Architectury

### Gradle Settings
- Daemon: Disabled in CI (`--no-daemon`)
- Build cache: Managed by GitHub Actions
- JVM args: 4GB memory (`org.gradle.jvmargs=-Xmx4G`)

## Success Indicators

A successful release will show:

1. ✅ GitHub Actions workflow completes (green checkmark)
2. ✅ Modrinth shows two new versions (Fabric + NeoForge)
3. ✅ GitHub Release is created with 2 JAR attachments
4. ✅ Changelog is visible in release notes
5. ✅ Build artifacts are archived in Actions
6. ✅ Version numbers match across all platforms

## Release Checklist Template

Copy this for each release:

```markdown
## Release v1.2.0 Checklist

- [ ] Version updated in gradle.properties
- [ ] CHANGELOG.md updated with all changes
- [ ] Local test build passes (./scripts/test-release.sh)
- [ ] Changes committed and pushed
- [ ] Tag created (git tag v1.2.0)
- [ ] Tag pushed (git push origin v1.2.0)
- [ ] GitHub Actions workflow succeeded
- [ ] Modrinth versions published (Fabric + NeoForge)
- [ ] GitHub Release created with artifacts
- [ ] Downloaded and tested both JARs
- [ ] Announced on community channels
```

---

**Last Updated**: 2025-12-13
**Deployment Version**: 1.0
**Workflow Version**: 1.0
