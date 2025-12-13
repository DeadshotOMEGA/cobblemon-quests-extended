plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.modrinth.minotaur")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

configurations {
    create("common")
    create("shadowCommon")
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    getByName("developmentFabric").extendsFrom(configurations["common"])
}

loom {
    enableTransitiveAccessWideners.set(true)
    silentMojangMappingsLicense()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")

    mappings(loom.officialMojangMappings())

    modApi("dev.architectury:architectury-fabric:${property("architectury_version")}")
    modApi("teamreborn:energy:4.1.0")

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin")}")
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")


    modImplementation(fabricApi.module("fabric-command-api-v2", "0.89.3+1.20.1"))


    "common"(project(":common", "namedElements")) { isTransitive = false }
    "shadowCommon"(project(":common", "transformProductionFabric")) { isTransitive = false }


    modImplementation("com.cobblemon:fabric:${property("cobblemon_version")}")
    modImplementation("dev.ftb.mods:ftb-quests-fabric:${property("ftb_quests_version")}") { isTransitive = false }
    modImplementation("dev.ftb.mods:ftb-teams-fabric:${property("ftb_teams_version")}") { isTransitive = false }
    modImplementation("dev.ftb.mods:ftb-library-fabric:${property("ftb_lib_version")}") { isTransitive = false }
}

tasks.processResources {
    expand(mapOf(
        "mod_name" to project.property("mod_name"),
        "mod_id" to project.property("mod_id"),
        "mod_version" to project.property("mod_version"),
        "mod_description" to project.property("mod_description"),
        "repository" to project.property("repository"),
        "license" to project.property("license"),
        "mod_icon" to project.property("mod_icon"),
        "environment" to project.property("environment"),
        "supported_minecraft_versions" to project.property("supported_minecraft_versions")
    ))
}


tasks {
    base.archivesName.set("${project.property("archives_base_name")}-fabric")
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        exclude("generations/gg/generations/core/generationscore/fabric/datagen/**")
        exclude("data/forge/**")
        configurations = listOf(project.configurations.getByName("shadowCommon"))
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
    }

    jar.get().archiveClassifier.set("dev")
}

// Modrinth publishing configuration
modrinth {
    token.set(System.getenv("MODRINTH_TOKEN") ?: "")
    projectId.set(System.getenv("MODRINTH_PROJECT_ID") ?: "")

    // Version configuration
    versionNumber.set("${project.version}")
    versionName.set("[Fabric ${project.property("minecraft_version")}] ${project.version}")

    // Release channel from environment or default to release
    versionType.set(System.getenv("RELEASE_CHANNEL") ?: "release")

    // Upload the remapped jar (production artifact)
    uploadFile.set(tasks.remapJar.get())

    // Game versions - supports multiple versions if needed
    gameVersions.add("${project.property("minecraft_version")}")

    // Loaders
    loaders.add("fabric")
    loaders.add("quilt") // Quilt is compatible with Fabric mods

    // Dependencies (use Modrinth project slugs)
    // Note: FTB mods are CurseForge-exclusive, not on Modrinth
    dependencies {
        // Required dependencies
        required.project("cobblemon")
        // FTB Quests is required but not on Modrinth - users must install separately
    }

    // Changelog from file (GitHub Actions will set this)
    changelog.set(project.findProperty("modrinth.changelog")?.toString() ?: "")

    // Sync with CurseForge if needed in the future
    syncBodyFrom.set(rootProject.file("README.md").readText())
}
