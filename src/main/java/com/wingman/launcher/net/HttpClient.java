package com.wingman.launcher.net;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import okio.BufferedSink;
import okio.Okio;

import java.io.IOException;
import java.nio.file.Path;

public class HttpClient {

    public static OkHttpClient httpClient = new OkHttpClient();

    private static Request.Builder cachedRequestBuilder;

    /**
     * Performs a synchronous download of the file from the URL specified, to the save path specified.
     *
     * @param url the URL of the file
     * @param savePath the save path of the downloaded file
     * @throws IOException
     */
    public static void downloadFileSync(String url, Path savePath) throws IOException {
        Response response = downloadUrlSync(url);
        BufferedSink sink = Okio.buffer(Okio.sink(savePath.toFile()));
        sink.writeAll(response.body().source());
        sink.close();
    }

    /**
     * Constructs a synchronous request to the URL specified. <br>
     * Returns {@link Response} upon completion.
     *
     * @param url the URL of your request
     * @throws IOException
     */
    public static Response downloadUrlSync(String url) throws IOException {
        Request request = getRealisticRequestBuilder()
                .url(url)
                .build();

        return HttpClient.httpClient
                .newCall(request)
                .execute();
    }

    /**
     * Constructs a {@link com.squareup.okhttp.Request.Builder} with request headers attempting to mimic a real browser. <br>
     *
     * @return a {@link com.squareup.okhttp.Request.Builder} with somewhat realistic headers
     */
    private static Request.Builder getRealisticRequestBuilder() {
        if (cachedRequestBuilder == null) {
            cachedRequestBuilder = new Request.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0")
                    .addHeader("Accept-Language", "en-US,en;q=0.5")
                    .addHeader("DNT", "1")
                    .addHeader("Connection", "keep-alive");
        }
        return cachedRequestBuilder;
    }
}
