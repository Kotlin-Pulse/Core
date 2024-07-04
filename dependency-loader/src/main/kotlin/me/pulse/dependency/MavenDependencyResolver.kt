package me.pulse.dependency

import com.fasterxml.jackson.databind.ObjectMapper
import me.pulse.dependency.aether.Aether
import me.pulse.dependency.data.Dependency
import me.pulse.dependency.data.Repository
import me.pulse.dependency.excpetion.DependencyNotFoundException
import org.eclipse.aether.artifact.DefaultArtifact
import java.io.File

class MavenDependencyResolver(
    private val cacheDirectory: File,
    private val log: (String) -> Unit,
) {

    private val aether = Aether(cacheDirectory)

    fun resolve(repositories: List<Repository>, dependencies: List<Dependency>): List<File> =
        dependencies.map { resolve(repositories, it) }.flatten()

    private fun resolve(repositories: List<Repository>, dependency: Dependency): List<File> =
        getDependencies(repositories, dependency).map { getFile(repositories, it) }

    private fun getDependencies(
        repositories: List<Repository>,
        dependency: Dependency,
        included: MutableList<Dependency> = mutableListOf(),
    ): List<Dependency> {
        if (dependency in included) return emptyList()
        included.add(dependency)

        val directDependencies =
            if (dependency.getFile(cacheDirectory, "info").exists()) {
                mapper.readValue(dependency.getFile(cacheDirectory, "info"), Dependency.listReference)
            } else {
                getDirectDependencies(repositories, dependency).also { directDependencies ->
                    mapper.writeValue(
                        dependency.getFile(cacheDirectory, "info")
                            .also { it.parentFile.mkdirs() }
                            .also { it.createNewFile() },
                        directDependencies
                    )
                }
            }

        return directDependencies
            .map { getDependencies(repositories, it, included) }
            .flatten()
            .let { it + dependency }
    }

    private fun getDirectDependencies(repositories: List<Repository>, dependency: Dependency): List<Dependency> =
        aether.getDependencies(DefaultArtifact(dependency.coordinates), repositories.map { it.remote })
            .map { it.artifact }
            .map { Dependency(it.groupId, it.artifactId, it.version) }

    private fun getFile(repositories: List<Repository>, dependency: Dependency): File {
        val file = dependency.getFile(cacheDirectory, "jar")
        if (file.exists()) return file

        repositories.forEach { repository ->
            if (dependency !in repository) return@forEach

            log("Downloading $dependency")
            return repository[dependency, cacheDirectory]
        }

        throw DependencyNotFoundException(dependency)
    }

    companion object {

        internal val mapper by lazy { ObjectMapper() }

    }

}