pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}

rootProject.name = "SessionTimer"

include("modern")
project(":modern").projectDir = file("versions/modern")
