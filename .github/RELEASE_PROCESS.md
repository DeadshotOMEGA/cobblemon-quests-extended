# Quick Release Guide

## TL;DR

```bash
# 1. Update version in gradle.properties
# 2. Update CHANGELOG.md
# 3. Commit changes
git add gradle.properties CHANGELOG.md
git commit -m "chore: bump version to 1.2.0"
git push

# 4. Create and push tag
git tag v1.2.0
git push origin v1.2.0

# 5. Watch GitHub Actions complete the release
```

## Release Channels

| Tag Format | Channel | Use Case |
|------------|---------|----------|
| `v1.2.0` | release | Stable production release |
| `v1.2.0-beta.1` | beta | Testing before release |
| `v1.2.0-alpha.1` | alpha | Early development preview |

## What Happens Automatically

1. GitHub Actions builds both Fabric and NeoForge jars
2. Uploads to Modrinth with correct metadata
3. Creates GitHub Release with changelog
4. Attaches artifacts to GitHub Release

## Required Secrets

Set once in GitHub repository settings:
- `MODRINTH_TOKEN` - From https://modrinth.com/settings/pats
- `MODRINTH_PROJECT_ID` - From your Modrinth project settings

## Rollback

```bash
# Delete release and tag
gh release delete v1.2.0 --yes
git tag -d v1.2.0
git push origin :refs/tags/v1.2.0

# Revert commits if needed
git revert HEAD
git push
```

See [DEPLOYMENT.md](../DEPLOYMENT.md) for full documentation.
