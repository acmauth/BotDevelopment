package org.acm.auth.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.HttpUrl;
import org.acm.auth.utils.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class GifCommand extends Command {
    private final String key; // the key used to query the GIPHY API

    private static final Permission[] NO_PERMS = {};

    public GifCommand(String key) {
        super(
                // name:
                "gif",
                // description:
                "Returns a GIF using the GIPHY API.",
                // guildOnly:
                false,
                // alias:
                new String[]{ "giphy" },
                // minArgs:
                0,
                // maxArgs:
                Integer.MAX_VALUE,
                // usage:
                "(gif name)",
                // botPerms:
                NO_PERMS,
                // usrPerms:
                NO_PERMS
        );

        this.key = key;
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
                                                            // build the URL endpoint we need
        HttpUrl url = buildUrl(
                args.length > 0                             // if the user supplied some keywords
                        ? String.join(" ", args)    // then combine them with a space between each one
                        : null                              // otherwise default to null, which means random gif
        );

        String response = HttpUtil.get(url);        // perform a GET request
        if (response == null) {                     // if the server failed to reply
            showErrorMessage(event.getChannel());   // show a generic error message
            return;                                 // and stop the command's execution
        }

        JSONObject json = new JSONObject(response);
        JSONObject meta = json.getJSONObject("meta");
        int statusCode = meta.getInt("status");
        if (statusCode == 429) {
            // we got rate limited! no more GIFs for today
            event.getChannel().sendMessage("I've reached my limit for today :(").queue();
            return;
        } else if (statusCode != 200) {
            // something went wrong unexpectedly!
            // we need to check if we did something wrong and fix it by reviewing the logs,
            // or if it was simply an error on the API's side which we can ignore
            System.err.printf("statusCode != 200: %s (%d)%n", meta.getString("msg"), statusCode);
            showErrorMessage(event.getChannel());
            return;
        }

        String embedUrl;
        if (json.get("data") instanceof JSONArray) {
            // when searching based on a keyword, the API returns an array of responses
            JSONArray data = json.getJSONArray("data");
            if (data.length() == 0) {
                // if the array is empty, it means that the query had no results
                event.getChannel().sendMessage("There are no results for your query!").queue();
                return;
            }
            // otherwise, get the first result and retrieve its embed URL
            embedUrl = data.getJSONObject(0).getString("embed_url");
        } else if (json.get("data") instanceof JSONObject) {
            // when polling for a random GIF, then we get a single result
            // which we can retrieve the embed URL from straight away
            embedUrl = json.getJSONObject("data").getString("embed_url");
        } else {
            // if the data object is neither a JSONArray nor a JSONObject,
            // then something must've gone wrong since it's an unexpected type.
            System.err.println("Expected JSONArray/JSONObject, found: " + json.get("data"));
            showErrorMessage(event.getChannel());
            return;
        }

        // if all went well, we can reply with the embed URL
        event.getChannel().sendMessage(embedUrl).queue();
    }

    private HttpUrl buildUrl(String query) {
        // build a new HttpUrl according to the following resource:
        // https://square.github.io/okhttp/4.x/okhttp/okhttp3/-http-url/
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme("https")
                .host("api.giphy.com")
                .addPathSegment("v1")
                .addPathSegment("gifs")
                .addPathSegment(
                        query == null       // if the user supplied no arguments
                                ? "random"  // then search for a random GIF
                                : "search"  // otherwise perform a query search
                )
                .addQueryParameter("api_key", key);

        if (query != null) {
            builder // if we have a query string
                    // pass it to the URL's query parameters
                    .addQueryParameter("q", query)
                    // and limit the results to 1 (we don't need more)
                    .addQueryParameter("limit", "1");
        }

        return builder.build();
    }

    private void showErrorMessage(MessageChannel channel) {
        channel.sendMessage("Something went wrong, try again later!").queue();
    }
}
