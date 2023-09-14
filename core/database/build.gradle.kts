@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.sqlDelight)
}

android {
    namespace = "hu.levente.fazekas.database"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
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
    //SqlDelight
    implementation(libs.sqlDelight.primitive.adapters)
    implementation(libs.sqlDelight.paging3.extensions)
    implementation(libs.sqlDelight.coroutines.extensions)
    //KotlinxDateTime
    implementation(libs.kotlinx.datetime)

    testImplementation(libs.junit)
    testImplementation(libs.sqlDelight.sqlite.driver)
}