package me.pulse.dependency.excpetion

import me.pulse.dependency.data.Dependency

class DependencyNotFoundException(dependency: Dependency) : RuntimeException(dependency.coordinates)