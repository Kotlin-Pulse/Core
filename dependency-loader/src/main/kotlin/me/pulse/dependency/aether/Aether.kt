package me.pulse.dependency.aether

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactDescriptorRequest
import org.eclipse.aether.supplier.RepositorySystemSupplier
import java.io.File

internal class Aether(cacheDirectory: File) {

    private val localRepository by lazy { LocalRepository(File(cacheDirectory, "repository")) }
    private val system by lazy { RepositorySystemSupplier().get() }

    fun getDependencies(artifact: Artifact, repositories: List<RemoteRepository>): List<Dependency> {
        val request = ArtifactDescriptorRequest()

        request.artifact = artifact
        request.repositories = repositories

        return system.readArtifactDescriptor(createSession(), request)
            .dependencies
            .filter { !it.isOptional }
            .filter { it.scope == "compile" }
    }

    private fun createSession(): RepositorySystemSession {
        val session = MavenRepositorySystemUtils.newSession()
        session.localRepositoryManager = system.newLocalRepositoryManager(session, localRepository)
        return session
    }


}