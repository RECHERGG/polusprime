plugins {
    java
}

group = "de.rechergg"
version = "1.0.0-alpha"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor(libs.lombok)

    implementation(libs.bundles.logging)
    implementation(libs.bundles.utils)
    implementation(libs.jda)
}