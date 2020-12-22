package org.acm.auth.utils;

import okhttp3.*;

import java.io.IOException;

public class HttpUtil {
    // our instance of OkHttpClient, which will be used for ALL http requests
    private static final OkHttpClient CLIENT = new OkHttpClient();

    // GET a resource from the specified HttpUrl instance
    public static String get(HttpUrl httpUrl) {
        // convert the HttpUrl to its string form
        return get(httpUrl.url().toString());
    }

    // GET a resource from the specified URL String
    public static String get(String url) {
        // build a new request with the specified URL
        Request request = new Request.Builder()
                .url(url)
                .build();

        // execute a new call with the OkHttpClient
        try (Response response = CLIENT.newCall(request).execute()) {
            ResponseBody body = response.body();            // get the response's body/payload.
            return body == null                             // if it's null (server returned no response?),
                    ? null                                  // then return null to the user as well (no data),
                    : body.string();                        // otherwise convert it to a string and return.
        } catch (IOException e) {                           // if something went wrong while contacting the server,
            System.err.println("Error during GET " + url);  // log the URL which caused the GET request to fail,
            System.err.println(e.getMessage());             // log the error message itself
            return null;                                    // and return null to the user, since we fetched no data.
        }
    }
}
