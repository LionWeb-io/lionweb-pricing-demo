plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
    id("org.jlleitschuh.gradle.ktlint") version "11.5.1"
    id("com.strumenta.kolasu.lionwebgen")
    id("com.adarshr.test-logger") version "3.2.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

lionweb {
    importPackageNames.set(mutableMapOf("Pricing" to "com.strumenta.pricing"))
}

val kolasuVersion = extra["kolasuVersion"]
val lionwebRelease = extra["lionwebRelease"]
val lionwebJavaVersion = extra["lionwebJavaVersion"]

dependencies {
    implementation("com.github.ajalt.clikt:clikt:4.2.0")
    testImplementation("com.strumenta.kolasu:kolasu-lionweb-gen:$kolasuVersion")
    testImplementation("io.lionweb.lioncore-java:lioncore-java-$lionwebRelease-core:$lionwebJavaVersion")
    testImplementation(kotlin("test"))
}

// see https://discuss.gradle.org/t/implicit-dependency-among-tasks-but-the-tasks-do-not-exist/46127
tasks.configureEach {
    if (name == "kspKotlin") {
        mustRunAfter("generateASTClasses")
    }
}

tasks.named("compileKotlin") {
    dependsOn("generateASTClasses")
}

tasks.named("runKtlintCheckOverMainSourceSet") {
    dependsOn("generateASTClasses")
}

tasks.named("runKtlintFormatOverMainSourceSet") {
    dependsOn("generateASTClasses")
}

ktlint.filter {
    exclude { it.file.path.contains("${project.buildDir.absolutePath}/") }
}
