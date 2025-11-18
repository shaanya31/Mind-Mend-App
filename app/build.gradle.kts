plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.mindmendapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mindmendapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    // If your version catalog supplies the compiler extension version, you can omit this.
    // Otherwise uncomment and set the correct version:
    // composeOptions {
    //     kotlinCompilerExtensionVersion = "1.5.3"
    // }
}

dependencies {
    // Core & lifecycle (from version catalog)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM (keeps Compose libs versions consistent)
    implementation(platform(libs.androidx.compose.bom))

    // UI libraries (from version catalog)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug tooling
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // --- Additional libraries required for the app (only once) ---
    // DataStore (for saving mood entries locally)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Gson (to store entries as JSON)
    implementation("com.google.code.gson:gson:2.10.1")

    // Lottie Animations (compose)
    implementation("com.airbnb.android:lottie-compose:6.1.0")
    // inside dependencies { ... } in app/build.gradle.kts
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

}

