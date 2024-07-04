dependencies {
    // Launcher
    compileOnlyApi(project(":launcher"))

    // Maven
    implementation("org.apache.maven.resolver:maven-resolver-supplier:1.9.18")

    // Serialization
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
}

tasks {
    jar {
        dependsOn("generateBom")

        from(layout.buildDirectory.dir("generated").get().file("dependency-loader.txt")) {
            rename("dependency-loader.txt", "dependencies.txt")
        }

        manifest {
            attributes["Loader-Class"] = "me.pulse.dependency.CoreDependencyLoader"
            attributes["Loader-Method"] = "load"
        }
    }

    register("generateBom") {
        // get all dependencies
        val dependencies = configurations
            .runtimeClasspath
            .get()
            .resolvedConfiguration
            .resolvedArtifacts
            .map { it.moduleVersion.id }
            .map { "${it.group}:${it.name}:${it.version}" }

        // write dependencies to file
        layout.buildDirectory
            .dir("generated")
            .get()
            .asFile
            .also { it.mkdirs() }
            .resolve("dependency-loader.txt")
            .writeText(dependencies.joinToString("\n"))
    }
}