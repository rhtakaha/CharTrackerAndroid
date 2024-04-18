import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.chartracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.chartracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //load the values from .properties file
        val keystoreFile = project.rootProject.file("\\gradle\\keys.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        //return empty key in case something goes wrong
        val bannerKey = properties.getProperty("BANNER_KEY") ?: ""

        buildConfigField(
            type = "String",
            name = "BANNER_KEY",
            value = bannerKey
        )

        manifestPlaceholders["BANNER_KEY"] = bannerKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("com.google.android.material:material:1.11.0")

    //Compose
    implementation ("androidx.compose.runtime:runtime")
    implementation ("androidx.compose.ui:ui")
    implementation ("androidx.compose.foundation:foundation")
    implementation ("androidx.compose.foundation:foundation-layout")
    implementation ("androidx.compose.material:material")
    implementation ("androidx.compose.runtime:runtime-livedata")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation ("androidx.compose.ui:ui-tooling")
    implementation ("androidx.compose.material3:material3")
    implementation ("androidx.compose.material:material-icons-extended")
    implementation ("androidx.compose.animation:animation")
    implementation ("androidx.compose.ui:ui-tooling-preview")
    implementation ("androidx.activity:activity-compose")
    implementation("androidx.navigation:navigation-compose")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.7")

    val lifecycleVersion = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    //Testing
    testImplementation("com.google.truth:truth:1.4.1")
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1-Beta")

    //logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Kotlin Navigation
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    //Firebase
    // "When using the BoM, don't specify versions in Firebase dependencies"
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")

    // FirebaseUI Storage only (for downloading images easily)
    implementation("com.firebaseui:firebase-ui-storage:7.2.0")

    //AdMob
    implementation ("com.google.android.gms:play-services-ads:23.0.0")

    // Credential Manager
    implementation("androidx.credentials:credentials:1.3.0-alpha01")

    //Glide for showing images
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    ksp("com.github.bumptech.glide:ksp:4.14.2")
}