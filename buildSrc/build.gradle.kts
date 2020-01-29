import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    `kotlin-dsl`
    kotlin("jvm") version "1.3.61"
}

repositories {
    jcenter()

}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}