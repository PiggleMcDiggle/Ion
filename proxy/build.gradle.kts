plugins {
	kotlin("jvm") version "1.6.10"
	kotlin("kapt") version "1.6.10"
	kotlin("plugin.serialization") version "1.6.10"
	id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
	maven("https://papermc.io/repo/repository/maven-public/")
	maven("https://repo.aikar.co/content/groups/aikar/")
	mavenCentral()
}

dependencies {
	compileOnly("com.velocitypowered:velocity-api:3.1.1")

	kapt("com.velocitypowered:velocity-api:3.1.1")

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
	implementation("co.aikar:acf-velocity:0.5.1-SNAPSHOT")
	implementation("net.dv8tion:JDA:5.0.0-alpha.4")
	implementation("org.litote.kmongo:kmongo:4.4.0")
}

tasks{
	compileJava {
		options.compilerArgs.add("-parameters")
		options.isFork = true
	}

	compileKotlin {
		kotlinOptions.javaParameters = true
	}

	shadowJar {
		archiveFileName.set("../../../build/IonProxy.jar")
	}

	create("ionBuild") {
		dependsOn("shadowJar")
	}
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}