plugins {
    `kotlin-dsl`
}
repositories {
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation("com.google.code.gson:gson:2.8.6")
}
