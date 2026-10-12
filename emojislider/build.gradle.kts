@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

import java.util.concurrent.Callable

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.maven.publish)
}

kotlin {
    android {
        namespace = "com.bernaferrari.emojislider"
        compileSdk {
            version = release(37) { minorApiLevel = 2 }
        }
        minSdk = 24
        androidResources {
            enable = true
        }
        optimization {
            consumerKeepRules.apply {
                publish = true
                file("consumer-rules.pro")
            }
        }
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.foundation)
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.ui)
            api(libs.jetbrains.compose.components.resources)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.bernaferrari.emojislider.generated.resources"
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = "io.github.bernaferrari",
        artifactId = "emojislider",
        version = "1.0.0",
    )

    pom {
        name.set("EmojiSlider")
        description.set("A Compose Multiplatform emoji slider inspired by Instagram")
        inceptionYear.set("2018")
        url.set("https://github.com/bernaferrari/EmojiSlider")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("bernaferrari")
                name.set("Bernardo Ferrari")
                url.set("https://github.com/bernaferrari")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/bernaferrari/EmojiSlider.git")
            developerConnection.set("scm:git:ssh://git@github.com/bernaferrari/EmojiSlider.git")
            url.set("https://github.com/bernaferrari/EmojiSlider")
        }
    }
}

extensions.configure<org.gradle.plugins.signing.SigningExtension>("signing") {
    setRequired(
        Callable {
            gradle.taskGraph.allTasks.any { task ->
                task.path.startsWith(":emojislider:publish") &&
                    task.name != "publishToMavenLocal" &&
                    !task.name.endsWith("ToMavenLocal")
            }
        },
    )
}
