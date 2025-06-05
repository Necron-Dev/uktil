plugins {
    kotlin("jvm") version "2.1.20"
}

group = "dev.necron"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
}

kotlin {
    jvmToolchain(8)
}
