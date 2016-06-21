package com.wingman.launcher;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Response;
import com.wingman.launcher.net.HttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Util {

    public static JsonObject getLatestRelease(String owner, String repo) throws IOException {
        Response response = HttpClient
                .downloadUrlSync("https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest");

        String responseString = response
                .body()
                .string();

        JsonElement rootElement = new JsonParser()
                .parse(responseString);

        return rootElement
                .getAsJsonObject();
    }

    /**
     * Attempts to retrieve a {@link InputStream} representation of a file as seen from the launcher class loader resource path.
     *
     * @param path a path to a file accessible through the launcher class loader
     * @return a {@link InputStream} representation of the file passed
     */
    public static InputStream getFile(String path) {
        return Main.class.getClassLoader().getResourceAsStream(path);
    }

    /**
     * Attempts to retrieve a {@code byte[]} representation of a file as seen from the launcher class loader resource path.
     *
     * @param path a path to a file accessible through the launcher class loader
     * @return a {@code byte[]} representation of a file accessible through the launcher class loader
     * @throws IOException
     */
    public static byte[] getFileAsBytes(String path) throws IOException {
        InputStream inputStream = getFile(path);
        if (inputStream != null) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int read;
            byte[] data = new byte[4096];
            while ((read = inputStream.read(data, 0, data.length)) != -1) {
                output.write(data, 0, read);
            }
            inputStream.close();
            return output.toByteArray();
        }
        return null;
    }
}
