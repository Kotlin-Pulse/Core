package me.pulse.launcher.dependency;

public class DependencyNotFoundException extends RuntimeException {

    public final Dependency dependency;

    public DependencyNotFoundException(Dependency dependency) {
        super("Dependency " + dependency.getCoordinates() + " could not be found.");
        this.dependency = dependency;
    }

}
