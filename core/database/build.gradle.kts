@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.android.junit5)
}

android {
    namespace = "hu.levente.fazekas.database"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

sqldelight{
    databases{
        create("ReceiptDatabase"){
            packageName.set("hu.levente.fazekas.database")
        }
    }
}

dependencies {
    implementation(project(":core:currency"))
    //SqlDelight
    implementation(libs.sqlDelight.primitive.adapters)
    implementation(libs.sqlDelight.paging3.extensions)
    implementation(libs.sqlDelight.coroutines.extensions)
    //KotlinxDateTime
    implementation(libs.kotlinx.datetime)

    
    //JUnit 5
    testImplementation(libs.junit5.jupiter.api)
    testRuntimeOnly(libs.junit5.jupiter.engine)
    testImplementation(libs.junit5.jupiter.params)

    //AssertK
    testImplementation(libs.assertK)

    //JVM SqlDelight driver for testing
    testImplementation(libs.sqlDelight.sqlite.driver)

    //Coroutine test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}