import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.servesphere"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.servesphere"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        val localProps = Properties()
        val localFile = rootProject.file("local.properties")
        if (localFile.exists()) {
            localFile.inputStream().use { localProps.load(it) }
        }
        val geminiApiKey = localProps.getProperty("GEMINI_API_KEY")

        if (!geminiApiKey.isNullOrBlank()) {
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        } else {
            println("⚠️ Warning: GEMINI_API_KEY not found in local.properties.")
            buildConfigField("String", "GEMINI_API_KEY", "\"\"")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources.excludes += setOf(
            "META-INF/DEPENDENCIES",
            "META-INF/LICENSE",
            "META-INF/LICENSE.txt",
            "META-INF/license.txt",
            "META-INF/NOTICE",
            "META-INF/NOTICE.txt",
            "META-INF/notice.txt",
            "META-INF/ASL2.0"
        )
    }
}

dependencies {
    // AndroidX core
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // ✅ Room (local persistence)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.activity)
    implementation(libs.monitor)
    implementation(libs.ext.junit)
    testImplementation(libs.junit.junit)
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // ✅ Lifecycle ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.0")

    // ✅ Firebase + Google Services
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // ✅ Gemini + Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ✅ JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.android.libraries.places:places:3.4.0")

    implementation("com.google.firebase:firebase-firestore:25.1.1")
}
