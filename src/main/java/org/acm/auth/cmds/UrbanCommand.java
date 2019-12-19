package org.acm.auth.cmds;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.utils.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class UrbanCommand extends Command {
    public static final String BASE_URL = "http://api.urbandictionary.com/v0/define?term=";

    public UrbanCommand() {
        super("urban", "Gets a definition from UrbanDictionary", false);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (args.length != 1) {
            event.getChannel().sendMessage("You must specify exactly 1 word to look up!").queue();
            return;
        }
        try {
            JSONObject json = new JSONObject(HttpUtil.getAsString(BASE_URL + args[0]));
            JSONArray list = json.getJSONArray("list");
            if (list.isEmpty()) {
                event.getChannel().sendMessage("There were no results for your query.").queue();
            } else {
                JSONObject result = list.getJSONObject(0);
                String definition = result.getString("definition")
                        .replace("[", "")
                        .replace("]", "");
                event.getChannel().sendMessage(definition).queue();
            }
        } catch (IOException e) {
            event.getChannel().sendMessage("Something went wrong! Try again.").queue();
        }
    }
}
