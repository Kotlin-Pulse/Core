package me.pulse.dependency.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.type.TypeReference
import java.io.File

data class Dependency(
    val group: String,
    val artifact: String,
    val version: String,
) {

    @JsonIgnore
    val name: String = "$artifact-$version"

    @JsonIgnore
    val coordinates: String = "$group:$artifact:$version"

    @JsonIgnore
    val path: String = "${group.replace(".", "/")}/$artifact/$version/$name"

     fun getFile(cacheDirectory: File, extension: String): File = File(cacheDirectory, "${path}.${extension}")

    override fun toString() = coordinates

    companion object {

        internal val listReference by lazy { object : TypeReference<List<Dependency>>() {} }

    }

}