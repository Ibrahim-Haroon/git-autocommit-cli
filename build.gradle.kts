
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.ktlint)
    application
}

application {
    mainClass.set("com.ibrahimharoon.gitautocommit.MainKt")
}

group = "com.ibrahimharoon.gitautocommit"
version = "1.0.0"  // This will be updated by the CI workflow

java {
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    implementation(libs.springWeb)
    implementation(libs.jacksonModuleKotlin)
    implementation(libs.kotlinReflect)
    implementation(libs.kotlinCLI)
    implementation(libs.cliKt)
    testImplementation(libs.mordant)
    implementation(libs.dotEnv)
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "com.ibrahimharoon.gitautocommit.MainKt"
    }
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get())
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register("stage") {
    dependsOn("shadowJar")
}

configurations {
    all {
        exclude(group = "commons-logging", module = "commons-logging")
    }
}
kotlin {
    jvmToolchain(11)
}
