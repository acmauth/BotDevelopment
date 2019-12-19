package org.acm.auth.cmds;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.utils.HttpUtil;
import org.json.JSONObject;

import java.io.IOException;

public class JokeCommand extends Command {
    public static final String URL = "https://official-joke-api.appspot.com/jokes/programming/random";

    public JokeCommand() {
        super("joke", "Returns a random joke", false);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        try {
            String response = HttpUtil.getAsString(URL);
            response = response.substring(1, response.length() - 1);
            JSONObject json = new JSONObject(response);
            String setup = json.getString("setup");
            String punchline = json.getString("punchline");
            event.getChannel().sendMessage(String.format("- %s%n- %s", setup, punchline)).queue();
        } catch (IOException e) {
            event.getChannel().sendMessage("Something went wrong! Try again.").queue();
        }
    }
}
