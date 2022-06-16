import java.time.Instant

plugins {
  id(/*net.fabricmc.*/ "fabric-loom") version "0.12.48"
  id("io.github.juuxel.loom-quiltflower") version "1.7.2"
  id("net.nemerosa.versioning") version "3.0.0"
  id("org.gradle.signing")
}

group = "dev.sapphic"
version = "2.3.0"

if ("CI" in System.getenv()) {
  version = "$version-${versioning.info.build}"
}

java {
  withSourcesJar()
}

loom {
  mixin {
    defaultRefmapName.set("mixins/plated/refmap.json")
  }

  runs {
    configureEach {
      vmArgs("-Xmx4G", "-XX:+UseZGC")

      property("mixin.debug", "true")
      property("mixin.debug.export.decompile", "false")
      property("mixin.debug.verbose", "true")
      property("mixin.dumpTargetOnFailure", "true")
      property("mixin.checks", "true")
      property("mixin.hotSwap", "true")
    }
  }
}

repositories {
  maven("https://cursemaven.com") {
    content {
      includeGroup("curse.maven")
    }
  }
}

dependencies {
  minecraft("com.mojang:minecraft:1.19")
  mappings(loom.layered {
    officialMojangMappings {
      nameSyntheticMembers = true
    }
  })

  modImplementation("net.fabricmc:fabric-loader:0.14.7")
  implementation("com.google.code.findbugs:jsr305:3.0.2")
  implementation("org.jetbrains:annotations:23.0.0")
  implementation("org.checkerframework:checker-qual:3.22.1")

  modCompileOnly("curse.maven:charm-318872:3829804") { // 1.19-4.1.2
    isTransitive = false
  }

  // Binary compatible with unpublished 1.19 codebase
  // https://github.com/magistermaks/mod-redbits/tree/ab96a1aa0625e662065789802625423b8ffd7a51
  modCompileOnly("curse.maven:red-bits-403914:3822702") { // 1.13.3
    isTransitive = false
  }
}

tasks {
  compileJava {
    with(options) {
      isDeprecation = true
      encoding = "UTF-8"
      isFork = true
      compilerArgs.addAll(
        listOf(
          "-Xlint:all", "-Xlint:-processing",
          // Enable parameter name class metadata 
          // https://openjdk.java.net/jeps/118
          "-parameters"
        )
      )
      release.set(17)
    }
  }

  processResources {
    filesMatching("/fabric.mod.json") {
      expand("version" to project.version)
    }
  }

  jar {
    from("/LICENSE")

    manifest.attributes(
      "Build-Timestamp" to Instant.now(),
      "Build-Revision" to versioning.info.commit,
      "Build-Jvm" to "${
        System.getProperty("java.version")
      } (${
        System.getProperty("java.vendor")
      } ${
        System.getProperty("java.vm.version")
      })",
      "Built-By" to GradleVersion.current(),
      "Implementation-Title" to project.name,
      "Implementation-Version" to project.version,
      "Implementation-Vendor" to project.group,
      "Specification-Title" to "FabricMod",
      "Specification-Version" to "1.0.0",
      "Specification-Vendor" to project.group,
      "Sealed" to "true"
    )
  }

  if (hasProperty("signing.mods.keyalias")) {
    val alias = property("signing.mods.keyalias")
    val keystore = property("signing.mods.keystore")
    val password = property("signing.mods.password")

    fun Sign.antSignJar(task: Task) =
      task.outputs.files.forEach { file ->
        ant.invokeMethod(
          "signjar", mapOf(
            "jar" to file,
            "alias" to alias,
            "storepass" to password,
            "keystore" to keystore,
            "verbose" to true,
            "preservelastmodified" to true
          )
        )
      }

    val signJar by creating(Sign::class) {
      dependsOn(remapJar)

      doFirst {
        antSignJar(remapJar.get())
      }

      sign(remapJar.get())
    }

    val signSourcesJar by creating(Sign::class) {
      dependsOn(remapSourcesJar)

      doFirst {
        antSignJar(remapSourcesJar.get())
      }

      sign(remapSourcesJar.get())
    }

    assemble {
      dependsOn(signJar, signSourcesJar)
    }
  }
}
