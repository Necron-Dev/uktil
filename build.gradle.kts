plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("maven-publish")
}

group = "yqloss"
version = "0.4.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.1")
}

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("uktil") {
            from(components["kotlin"])
            artifact(tasks["kotlinSourcesJar"])

            pom {
                groupId = "net.yqloss"
                name = "uktil"
                description = ""
                url = "https://github.com/Necron-Dev/uktil"

                licenses {
                    license {
                        name = "MIT"
                        url = "https://github.com/Necron-Dev/uktil/blob/master/LICENSE"
                    }
                }

                developers {
                    developer {
                        id = "Yqloss"
                        name = "Yqloss"
                        email = "yqloss@yqloss.net"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/Necron-Dev/uktil.git"
                    developerConnection = "scm:git:ssh://github.com:Necron-Dev/uktil.git"
                    url = "https://github.com/Necron-Dev/uktil"
                }
            }
        }
    }

    repositories {
        val uktilPublishToLocal: String by project
        val uktilPublishToRemote: String by project

        if (uktilPublishToLocal == "true") {
            mavenLocal()
        }

        if (uktilPublishToRemote == "true") {
            maven {
                val uktilPublishingRemoteURL: String by project
                val uktilPublishingRemoteUsername: String? by project
                val uktilPublishingRemotePassword: String? by project

                name = "Remote"
                url = uri(uktilPublishingRemoteURL)

                credentials {
                    username = uktilPublishingRemoteUsername
                    password = uktilPublishingRemotePassword
                }
            }
        }
    }
}
