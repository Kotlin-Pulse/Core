package me.pulse.launcher.dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DependencyResolver {

    private final File cacheDirectory;

    public DependencyResolver(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    public List<File> resolve(List<Dependency> dependencies) {
        List<File> files = new ArrayList<>();

        for (Dependency dependency : dependencies) {
            var file = resolve(dependency);
            if (file == null) throw new DependencyNotFoundException(dependency);

            files.add(file);
        }

        return files;
    }

    private File resolve(Dependency dependency) {
        if (dependency.getFile(cacheDirectory).exists()) return dependency.getFile(cacheDirectory);

        if (!dependency.exists()) return null;

        System.out.println("Downloading " + dependency.getCoordinates());
        try {
            dependency.download(cacheDirectory);
        } catch (IOException e) {
            return null;
        }

        return dependency.getFile(cacheDirectory);
    }

}
