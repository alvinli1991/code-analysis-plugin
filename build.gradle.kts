plugins {
    id("org.jetbrains.intellij") version "1.5.3"
    kotlin("jvm") version "1.6.20"
    java
}

group = "me.alvin"
version = "1.0.1"

java {
//    toolchain {
//        languageVersion.set(JavaLanguageVersion.of(11))
//    }
    setSourceCompatibility(JavaLanguageVersion.of(11))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testCompileOnly("junit:junit:4.13.1")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    testImplementation("org.hamcrest:hamcrest:2.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.2")
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

    testCompileOnly("org.projectlombok:lombok:1.18.22")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.22")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.jgrapht:jgrapht-core:1.5.1")
    implementation("org.jgrapht:jgrapht-ext:1.5.1")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("net.steppschuh.markdowngenerator:markdowngenerator:1.3.1.1")
    implementation("org.reflections:reflections:0.10.2")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2022.1")
    plugins.add("java")
    plugins.add("org.jetbrains.kotlin")
}


tasks {
    patchPluginXml {
        changeNotes.set(
            """
            <br/>2022.01.07
            <br/> - add extract class field info and render to markdown table
            <br/>2022.04.27
            <br/> - transfer dag to plantuml state text 
            <br/> ...               """.trimIndent()
        )
        sinceBuild.set("212")
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}
tasks.named<Test>("test") {
    useJUnitPlatform()
    systemProperty("idea.home.path", "/Users/alvinli/Work/github/intellij-community")
}

tasks {
    runPluginVerifier {
        ideVersions.addAll(listOf("2020.1", "2021.2.3", "2021.3.1", "IC-213.*"))
    }
}