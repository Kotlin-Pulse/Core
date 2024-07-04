package me.pulse.dependency.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import me.pulse.launcher.util.WebUtil
import org.eclipse.aether.repository.RemoteRepository
import java.io.File
import java.net.URI
import java.net.URL

data class Repository(@JsonProperty("url") val url: String) {

    @JsonIgnore
    internal val pathUrl = if (url.endsWith('/')) url else "$url/"

    internal val remote: RemoteRepository
        get() = RemoteRepository.Builder(null, "default", url).build()

    internal operator fun contains(dependency: Dependency): Boolean = WebUtil.exists(getUrl(dependency))

    internal operator fun get(dependency: Dependency, cacheDirectory: File): File =
        dependency.getFile(cacheDirectory, "jar")
            .also { WebUtil.downloadFile(getUrl(dependency), it) }

    private fun getUrl(dependency: Dependency): URL =
        URI("${pathUrl}${dependency.path}.jar").toURL()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Repository) return false
        return pathUrl == other.pathUrl
    }

    override fun hashCode(): Int {
        return pathUrl.hashCode()
    }

    override fun toString() = url


}