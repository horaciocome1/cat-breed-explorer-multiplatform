plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization").version("1.9.20")
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "io.github.horaciocome1.breedy"
    ndkVersion = "25.1.8937393"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    // To avoid complication error on duplicate resource when adding kotlinx-datetime
    packagingOptions.resources.excludes.add("META-INF/versions/9/previous-compilation-data.bin")

    defaultConfig {
        applicationId = "io.github.horaciocome1.breedy"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 1
        versionName = "0.0.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
            version = "3.22.1"
        }
    }
}
