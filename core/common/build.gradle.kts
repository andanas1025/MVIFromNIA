plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    // 🚀 THE MAGIC PIECE: Pure JVM Hilt (NOT hilt.android)
    implementation("com.google.dagger:hilt-core:2.55")
    ksp("com.google.dagger:hilt-compiler:2.55")

    // Testing utilities
    testImplementation(libs.kotlinx.coroutines.test)
}