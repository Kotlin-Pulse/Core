package me.pulse.launcher.util;

import java.io.File;

public class FileManager {

    private final File dependenciesCache = new File("cache", "dependencies");
    private final File dependencyLoader = new File("storage", "dependency-loader.jar");
    private final File core = new File("storage", "core.jar");

    public FileManager() {
        if (!dependenciesCache.exists() && !dependenciesCache.mkdirs()) {
            throw new IllegalStateException("Couldn't create dependencies directory");
        }

        if (!dependencyLoader.exists()) {
            throw new IllegalStateException("Couldn't find dependency-loader.jar in storage directory");
        }

        if (!core.exists()) {
            throw new IllegalStateException("Couldn't find core.jar in storage directory");
        }
    }

    public File getDependenciesCache() {
        return dependenciesCache;
    }

    public File getDependencyLoader() {
        return dependencyLoader;
    }

    public File getCore() {
        return core;
    }

}
