import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

group = "com.globant.mvitest.convention"

// Google NiA uses this specific block to inject build tool classpaths into the plugins module
dependencies {
//    implementation(libs.android.gradlePlugin)
//    implementation(libs.kotlin.gradlePlugin)
//    implementation(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "mymvi.android.hilt"
            implementationClass = "com.globant.mvitest.convention.AndroidHiltConventionPlugin"
        }
    }
}