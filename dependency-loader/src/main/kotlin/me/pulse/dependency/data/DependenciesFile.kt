package me.pulse.dependency.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference

data class DependenciesFile(
    @JsonProperty("dependencies") val dependencies: List<Dependency>,
    @JsonProperty("repositories") val repositories: List<Repository>?,
) {

    companion object {

        internal val reference by lazy { object : TypeReference<DependenciesFile>() {} }

    }

}