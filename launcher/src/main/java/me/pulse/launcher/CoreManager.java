package me.pulse.launcher;

import me.pulse.launcher.util.FileManager;

import java.io.IOException;
import java.util.jar.JarFile;

public class CoreManager {

    private final FileManager fileManager;
    private final String[] args;

    public CoreManager(FileManager fileManager, String[] args) {
        this.fileManager = fileManager;
        this.args = args;
    }

    public void start(ClassLoader coreClassLoader) {
        String mainClassName;
        try (var file = new JarFile(fileManager.getCore())) {
            var manifest = file.getManifest();
            mainClassName = manifest.getMainAttributes().getValue("Main-Class");
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read core.jar's Main-Class", e);
        }

        Class<?> mainClass;
        try {
            mainClass = coreClassLoader.loadClass(mainClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Couldn't find main class", e);
        }

        try {
            var mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (Exception e) {
            throw new IllegalStateException("Couldn't start main class", e);
        }
    }

}
