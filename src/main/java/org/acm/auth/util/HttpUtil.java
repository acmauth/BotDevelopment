package org.acm.auth.util;

import okhttp3.*;

import java.io.IOException;

public class HttpUtil {
    private static final OkHttpClient CLIENT = new OkHttpClient();

    public static String get(HttpUrl httpUrl) {
        return get(httpUrl.url().toString());
    }

    public static String get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try(Response response = CLIENT.newCall(request).execute()) {
            ResponseBody body = response.body();
            return body == null ? null : body.string();
        } catch (IOException exception) {
            System.err.println("Error during GET " + url);
            System.err.println(exception.getMessage());
            return null;
        }
    }
}
