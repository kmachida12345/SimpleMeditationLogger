plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.play.publisher)
    alias(libs.plugins.oss.licenses)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.github.kmachida12345.simplemeditationlogger"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.github.kmachida12345.simplemeditationlogger"
        minSdk = 34
        targetSdk = 36
        versionCode = (project.property("app.versionCode") as String).toInt()
        versionName = project.property("app.versionName") as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    // CI では環境変数、ローカルでは ~/.gradle/gradle.properties から取得。未設定なら署名なし
    fun signingValue(name: String): String? =
        System.getenv(name) ?: providers.gradleProperty(name).orNull

    val releaseStoreFile = signingValue("KEYSTORE_FILE")?.let { file(it) }
    signingConfigs {
        if (releaseStoreFile != null) {
            create("release") {
                storeFile = releaseStoreFile
                storePassword = signingValue("KEYSTORE_PASSWORD")
                keyAlias = signingValue("KEY_ALIAS")
                keyPassword = signingValue("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfigs.findByName("release")?.let { signingConfig = it }
            isMinifyEnabled = true
            isShrinkResources = true
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
        buildConfig = true
    }
}

play {
    // CI の PLAY_SERVICE_ACCOUNT_JSON_PATH で指定した JSON キーで internal トラックへ配信
    System.getenv("PLAY_SERVICE_ACCOUNT_JSON_PATH")?.let {
        serviceAccountCredentials.set(file(it))
    }
    track.set("internal")
    defaultToAppBundles.set(true)
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
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material.icons.extended)
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    
    // Navigation
    implementation(libs.navigation.compose)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // OSS Licenses
    implementation(libs.play.services.oss.licenses)
    
    // Health Connect
    implementation(libs.androidx.health.connect)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}