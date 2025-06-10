plugins {
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.9.22"
}

group = "com.mumulbo"
version = "0.0.1-SNAPSHOT"

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	gradlePluginPortal()
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation(kotlin("stdlib-jdk8"))
	runtimeOnly("com.mysql:mysql-connector-j")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// ✅ WebSocket
	implementation("org.springframework.boot:spring-boot-starter-websocket")

	// ✅ Redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	// ✅ MongoDB
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	// ✅ JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	// ✅ Jackson Kotlin module (선택적으로 JSON 직렬화 용이)
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// ✅ openfeign
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.1")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootJar {
	archiveFileName.set("ms-msa-playground.jar")
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		jvmTarget = "17"
	}
}
kotlin {
	jvmToolchain(17)
}