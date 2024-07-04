package me.pulse.dependency

import me.pulse.dependency.data.DependenciesFile
import me.pulse.dependency.data.Repository
import java.io.File
import java.util.jar.JarFile

// Created by the launcher via reflection
@Suppress("unused")
class CoreDependencyLoader(
    dependencyCache: File,
    private val core: File,
) {

    private val resolver = MavenDependencyResolver(dependencyCache) { println(it) }

    fun load(): List<File> {
        val dependencies = JarFile(core).use {
            val entry = it.getJarEntry("dependencies.json")
                ?: throw IllegalStateException("Couldn't find dependencies.json in core.jar")

            val stream = it.getInputStream(entry)
            MavenDependencyResolver.mapper.readValue(stream, DependenciesFile.reference)
        }

        val repositories = dependencies.repositories.takeIf { it.isNotEmpty() }
            ?: listOf(Repository("https://repo.maven.apache.org/maven2/"))

        return resolver.resolve(repositories, dependencies.dependency)
    }

}