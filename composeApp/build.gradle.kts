import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    
    alias(libs.plugins.jetbrainsCompose)
}

val ktor_version = "2.3.5"

kotlin {
    jvm("desktop")
    js(){
        browser()
        binaries.executable()
    }
//    sourceSets.all {
//        languageSettings {
//            languageVersion = "2.0"
//        }
//    }
    sourceSets {
        val desktopMain by getting
        val jsMain by getting

        jsMain.dependencies{
            implementation("io.ktor:ktor-client-js:$ktor_version")
            implementation(npm("crypto-browserify", "3.12"))
            implementation(npm("stream-browserify", "3.0"))
            implementation(npm("buffer", "6.0"))

            implementation(compose.html.core)

            implementation("com.varabyte.kobweb:silk-widgets:0.15.0")
        }
        
        desktopMain.dependencies {
            implementation("io.ktor:ktor-client-cio-jvm:$ktor_version")

            implementation(compose.foundation)
            implementation(compose.material)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(compose.desktop.currentOs)

        }
        commonMain.dependencies {
            implementation("com.ionspin.kotlin:multiplatform-crypto-libsodium-bindings:0.8.9")
            implementation("io.ktor:ktor-client-core:$ktor_version")

            implementation(compose.runtime)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            implementation("me.rahimklaber:stellar_kt:0.0.3")

        }
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "me.rahimklaber.stellar_kt_example"
            packageVersion = "1.0.0"
        }
    }
}
