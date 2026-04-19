
plugins {
    java
    id("io.quarkus") version "3.32.3"
}

repositories {
    mavenCentral()
}

group = "it.unibas.tav.iotsentinel" 
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.32.3"))

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")


    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-hibernate-orm-panache")
    implementation("io.quarkus:quarkus-jdbc-postgresql")

    testImplementation("io.quarkus:quarkus-junit5")
    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
