package org.acm.auth.cmds;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.utils.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class YtCommand extends Command {
    private final String baseUrl;

    public YtCommand(String apiKey) {
        super("youtube", "Searches YouTube for a video", false);
        this.baseUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&key=" + apiKey;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (args.length == 0) {
            event.getChannel().sendMessage("Please specify a query!").queue();
            return;
        }
        event.getChannel().sendTyping().queue(success -> {
            try {
                String query = String.join(" ", args);
                String url = String.format("%s&q=%s", this.baseUrl, query);
                JSONObject json = new JSONObject(HttpUtil.getAsString(url));
                JSONArray items = json.getJSONArray("items");
                String id = items.getJSONObject(0).getJSONObject("id").getString("videoId");
                event.getChannel().sendMessage("https://youtu.be/" + id).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
