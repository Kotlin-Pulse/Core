package me.pulse.dependency.data

import com.fasterxml.jackson.core.type.TypeReference

data class DependenciesFile(
    val dependency: List<Dependency>,
    val repositories: List<Repository> = emptyList(),
) {

    companion object {

        internal val reference by lazy { object : TypeReference<DependenciesFile>() {} }

    }

}