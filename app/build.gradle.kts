plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.alnoer.drivenext"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "dev.alnoer.drivenext"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        val googleWebClientId = providers.gradleProperty("googleWebClientId").orElse("").get()
        resValue("string", "google_web_client_id", googleWebClientId)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        resValues = true
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
}

dependencies {
    implementation(libs.decompose)
    implementation(libs.decompose.compose)
    implementation(libs.decompose.jetpack)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services)
    implementation(libs.googleid)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
