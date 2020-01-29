// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath(BuildTools.androidGradle)
        classpath(kotlin("gradle-plugin", version = Versions.kotlinGradle))
        classpath(BuildTools.navigationSafeArgs)
        classpath(BuildTools.detektGradle)
        classpath(BuildTools.androidOssPlugin)
        classpath("com.android.tools.build:gradle:4.0.0-alpha09")
        classpath("com.google.gms:google-services:4.3.3")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }

    apply(plugin = BuildTools.detekt)
}

plugins {
    id(BuildTools.detekt).version(Versions.detekt)
}

detekt {
    debug = true
    parallel = true
    config = files("$projectDir/default-detekt-config.yml")
    input = files("$projectDir/src/navGraph/java")
    filters = ".*build.*,.*/resources/.*,.*/tmp/.*"
}

dependencies {
    detektPlugins(BuildTools.detektFormatting)
}


tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}

