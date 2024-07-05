package me.pulse.launcher;

import me.pulse.launcher.util.FileManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
        String mainMethodName;

        try (var file = new JarFile(fileManager.getCore())) {
            var manifest = file.getManifest();

            mainClassName = manifest.getMainAttributes().getValue("Main-Class");
            mainMethodName = manifest.getMainAttributes().getValue("Main-Method");
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read core.jar's Main-Class", e);
        }

        Class<?> mainClass;
        Object mainInstance;

        try {
            mainClass = coreClassLoader.loadClass(mainClassName);

            var constructor = mainClass.getDeclaredConstructor(File.class, String[].class);
            constructor.setAccessible(true);

            mainInstance = constructor.newInstance(fileManager.getDependenciesCache(), args);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new IllegalStateException("Couldn't find or load main class", e);
        }

        try {
            var mainMethod = mainClass.getMethod(mainMethodName);
            mainMethod.setAccessible(true);
            mainMethod.invoke(mainInstance);
        } catch (Exception e) {
            throw new IllegalStateException("Couldn't start main class", e);
        }
    }

}
