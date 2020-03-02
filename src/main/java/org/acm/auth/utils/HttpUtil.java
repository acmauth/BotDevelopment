package org.acm.auth.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class HttpUtil {
    private static final Logger LOG = LogManager.getLogger();
    private static OkHttpClient client = new OkHttpClient();

    /**
     * Builds the {@link Request} object for a GET Request
     * @param url The URL to GET
     * @return a Request object for the {@link OkHttpClient} to call
     */
    private static Request buildGetRequest(String url) {
        LOG.debug("GET request to {}", url);
        return new Request.Builder().url(url).build();
    }

    /**
     * Gets a resource from the URL specified and returns its body as a String
     * @param url The URL to GET
     * @return A String representation of the HTTP response's body
     * @throws IOException if the connection could not be established
     */
    public static String getAsString(String url) throws IOException {
        Request request = buildGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException();
            }
            return body.string();
        }
    }

    /**
     * Gets a resource from the URL specified and returns its body as a byte array
     * @param url The URL to GET
     * @return A byte[] representation of the HTTP response's body
     * @throws IOException if the connection could not be established
     */
    public static byte[] getAsBytes(String url) throws IOException {
        Request request = buildGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException();
            }
            return body.bytes();
        }
    }
}
