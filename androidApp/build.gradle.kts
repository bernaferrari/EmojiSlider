plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
}

android {
    compileSdk = 36

    defaultConfig {
        applicationId = "com.bernaferrari.emojislider"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "com.bernaferrari.emojisliderexample"
    lint {
        abortOnError = false
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(project(":example"))
}
