# Deployment Pipeline Diagram

## Overview

This document provides visual representations of the CI/CD pipeline for Cobblemon Quests Extended.

## Full Release Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                        DEVELOPER WORKFLOW                            │
└─────────────────────────────────────────────────────────────────────┘

    1. Update gradle.properties (mod_version=1.2.0)
                      ↓
    2. Update CHANGELOG.md with changes
                      ↓
    3. git commit -m "chore: bump version to 1.2.0"
                      ↓
    4. git push
                      ↓
    5. git tag v1.2.0
                      ↓
    6. git push origin v1.2.0

┌─────────────────────────────────────────────────────────────────────┐
│                     GITHUB ACTIONS WORKFLOW                          │
└─────────────────────────────────────────────────────────────────────┘

    Trigger: Tag matching v*
                      ↓
    ┌─────────────────────────────────────┐
    │  Step 1: Parse Version & Channel    │
    │  - Extract version from tag         │
    │  - Determine release channel        │
    │  - Extract changelog section        │
    └────────────┬────────────────────────┘
                 ↓
    ┌─────────────────────────────────────┐
    │  Step 2: Setup Build Environment    │
    │  - Checkout code (full history)     │
    │  - Setup JDK 21                     │
    │  - Validate Gradle wrapper          │
    └────────────┬────────────────────────┘
                 ↓
    ┌─────────────────────────────────────┐
    │  Step 3: Build Artifacts            │
    │  - ./gradlew build                  │
    │  - Build Fabric JAR                 │
    │  - Build NeoForge JAR               │
    └────────────┬────────────────────────┘
                 ↓
         ┌───────┴──────┐
         ↓              ↓
┌─────────────────┐  ┌──────────────────┐
│ Step 4a:        │  │ Step 4b:         │
│ Publish         │  │ Create GitHub    │
│ to Modrinth     │  │ Release          │
│                 │  │                  │
│ - Upload Fabric │  │ - Attach JARs    │
│ - Upload NeoF   │  │ - Add changelog  │
│ - Set metadata  │  │ - Set prerelease │
└─────────────────┘  └──────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                         FINAL OUTPUTS                                │
└─────────────────────────────────────────────────────────────────────┘

    Modrinth:                      GitHub:
    ├─ Fabric version              ├─ Release v1.2.0
    │  ├─ Loader: fabric, quilt    │  ├─ Changelog
    │  ├─ Version: 1.2.0           │  ├─ fabric-1.2.0.jar
    │  └─ Dependencies              │  └─ neoforge-1.2.0.jar
    │                               │
    └─ NeoForge version             └─ Build artifacts (archived)
       ├─ Loader: neoforge
       ├─ Version: 1.2.0
       └─ Dependencies
```

## Release Channel Detection

```
Tag Format           →    Release Channel    →    Visibility
─────────────────────────────────────────────────────────────
v1.2.0               →    release            →    All users
v1.2.0-beta.1        →    beta               →    Opt-in
v1.2.0-alpha.1       →    alpha              →    Opt-in
v1.2.0-rc.1          →    beta               →    Opt-in
```

## Build Artifact Flow

```
Source Code (Kotlin)
        ↓
┌───────────────────┐
│  Common Module    │  ← Shared code
└─────────┬─────────┘
          ↓
    ┌─────┴─────┐
    ↓           ↓
┌─────────┐ ┌──────────┐
│ Fabric  │ │ NeoForge │
│ Module  │ │  Module  │
└────┬────┘ └────┬─────┘
     ↓           ↓
┌─────────┐ ┌──────────┐
│ Shadow  │ │  Shadow  │
│   JAR   │ │    JAR   │
└────┬────┘ └────┬─────┘
     ↓           ↓
┌─────────┐ ┌──────────┐
│ Remap   │ │  Remap   │
│   JAR   │ │    JAR   │
└────┬────┘ └────┬─────┘
     ↓           ↓
Final Artifacts:
- cobblemon_quests_extended-1.21.1-fabric-1.2.0.jar
- cobblemon_quests_extended-1.21.1-neoforge-1.2.0.jar
```

## Dependency Resolution

```
Modrinth Upload
        ↓
    ┌───────────────────────────────────────┐
    │  Metadata Configuration               │
    ├───────────────────────────────────────┤
    │  Game Version: 1.21.1                 │
    │  Version Type: release/beta/alpha     │
    │  Changelog: From CHANGELOG.md         │
    └───────────┬───────────────────────────┘
                ↓
        ┌───────┴──────┐
        ↓              ↓
