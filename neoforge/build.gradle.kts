plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.modrinth.minotaur")
}

architectury {
    platformSetupLoomIde()
    neoForge {
        platformPackage = "forge"
    }
}

configurations {
    create("common")
    create("shadowCommon")
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
}

loom {
    enableTransitiveAccessWideners.set(true)
    silentMojangMappingsLicense()
}

repositories {
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://maven.neoforged.net")
}

val shadowBundle = configurations.create("shadowBundle") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    neoForge("net.neoforged:neoforge:${property("neoforge_version")}")
    modImplementation("com.cobblemon:neoforge:${property("cobblemon_version")}") { isTransitive = false }
    forgeRuntimeLibrary("thedarkcolour:kotlinforforge-neoforge:${property("kotlin_for_forge_version")}") {
        exclude("net.neoforged.fancymodloader", "loader")
    }
    implementation(project(":common", configuration = "namedElements"))
    "developmentNeoForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    shadowBundle(project(":common", configuration = "transformProductionNeoForge"))

    modApi("dev.architectury:architectury-neoforge:${property("architectury_version")}")


    modImplementation("dev.ftb.mods:ftb-quests-neoforge:${property("ftb_quests_version")}") { isTransitive = false }
    modImplementation("dev.ftb.mods:ftb-teams-neoforge:${property("ftb_teams_version")}") { isTransitive = false }
    modImplementation("dev.ftb.mods:ftb-library-neoforge:${property("ftb_lib_version")}") { isTransitive = false }
}

tasks.processResources {
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(
            mapOf(
                "mod_name" to project.property("mod_name"),
                "mod_id" to project.property("mod_id"),
                "version" to project.property("mod_version"),
                "mod_description" to project.property("mod_description"),
                "repository" to project.property("repository"),
                "license" to project.property("license"),
                "mod_icon" to project.property("mod_icon"),
                "environment" to project.property("environment"),
                "supported_minecraft_versions" to project.property("supported_minecraft_versions")
            )
        )
    }
}

tasks {
    jar {
        archiveBaseName.set("${rootProject.property("archives_base_name")}-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        exclude("fabric.mod.json")
        archiveClassifier.set("dev-shadow")
        archiveBaseName.set("${rootProject.property("archives_base_name")}-${project.name}")
        configurations = listOf(shadowBundle)
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        archiveBaseName.set("${rootProject.property("archives_base_name")}-${project.name}")
        archiveVersion.set("${rootProject.version}")
    }
}

// Modrinth publishing configuration
modrinth {
    token.set(System.getenv("MODRINTH_TOKEN") ?: "")
    projectId.set(System.getenv("MODRINTH_PROJECT_ID") ?: "")

    // Version configuration
    versionNumber.set("${project.version}")
    versionName.set("[NeoForge ${project.property("minecraft_version")}] ${project.version}")

    // Release channel from environment or default to release
    versionType.set(System.getenv("RELEASE_CHANNEL") ?: "release")

    // Upload the remapped jar (production artifact)
    uploadFile.set(tasks.remapJar.get())

    // Game versions - supports multiple versions if needed
    gameVersions.add("${project.property("minecraft_version")}")

    // Loaders
    loaders.add("neoforge")

    // Dependencies
    dependencies {
        // Required dependencies
        required.project("cobblemon")
        required.project("ftb-quests-neoforge")

        // Optional dependencies for better integration
        optional.project("ftb-teams-neoforge")
        optional.project("ftb-library-neoforge")
    }

    // Changelog from file (GitHub Actions will set this)
    changelog.set(project.findProperty("modrinth.changelog")?.toString() ?: "")

    // Sync with CurseForge if needed in the future
    syncBodyFrom.set(rootProject.file("README.md").readText())
}
