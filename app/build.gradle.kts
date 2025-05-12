android {
    compileSdk = 34 // Целевая версия SDK
    defaultConfig {
        applicationId = "com.example.figmatocompose"
        minSdk = 24 // Минимальная версия SDK
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true // Включение поддержки Compose
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14" // Версия компилятора Compose
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.bluehits"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bluehits"
        minSdk = 31
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2") // Для интеграции Compose с Activity
    implementation("androidx.compose.ui:ui:1.7.0") // Основные компоненты Compose
    implementation("androidx.compose.material3:material3:1.3.0") // Material 3
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.0") // Для предпросмотра
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.0") // Для отладки Compose
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6") // Для работы с жизненным циклом
    implementation("androidx.compose.runtime:runtime:1.7.0") // Runtime для Compose

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(project(":interpreter"))
    implementation(libs.androidx.room.runtime.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
