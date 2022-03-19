package org.acm.auth.commands;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.HttpUrl;
import org.acm.auth.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class GifCommand extends Command {
    private final String key;

    public GifCommand(String key) {
        super(  "gif", // name
                "Returns a GIF using GIPHY API", // description
                false, // guildOnly
                false, // devOnly
                new String[] { "giphy" }, // alias
                0, // minArgs
                Integer.MAX_VALUE, // maxArgs
                "(gif name)", // usage
                EMPTY_PERMS, // botPerms
                EMPTY_PERMS // userPerms
                );
        this.key = key;
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        HttpUrl url = buildUrl(args.length > 0 ? String.join(" ", args) : null);

        String response = HttpUtil.get(url);
        if(response == null) {
            showErrorMessage(event.getChannel());
            return;
        }

        JSONObject json = new JSONObject(response);
        JSONObject meta = json.getJSONObject("meta");
        int statusCode = meta.getInt("status");
        if (statusCode == 429) {
            event.getChannel().sendMessage("I've reached my limit for today :(").queue();
            return;
        } else if (statusCode != 200) {
            System.err.printf("statusCode != 200: %s (%d)%n", meta.getString("msg"), statusCode);
            showErrorMessage(event.getChannel());
            return;
        }

        String embedUrl;
        if (json.get("data") instanceof JSONArray) {
            JSONArray data = json.getJSONArray("data");

            if(data.length() == 0) {
                event.getChannel().sendMessage("There are no results for your query!").queue();
                return;
            }

            embedUrl = data.getJSONObject(0).getString("embed_url");
        } else if(json.get("data") instanceof JSONObject) {
            embedUrl = json.getJSONObject("data").getString("embed_url");
        } else {
            System.err.println("Expected JSONArray/JSONObject, found: " + json.get("data"));
            showErrorMessage(event.getChannel());
            return;
        }

        event.getChannel().sendMessage(embedUrl).queue();
    }

    private void showErrorMessage(MessageChannel channel) {
        channel.sendMessage("Something went wrong, try again later!").queue();
    }

    private HttpUrl buildUrl(String query) {
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme("https")
                .host("api.giphy.com")
                .addPathSegment("v1")
                .addPathSegment("gifs")
                .addPathSegment(query == null ? "random" : "search")
                .addQueryParameter("api_key", key);

        if (query != null) {
            builder.addQueryParameter("q", query)
                    .addQueryParameter("limit", "1");
        }

        return builder.build();
    }
}
