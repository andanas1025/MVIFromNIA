plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}

android {
    namespace = "com.globant.mvitest.core.domain"
    compileSdk = 35

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))

    // Android Hilt (since this is now an Android library module)
    implementation("com.google.dagger:hilt-android:2.55")
    ksp("com.google.dagger:hilt-compiler:2.55")

    implementation(libs.kotlinx.coroutines.core)
}