plugins {
    id("fabric-loom")
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

base {
    archivesName.set("SessionTimer")
}

tasks.named<AbstractArchiveTask>("remapJar") {
    archiveFileName.set("SessionTimer${project.version}-1.21.8+.jar")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.11")
    mappings("net.fabricmc:yarn:1.21.11+build.6:v2")
    modImplementation("net.fabricmc:fabric-loader:0.19.3")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.141.4+1.21.11")

    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
    main {
        java.srcDir("../../src/main/java")
        resources.srcDir("../../src/main/resources")
    }
    test {
        java.srcDir("../../src/test/java")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.processResources {
    inputs.properties(
        "version" to project.version,
        "entrypoint" to "com.flarek.sessiontimer.modern.SessionTimerModernClient",
        "minecraft_range" to ">=1.21.8"
    )
    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "entrypoint" to "com.flarek.sessiontimer.modern.SessionTimerModernClient",
            "minecraft_range" to ">=1.21.8"
        )
    }
}

tasks.test {
    useJUnitPlatform()
}
