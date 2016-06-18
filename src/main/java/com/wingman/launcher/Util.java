package com.wingman.launcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Util {

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
