plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.projektapp"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.projektapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_URL", "\"http://13.95.23.193:3001/\"")
        }
        release {
            buildConfigField("String", "API_URL", "\"http://13.95.23.193:3001/\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

// KO IMAM CAS DODAT SE TO SECRETS
// Dokumentacija: https://developers.google.com/maps/documentation/android-sdk/start#api-key
secrets {
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(project(":app:lib"))

    // F R A G M E N T S
    implementation(libs.androidx.navigation.fragment.ktx.v273)
    implementation(libs.androidx.navigation.ui.ktx.v273)

    // R E T R O F I T
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    // C O R O U T I N E
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.kotlinx.coroutines.core)

    // T I M B E R
    implementation(libs.timber)

    // G S O N
    implementation(libs.gson)

    // S W I P E   T O   R E F R E S H
    implementation(libs.androidx.swiperefreshlayout)

    // G L I D E
    implementation (libs.glide)
    annotationProcessor (libs.compiler)

    // S H I M M E R
    implementation (libs.shimmer)

    // R E T R O F I T
    implementation(libs.retrofit)

    // S C A L A R S   C O N V E R T E R
    implementation(libs.converter.scalars)

    // O K H T T P -> temporary, menjamo za M Q T T
    implementation(libs.okhttp)

    // L O G G I N G   I N T E R C E P T O R
    implementation(libs.okhttp3.logging.interceptor)

    // B O U N C Y   C A S T L E
    implementation (libs.bcprov.jdk15to18)

    implementation (libs.org.eclipse.paho.client.mqttv3)

    // D O T S   I N D I C A T O R
    implementation(libs.dotsindicator)

    // C A M E R A
    implementation(libs.androidx.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
}