import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "xigong"
version = "1.0-SNAPSHOT"

repositories {
//    maven("https://maven.aliyun.com/repository/public")
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.squareup.okio:okio:3.7.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("org.json:json:20210307")
//    implementation("org.jetbrains.androidx.navigation:navigation-common-desktop:2.8.0-alpha02")
    implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha02")

    // Enables FileKit without Compose dependencies
    implementation("io.github.vinceglb:filekit-core:0.6.1")

    // Enables FileKit with Composable utilities
    implementation("io.github.vinceglb:filekit-compose:0.6.1")
}

// 小米应用市场
dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
//    implementation("net.sf.json-lib:json-lib:2.2.3")
    implementation("commons-codec:commons-codec:1.4")
    implementation("org.bouncycastle:bcprov-jdk15on:1.62")
}


compose.desktop {
    application {
        mainClass = "MainKt"
        buildTypes {
            jvmArgs("-DbuildType=debug")
            release {
                proguard.isEnabled = false
                jvmArgs("-DbuildType=release")
            }
        }
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ApkDispatcher"
            packageVersion = "1.0.0"
        }
    }
}
