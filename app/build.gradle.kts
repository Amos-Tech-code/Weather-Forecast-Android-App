plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // Kotlin Serialization
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.amos_tech_code.weatherforecast"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.amos_tech_code.weatherforecast"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //SplashScreen
    implementation(libs.androidx.core.splashscreen)
    //Lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    //Navigation
    implementation(libs.androidx.navigation.compose)
    //Koin
    implementation(libs.koin.android)
    implementation(libs.koin.android.compose)
    // Kotlinx JSON serialization
    implementation(libs.kotlinx.serialization.json)
    //Kotlin Date Time
    implementation(libs.kotlinx.datetime)
    // Retrofit
    implementation(libs.retrofit)
    // Gson converter for Retrofit
    implementation(libs.converter.gson)
    //Logging
    implementation(libs.logging.interceptor)

}