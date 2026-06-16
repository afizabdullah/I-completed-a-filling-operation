import java.util.Base64
import org.gradle.api.GradleException

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
}

// =====================
// ENV FILE SETUP
// =====================
val envFile = file("${rootDir}/.env")
if (!envFile.exists()) {
  val envExample = file("${rootDir}/.env.example")
  if (envExample.exists()) {
    envExample.copyTo(envFile)
    println("Created .env from .env.example")
  }
}

// =====================
// DEBUG KEYSTORE SETUP (SAFE FOR CI)
// =====================
val debugKeystoreFile = file("${rootDir}/debug.keystore")

if (!debugKeystoreFile.exists()) {
  val base64File = file("${rootDir}/debug.keystore.base64")

  if (base64File.exists()) {
    try {
      val base64Text = base64File.readText().replace("\\s".toRegex(), "")
      val decodedBytes = Base64.getDecoder().decode(base64Text)
      debugKeystoreFile.writeBytes(decodedBytes)
      println("Decoded debug.keystore from base64")
    } catch (e: Exception) {
      println("Failed to decode debug.keystore.base64: ${e.message}")
    }
  }
}

// ❌ IMPORTANT: No keytool / ProcessBuilder هنا نهائيًا
if (!debugKeystoreFile.exists()) {
  println("WARNING: debug.keystore missing. CI must provide it.")
}

// =====================
// ANDROID CONFIG
// =====================
android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.webcompanion.vzkplq"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }

    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      val keystoreFile = file(keystorePath)

      if (!keystoreFile.exists()) {
        storeFile = file("${rootDir}/debug.keystore")
        storePassword = "android"
        keyAlias = "androiddebugkey"
        keyPassword = "android"
        println("Release keystore missing → fallback to debug")
      } else {
        storeFile = keystoreFile
        storePassword = System.getenv("STORE_PASSWORD")
        keyAlias = "upload"
        keyPassword = System.getenv("KEY_PASSWORD")
      }
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      isCrunchPngs = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      signingConfig = signingConfigs.getByName("release")
    }

    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

// =====================
// SECRETS PLUGIN
// =====================
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

// =====================
// DEPENDENCIES
// =====================
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)

  implementation(libs.retrofit)
  implementation(libs.okhttp)
  implementation(libs.converter.moshi)
  implementation(libs.moshi.kotlin)
  implementation(libs.logging.interceptor)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)

  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}