package me.pulse.launcher;

import me.pulse.launcher.dependency.DependencyManager;
import me.pulse.launcher.util.FileManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class Launcher {

    private final String[] args;

    public static void main(String[] args) {
        var launcher = new Launcher(args);
        launcher.launch();
    }

    public Launcher(String[] args) {
        this.args = args;
    }

    public void launch() {
        FileManager fileManager = new FileManager();
        DependencyManager dependencyManager = new DependencyManager(fileManager);
        CoreManager coreManager = new CoreManager(fileManager, args);

        ClassLoader dependencyClassLoader = createDependencyClassLoader(fileManager, dependencyManager);
        ClassLoader coreClassLoader = createCoreClassLoader(fileManager, dependencyManager, dependencyClassLoader);

        Thread.currentThread().setContextClassLoader(coreClassLoader);

        coreManager.start(coreClassLoader);
    }

    private ClassLoader createDependencyClassLoader(FileManager fileManager, DependencyManager dependencyManager) {
        List<URL> primaryDependencies = dependencyManager.resolvePrimaryDependencies();
        try {
            primaryDependencies.add(fileManager.getDependencyLoader().toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Couldn't convert dependency-loader file to URL", e);
        }

        return new URLClassLoader(primaryDependencies.toArray(URL[]::new));
    }

    private ClassLoader createCoreClassLoader(FileManager fileManager, DependencyManager dependencyManager, ClassLoader dependencyClassLoader) {
        List<URL> coreDependencies = dependencyManager.resolveCoreDependencies(dependencyClassLoader);
        try {
            coreDependencies.add(fileManager.getCore().toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Couldn't convert core file to URL", e);
        }

        return new URLClassLoader(coreDependencies.toArray(URL[]::new), dependencyClassLoader);
    }

}
