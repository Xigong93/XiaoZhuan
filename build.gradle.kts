import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

val packageId = "com.xigong.xiaozhuan"
val appVersion = AppVersion(1, 1, 0)
val appName = "小篆传包"
println("当前版本:v${appVersion.versionName} (${appVersion.versionCode})")
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
    // Enables FileKit without Compose dependencies
    implementation("io.github.vinceglb:filekit-core:0.6.2")

    // Enables FileKit with Composable utilities
    implementation("io.github.vinceglb:filekit-compose:0.6.2")

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
            targetFormats(TargetFormat.Msi, TargetFormat.Dmg)
            outputBaseDir.set(project.buildDir.resolve("packages"))
            includeAllModules = true
            packageName = appName
            description = "一键上传Apk到多个应用市场，开源，免费"
            copyright = "© 2024 Xigong"
            vendor = "Xigong"
            packageVersion = appVersion.versionName

            windows {
                // 生成桌面快捷方式
                shortcut = true
                // 设置图标
                iconFile.set(project.file("launcher/icon.ico"))

                upgradeUuid = "c5dd9f2e-9e6b-4899-867e-a980924c8962"
                // 自定义安装目录的名称，不设置的话，会使用中文
                installationPath = "./XiaoZhuan"
            }
            macOS {
                iconFile.set(project.file("launcher/icon.icns"))
                installationPath = "./XiaoZhuan"
            }
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
    val name = if (release) appName else "${appName}(测试)"
    val buildConfig = BuildConfig(
        versionCode = appVersion.versionCode.toLong(),
        versionName = appVersion.versionName,
        packageId = packageId,
        appName = name,
        release = release
    )
    file.writeText(buildConfig.toJson())
}

