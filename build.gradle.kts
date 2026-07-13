plugins {
    base
    id("fabric-loom") version "1.14.10" apply false
}

group = "com.flarek"
version = "1.0.2"

tasks.named("build") {
    dependsOn(":modern:build", "collectVersionedJar")
}

tasks.named("clean") {
    dependsOn(":modern:clean")
}

tasks.register<Copy>("collectVersionedJar") {
    dependsOn(":modern:remapJar")
    from("versions/modern/build/libs") {
        include("SessionTimer*-1.21.8+.jar")
    }
    into(layout.buildDirectory.dir("libs"))
}
