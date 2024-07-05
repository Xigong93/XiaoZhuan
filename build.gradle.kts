import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}


val appVersion = AppVersion(1, 0, 0)
println(appVersion)
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
    implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha02")

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
            release {
                proguard.isEnabled = false
            }
        }
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ApkDispatcher"
            packageVersion = appVersion.versionName
        }
    }
}


tasks.named("processResources") {
    doLast {
        val dir = outputs.files.first()
        val file = File(dir, "BuildConfig.json")
        val tasks = gradle.taskGraph.allTasks
        val release = tasks.any { it.name.startsWith("package") }
        writeBuildConfig(file, release)
    }
}


/**
 * 生成BuildConfig配置文件
 */
fun writeBuildConfig(file: File, release: Boolean) {
    val type = if (release) "release" else "debug"
    println("Write $type BuildConfig.json to  ${file.absolutePath}")
    val buildConfig = BuildConfig(
        appVersion.versionCode.toLong(),
        appVersion.versionName,
        release
    )
    file.writeText(buildConfig.toJson())
}