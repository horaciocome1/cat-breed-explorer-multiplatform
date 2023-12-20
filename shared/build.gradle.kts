plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization").version("1.9.20")
    id("kotlin-parcelize")
    id("app.cash.sqldelight").version("2.0.0")
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val coroutinesVersion = "1.7.3"
        val ktorVersion = "2.3.6"
        val serializationJsonVersion = "1.6.0"
        val kamelVersion = "0.9.0"
        val decomposeVersion = "2.1.4"
        val sqlDelightVersion = "2.0.0"
        val startupRuntime = "1.1.1"
        val dateTimeVersion = "0.4.1"
        val timberVersion = "5.0.1"
        val collectionsImmutableVersion = "0.3.6"

        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.animation)
                implementation(compose.animationGraphics)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationJsonVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("media.kamel:kamel-image:$kamelVersion")
                api("com.arkivanov.decompose:decompose:$decomposeVersion")
                api("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion-compose-experimental")
                implementation("app.cash.sqldelight:coroutines-extensions:$sqlDelightVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$dateTimeVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:$collectionsImmutableVersion")
            }
        }

        androidMain {
            dependencies {
                implementation(compose.preview)
                api("androidx.activity:activity-compose:1.8.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.12.0")
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                api("com.arkivanov.decompose:extensions-compose-jetpack:$decomposeVersion")
                implementation("app.cash.sqldelight:android-driver:$sqlDelightVersion")
                implementation("androidx.startup:startup-runtime:$startupRuntime")
                api("com.jakewharton.timber:timber:$timberVersion")
            }
        }

        nativeMain {
            dependencies {
                implementation("app.cash.sqldelight:native-driver:$sqlDelightVersion")
            }
        }

        iosMain {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
                implementation("com.arkivanov.decompose:decompose:$decomposeVersion-compose-experimental")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "io.github.horaciocome1.breedy.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName = "io.github.horaciocome.breedy.data.db"
        }
    }
}
