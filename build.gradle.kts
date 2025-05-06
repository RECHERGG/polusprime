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
    implementation(libs.twitch4j)
    implementation(libs.jda)

    implementation("io.github.cdimascio:dotenv-java:3.2.0") // TODO ony for testing purpose
}