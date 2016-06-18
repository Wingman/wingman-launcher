package com.wingman.launcher;

import com.google.common.base.Throwables;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wingman.launcher.net.HttpClient;
import com.wingman.launcher.settings.Settings;
import com.wingman.launcher.settings.SettingsScreen;
import okio.BufferedSink;
import okio.Okio;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class Main extends LauncherFrame {

    private Settings settings = new Settings();

    private File currentJar = Paths
            .get(System.getProperty("user.home"))
            .resolve("Wingman")
            .resolve("Wingman.jar")
            .toFile();

    private Path pluginPath = Paths
            .get(System.getProperty("user.home"))
            .resolve("Wingman")
            .resolve("plugins");

    private Main() {
        settingsScreen = new SettingsScreen(settings);
        settingsScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                checkUpdate();
            }
        });

        if (Settings.SETTINGS_FILE.exists()) {
            checkUpdate();
        } else {
            settingsScreen.setVisible(true);
        }
    }

    private void checkUpdate() {
        checkUpdate(false);
    }

    private void checkUpdate(final boolean failedOnce) {
        launchButton.setEnabled(false);
        setButtonText("Checking for updates");

        try {
            HttpClient.downloadUrlAsync("https://api.github.com/repos/Wingman/wingman/releases/latest", new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Throwables.propagate(e);
                    retryCheckUpdate(failedOnce);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.code() == 200) {
                        String responseString = response.body().string();
                        JsonElement rootElement = new JsonParser().parse(responseString);
                        String tag = rootElement
                                .getAsJsonObject()
                                .getAsJsonPrimitive("tag_name")
                                .getAsString();

                        if (!currentJar.exists()) {
                            if (!update(tag)) {
                                setButtonText("Failed to download updates");
                                return;
                            }
                        } else {
                            if (!isClientUpToDate(tag)) {
                                if (!update(tag)) {
                                    setButtonText("Failed to download updates");
                                    return;
                                }
                            }
                        }

                        if (settings.getBoolean(Settings.DOWNLOAD_DEFAULT_PLUGINS)) {
                            checkPluginsUpToDate();
                        }
                        setReadyForLaunch();
                    } else {
                        if (response.code() == 403) {
                            if (currentJar.exists()) {
                                setReadyForLaunch();
                            }
                        } else {
                            retryCheckUpdate(failedOnce);
                        }
                    }
                }
            });
        } catch (IOException e) {
            Throwables.propagate(e);
            retryCheckUpdate(failedOnce);
        }
    }

    private void retryCheckUpdate(boolean failedOnce) {
        if (!failedOnce) {
            setButtonText("Failed once - retrying");
            checkUpdate(true);
        } else {
            setButtonText("Failed twice - please restart the launcher");
        }
    }

    private void setReadyForLaunch() {
        setButtonText("Launch");
        launchButton.setEnabled(true);
    }

    private boolean isClientUpToDate(String tag) throws IOException {
        JarFile capsule = new JarFile(currentJar);
        JarEntry jarEntry = capsule.getJarEntry("Wingman-core.jar");
        JarInputStream jarInputStream = new JarInputStream(capsule.getInputStream(jarEntry));

        JarEntry entry;
        while ((entry = jarInputStream.getNextJarEntry()) != null) {
            if (entry.getName().equals("version.properties")) {
                BufferedReader versionBufferedReader = new BufferedReader(new InputStreamReader(jarInputStream));
                String version = versionBufferedReader.readLine();
                if (!version.equals(tag)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean update(String tag) {
        setButtonText("Downloading updates");

        try {
            Response fileResponse
                    = HttpClient.downloadUrlSync("https://github.com/Wingman/wingman/releases/download/" + tag + "/Wingman.jar");
            BufferedSink sink = Okio.buffer(Okio.sink(currentJar));
            sink.writeAll(fileResponse.body().source());
            sink.close();
            return true;
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        return false;
    }

    private void checkPluginsUpToDate() throws IOException {
        Response response = HttpClient.downloadUrlSync("https://api.github.com/repos/Wingman/wingman-defaultplugins/releases/latest");
        String responseString = response.body().string();
        JsonElement rootElement = new JsonParser().parse(responseString);

        String tag = rootElement
                .getAsJsonObject()
                .getAsJsonPrimitive("tag_name")
                .getAsString();

        Path versionFile = pluginPath.resolve("defaultplugins.version");

        boolean isUpToDate = versionFile.toFile().exists()
                && Files.readAllLines(versionFile, Charset.defaultCharset()).get(0).equals(tag);

        JsonArray assets = rootElement
                .getAsJsonObject()
                .getAsJsonArray("assets");

        for (JsonElement asset : assets) {
            String name = asset
                    .getAsJsonObject()
                    .getAsJsonPrimitive("name")
                    .getAsString();

            if (name.endsWith("-default.jar")) {
                if (!isUpToDate || !pluginPath.resolve(name).toFile().exists()) {
                    setButtonText("Downloading " + name);
                    HttpClient.downloadFileSync("https://github.com/Wingman/wingman-defaultplugins/releases/download/" + tag + "/" + name,
                            pluginPath.resolve(name));
                }
            }
        }

        if (!isUpToDate) {
            Files.write(versionFile, tag.getBytes());
        }
    }

    @Override
    public boolean launchClient() {
        setButtonText("Launching the client");

        try {
            Desktop.getDesktop().open(currentJar);
            return true;
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        return false;
    }

    public static void main(String[] args) {
        new Main();
    }
}
