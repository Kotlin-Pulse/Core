package me.pulse.launcher.dependency;

import me.pulse.launcher.util.WebUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public record Dependency(String group, String artifact, String version) {

    private static final String CENTRAL = "https://repo.maven.apache.org/maven2/";

    public String getName() {
        return artifact + "-" + version;
    }

    public String getPath() {
        return group.replace(".", "/") + "/" + artifact + "/" + version + "/" + getName();
    }

    public File getFile(File cacheDirectory) {
        return new File(cacheDirectory, getPath() + ".jar");
    }

    public String getCoordinates() {
        return group + ":" + artifact + ":" + version;
    }

    public URL getUrl() {
        try {
            return new URI(CENTRAL + getPath() + ".jar").toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalStateException("Failed to create URL", e);
        }
    }

    public boolean exists() {
        return WebUtil.exists(getUrl());
    }

    public void download(File cacheDirectory) throws IOException {
        WebUtil.downloadFile(getUrl(), getFile(cacheDirectory));
    }

    public static Dependency fromCoordinates(String coordinates) {
        var parts = coordinates.split(":");
        return new Dependency(parts[0], parts[1], parts[2]);
    }

}