┌───────────────┐  ┌──────────────┐
│ Fabric Build  │  │ NeoForge Build│
├───────────────┤  ├──────────────┤
│ Loaders:      │  │ Loaders:     │
│  - fabric     │  │  - neoforge  │
│  - quilt      │  │              │
│               │  │              │
│ Required:     │  │ Required:    │
│  - cobblemon  │  │  - cobblemon │
│  - ftb-quests │  │  - ftb-quests│
│    -fabric    │  │    -neoforge │
│               │  │              │
│ Optional:     │  │ Optional:    │
│  - ftb-teams  │  │  - ftb-teams │
│    -fabric    │  │    -neoforge │
│  - ftb-library│  │  - ftb-library│
│    -fabric    │  │    -neoforge │
└───────────────┘  └──────────────┘
```

## Environment Variables Flow

```
GitHub Repository Secrets
    ├─ MODRINTH_TOKEN
    └─ MODRINTH_PROJECT_ID
            ↓
    GitHub Actions Workflow
    (release.yml)
            ↓
    Environment Variables Set
    ├─ MODRINTH_TOKEN
    ├─ MODRINTH_PROJECT_ID
    └─ RELEASE_CHANNEL (computed)
            ↓
    Gradle Build Process
            ↓
    Minotaur Plugin
    (fabric/neoforge)
            ↓
    Modrinth API Upload
```

## Testing & Validation Flow

```
Local Development
        ↓
┌──────────────────────┐
│ scripts/             │
│ test-release.sh      │
├──────────────────────┤
│ ✓ Check CHANGELOG    │
│ ✓ Build locally      │
│ ✓ Verify artifacts   │
│ ✓ Validate metadata  │
└──────────┬───────────┘
           ↓
    Push to GitHub
           ↓
┌──────────────────────┐
│ .github/workflows/   │
│ build.yml            │
├──────────────────────┤
│ ✓ Build on PR        │
│ ✓ Build on commit    │
│ ✓ Archive artifacts  │
└──────────┬───────────┘
           ↓
    Create Tag
           ↓
┌──────────────────────┐
│ .github/workflows/   │
│ release.yml          │
├──────────────────────┤
│ ✓ Full release       │
│ ✓ Modrinth publish   │
│ ✓ GitHub release     │
└──────────────────────┘
```

## Error Handling & Rollback

```
Release Failure Detected
            ↓
    ┌───────┴────────┐
    ↓                ↓
Build Failed    Upload Failed
    ↓                ↓
Check Logs      Check Secrets
    ↓                ↓
Fix Code        Verify Config
    ↓                ↓
Retry           Retry
    ↓                ↓
Success!        Success!

Successful but Wrong Version
            ↓
    ┌───────────────────┐
    │ Rollback Process  │
    ├───────────────────┤
    │ 1. Delete from    │
    │    Modrinth       │
    │ 2. Delete GitHub  │
    │    Release        │
    │ 3. Delete tag     │
    │ 4. Revert commits │
    │ 5. Fix issues     │
    │ 6. Retry          │
    └───────────────────┘
```

## Timeline View (Typical Release)

```
Time    Developer              GitHub Actions           External Services
───────────────────────────────────────────────────────────────────────
T+0s    git push origin v1.2.0
                               Workflow triggered
T+10s                          Checkout & setup
T+30s                          Parse version
T+1m                           Build starts
T+5m                           Build completes
T+6m                           Modrinth upload          → Modrinth processing
T+7m                                                    → Modrinth live
T+8m                           GitHub release created   → GitHub live
T+9m    Notification received
        Verification
───────────────────────────────────────────────────────────────────────
Total: ~10 minutes from tag push to full availability
```

## Monitoring Points

```
┌─────────────────────────────────────────────────────────────┐
│                    HEALTH CHECK POINTS                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  1. GitHub Actions Status                                   │
│     └─ Actions tab → Latest workflow run                    │
│                                                              │
│  2. Build Artifacts                                          │
│     └─ Workflow artifacts → Download & verify               │
│                                                              │
│  3. Modrinth Publication                                     │
│     └─ Project page → Versions → Check both loaders         │
│                                                              │
│  4. GitHub Release                                           │
│     └─ Releases page → Check changelog & attachments        │
│                                                              │
│  5. Download Test                                            │
│     └─ Download both JARs → Test in game                    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## Security Boundaries

```
┌─────────────────────────────────────────────────────────────┐
│                      SECRET STORAGE                          │
│                                                              │
│  GitHub Repository Settings (Encrypted)                      │
│  ├─ MODRINTH_TOKEN        ← Never in code                   │
│  └─ MODRINTH_PROJECT_ID   ← Never in code                   │
│                                                              │
└──────────────────────┬───────────────────────────────────────┘
                       ↓
            Injected as Environment Variables
                       ↓
┌──────────────────────────────────────────────────────────────┐
│                 WORKFLOW EXECUTION CONTEXT                    │
│                                                              │
│  Read-only access to secrets                                 │
│  Secrets never logged                                        │
│  Secrets cleared after workflow                              │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

**Legend:**
- `→` Flow direction
- `├─` Tree structure
- `↓` Sequential step
- `┌─┐` Component boundary
- `✓` Validation checkpoint
