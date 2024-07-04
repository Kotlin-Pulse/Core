package me.pulse.launcher.dependency;

import me.pulse.launcher.util.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class DependencyManager {

    private final FileManager fileManager;
    private final DependencyResolver dependencyResolver;

    public DependencyManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.dependencyResolver = new DependencyResolver(fileManager.getDependenciesCache());
    }

    public List<URL> resolvePrimaryDependencies() {
        try (var jarFile = new JarFile((fileManager.getDependencyLoader()))) {
            var entry = jarFile.getJarEntry("dependencies.txt");
            if (entry == null) throw new RuntimeException("Couldn't find dependencies.txt in dependency-loader.jar");

            var stream = jarFile.getInputStream(entry);
            System.out.println("Loading dependencies...");

            var required = new ArrayList<Dependency>();
            try (var reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank() || line.startsWith("#")) continue;
                    required.add(Dependency.fromCoordinates(line));
                }
            }

            var files = dependencyResolver.resolve(required);
            var urls = new ArrayList<URL>();
            for (var file : files) {
                urls.add(file.toURI().toURL());
            }

            return urls;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read dependency-loader.jar", e);
        }
    }

    public List<URL> resolveCoreDependencies(ClassLoader dependencyClassLoader) {
        String loaderClassName;
        String loaderMethodName;

        try (var file = new JarFile(fileManager.getDependencyLoader())) {
            var manifest = file.getManifest();

            loaderClassName = manifest.getMainAttributes().getValue("Loader-Class");
            loaderMethodName = manifest.getMainAttributes().getValue("Loader-Method");
        } catch (Exception e) {
            throw new IllegalStateException("Couldn't read dependency-loader.jar's loader information", e);
        }

        Class<?> loaderClass;
        Object loaderInstance;

        try {
            loaderClass = dependencyClassLoader.loadClass(loaderClassName);

            var constructor = loaderClass.getDeclaredConstructor(File.class, File.class);
            constructor.setAccessible(true);
            loaderInstance = constructor.newInstance(fileManager.getDependenciesCache(), fileManager.getCore());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new IllegalStateException("Couldn't find or load dependency-loader class", e);
        }

        List<File> dependencies;
        try {
            var method = loaderInstance.getClass().getDeclaredMethod(loaderMethodName);
            method.setAccessible(true);

            // 1. The method should return a List<File>
            // 2. If it doesn't, we cache the ClassCastException
            //noinspection unchecked
            dependencies = (List<File>) method.invoke(loaderInstance);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassCastException e) {
            throw new IllegalStateException("Couldn't find or invoke dependency-loader method", e);
        }

        var urls = new ArrayList<URL>();
        for (var file : dependencies) {
            try {
                urls.add(file.toURI().toURL());
            } catch (IOException e) {
                throw new IllegalStateException("Couldn't convert dependency file to URL", e);
            }
        }

        return urls;
    }

}
