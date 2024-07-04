package me.pulse.launcher.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;

public class WebUtil {

    public static Boolean exists(URL url) {
        try {
            var connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0"
            );
            connection.connect();

            try {
                var responseCode = connection.getResponseCode();
                return responseCode == 200;
            } finally {
                connection.disconnect();
            }
        } catch (IOException | ClassCastException e) {
            return false;
        }
    }

    public static void downloadFile(URL url, File to) throws IOException {
        if (!to.getParentFile().exists() && !to.getParentFile().mkdirs()) {
            throw new IllegalStateException("Could not create parent directories for file " + to);
        }

        try (var out = new FileOutputStream(to)) {
            var connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0"
            );
            connection.connect();

            try (var in = connection.getInputStream()) {
                var buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
    }

}
