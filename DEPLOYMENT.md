# Deployment Guide

This document outlines the automated deployment process for Cobblemon Quests Extended.

## Overview

The project uses GitHub Actions for automated building and publishing to Modrinth. Releases are triggered by pushing version tags.

## Prerequisites

### Required GitHub Secrets

Configure these secrets in your GitHub repository settings (Settings > Secrets and variables > Actions):

| Secret Name | Description | How to obtain |
|------------|-------------|---------------|
| `MODRINTH_TOKEN` | Modrinth API token | Generate at https://modrinth.com/settings/pats |
| `MODRINTH_PROJECT_ID` | Your project's unique ID | Found in project settings URL or API |

### Required Tools

- Git
- Access to push tags to the repository

## Release Process

### 1. Update Version

Edit `gradle.properties` and update the version:

```properties
mod_version=1.2.0
```

### 2. Update Changelog

Edit `CHANGELOG.md` following [Keep a Changelog](https://keepachangelog.com/) format:

```markdown
## [1.2.0] - 2025-12-14

### Added
- New feature descriptions

### Changed
- Modified behavior descriptions

### Fixed
- Bug fix descriptions
```

### 3. Commit Changes

```bash
git add gradle.properties CHANGELOG.md
git commit -m "chore: bump version to 1.2.0"
git push
```

### 4. Create and Push Release Tag

The tag format determines the release channel:

```bash
# Production release
git tag v1.2.0
git push origin v1.2.0

# Beta release
git tag v1.2.0-beta.1
git push origin v1.2.0-beta.1

# Alpha release
git tag v1.2.0-alpha.1
git push origin v1.2.0-alpha.1
```

### 5. Monitor Build

1. Go to your repository's Actions tab
2. Watch the "Release to Modrinth" workflow
3. Build typically takes 5-10 minutes

### 6. Verify Release

Check that the release appears on:
- Modrinth project page
- GitHub Releases page

## Workflow Details

### Trigger Conditions

The workflow triggers on any tag matching `v*`:
- `v1.0.0` → Release channel
- `v1.0.0-beta.1` → Beta channel
- `v1.0.0-alpha.1` → Alpha channel

### Build Process

1. **Parse version from tag**: Strips the `v` prefix and determines release channel
2. **Extract changelog**: Pulls the relevant section from CHANGELOG.md
3. **Build artifacts**: Runs `./gradlew build` to create both Fabric and NeoForge jars
4. **Publish to Modrinth**: Runs `./gradlew modrinth` to upload both variants
5. **Create GitHub Release**: Attaches artifacts and changelog

### Artifact Naming

Built artifacts follow this pattern:
- Fabric: `cobblemon_quests_extended-1.21.1-fabric-{version}.jar`
- NeoForge: `cobblemon_quests_extended-1.21.1-neoforge-{version}.jar`

Note: No brackets `[]` in filenames to avoid shell glob issues.

## Modrinth Configuration

### Dependencies

Both Fabric and NeoForge versions declare:

**Required:**
- Cobblemon
- FTB Quests (platform-specific variant)

**Optional:**
- FTB Teams (platform-specific variant)
- FTB Library (platform-specific variant)

### Loaders

- **Fabric build**: `fabric`, `quilt` (Quilt is Fabric-compatible)
- **NeoForge build**: `neoforge`

### Version Naming

- Fabric: `[Fabric 1.21.1] 1.2.0`
- NeoForge: `[NeoForge 1.21.1] 1.2.0`

## Troubleshooting

### Build Fails

**Symptom**: GitHub Actions workflow fails during build step

**Solutions**:
1. Check build logs in Actions tab
2. Test locally: `./gradlew build`
3. Verify all dependencies are available
4. Check Java version (requires JDK 21)

### Modrinth Upload Fails

**Symptom**: Build succeeds but Modrinth upload fails

**Solutions**:
1. Verify `MODRINTH_TOKEN` secret is set correctly
2. Verify `MODRINTH_PROJECT_ID` secret matches your project
3. Check token has "Upload versions" permission
4. Ensure project exists on Modrinth

### GitHub Release Fails

**Symptom**: Modrinth upload succeeds but GitHub release fails

**Solutions**:
1. Check repository permissions
2. Verify workflow has `contents: write` permission
3. Ensure tag matches pattern `v*`

### Artifacts Not Found

**Symptom**: Workflow can't find built JARs

**Solutions**:
1. Check artifact paths in workflow file
2. Verify build creates jars in expected locations
3. Look at "Find built artifacts" step logs

## Manual Release (Fallback)

If automated release fails, you can release manually:

### Build Locally

```bash
cd /home/sauk/cobble-quests/cobblemon-quests-extended
./gradlew build
```

### Upload to Modrinth

1. Go to your Modrinth project page
2. Click "Create a version"
3. Upload both jars:
   - `fabric/build/libs/cobblemon_quests_extended-1.21.1-fabric-{version}.jar`
   - `neoforge/build/libs/cobblemon_quests_extended-1.21.1-neoforge-{version}.jar`
4. Fill in version details and publish

### Create GitHub Release

```bash
gh release create v1.2.0 \
  --title "Release 1.2.0" \
  --notes-file CHANGELOG.md \
  fabric/build/libs/cobblemon_quests_extended-1.21.1-fabric-1.2.0.jar \
  neoforge/build/libs/cobblemon_quests_extended-1.21.1-neoforge-1.2.0.jar
```

## Rollback Procedures

### Remove Bad Release from Modrinth

1. Go to project versions on Modrinth
2. Find the problematic version
3. Click "Delete version"
4. Confirm deletion

### Delete GitHub Release

```bash
# Delete release
gh release delete v1.2.0 --yes

# Delete tag locally and remotely
git tag -d v1.2.0
git push origin :refs/tags/v1.2.0
```

### Revert Version Changes

```bash
# Reset to previous commit
git revert HEAD
git push
```

## Health Checks

After deployment, verify:

- [ ] Modrinth page shows new version
- [ ] Both Fabric and NeoForge variants are listed
- [ ] Dependencies are correctly declared
- [ ] Changelog is displayed properly
- [ ] Download counts are incrementing
- [ ] GitHub release is created with artifacts
- [ ] Tag is visible in repository

## Release Checklist

Use this checklist for each release:

- [ ] Version bumped in `gradle.properties`
- [ ] CHANGELOG.md updated with all changes
- [ ] Changes committed and pushed to main/develop
- [ ] Tag created with correct format
- [ ] Tag pushed to remote
- [ ] GitHub Actions workflow completed successfully
- [ ] Modrinth page shows new version
- [ ] GitHub release created with artifacts
- [ ] Test download and installation of both variants
- [ ] Announce release (Discord, Reddit, etc.)

## Environment Variables

The workflow and build scripts use these environment variables:

| Variable | Set By | Used For |
|----------|--------|----------|
| `MODRINTH_TOKEN` | GitHub Secrets | Authentication with Modrinth API |
| `MODRINTH_PROJECT_ID` | GitHub Secrets | Identifying target project |
| `RELEASE_CHANNEL` | Workflow (computed) | Determining alpha/beta/release |
| `GITHUB_TOKEN` | GitHub (automatic) | Creating GitHub releases |

## Security Considerations

1. **Never commit secrets**: Tokens must only be in GitHub Secrets
2. **Token permissions**: Modrinth token should have minimal required permissions
3. **Branch protection**: Consider protecting main/develop branches
4. **Tag protection**: Consider protecting version tags from deletion

## CI/CD Pipeline Architecture

```
┌─────────────┐
│  Git Tag    │
│   (v1.2.0)  │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────┐
│  GitHub Actions Workflow    │
├─────────────────────────────┤
│ 1. Parse version & channel  │
│ 2. Extract changelog        │
│ 3. Setup JDK 21             │
│ 4. Build with Gradle        │
│ 5. Publish to Modrinth      │
│ 6. Create GitHub Release    │
└──────┬──────────────┬───────┘
       │              │
       ▼              ▼
┌─────────────┐  ┌──────────────┐
│  Modrinth   │  │   GitHub     │
│  (public)   │  │   Releases   │
└─────────────┘  └──────────────┘
```

## Support

For issues with the deployment process:
1. Check GitHub Actions logs
2. Review this documentation
3. Check Modrinth API status
4. Open an issue in the repository
