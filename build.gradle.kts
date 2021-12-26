plugins {
    id("org.jetbrains.intellij") version "1.3.0"
    java
}

group = "me.alvin"
version = "1.0-SNAPSHOT"

java {
//    toolchain {
//        languageVersion.set(JavaLanguageVersion.of(11))
//    }
    setSourceCompatibility(JavaLanguageVersion.of(8))
}

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testCompileOnly("junit:junit:4.13")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    // https://mvnrepository.com/artifact/org.hamcrest/hamcrest
    testImplementation("org.hamcrest:hamcrest:2.2")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.5")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.5")
    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

    testCompileOnly("org.projectlombok:lombok:1.18.22")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.22")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.12.0")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-collections4
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.jgrapht:jgrapht-core:1.5.1")
    // https://mvnrepository.com/artifact/org.jgrapht/jgrapht-ext
    implementation("org.jgrapht:jgrapht-ext:1.5.1")
// https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:30.1.1-jre")


}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2021.2.3")
    plugins.add("java")
}


tasks {
    patchPluginXml {
        changeNotes.set(
            """
            Add change notes here.<br>
            <em>most HTML tags may be used</em>        """.trimIndent()
        )
    }
}
tasks.named<Test>("test") {
    useJUnitPlatform()
    systemProperty("idea.home.path","/Users/alvinli/Work/github/intellij-community")
}

tasks {
    runPluginVerifier {
        ideVersions.addAll(listOf("2021.2.3", "2021.3.1"))
    }
}