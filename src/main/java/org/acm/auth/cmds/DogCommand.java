package org.acm.auth.cmds;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.utils.HttpUtil;
import org.json.JSONObject;

import java.io.IOException;

public class DogCommand extends Command {
    public static final String URL = "https://dog.ceo/api/breeds/image/random";

    public DogCommand() {
        super("dog", "Returns a dog picture", false);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        // This command might take a while to execute, show as typing to indicate progress
        event.getChannel().sendTyping().queue(success -> {
            try {
                JSONObject json = new JSONObject(HttpUtil.getAsString(URL));
                String url = json.getString("message");
                byte[] image = HttpUtil.getAsBytes(url);
                event.getChannel().sendFile(image, "dog.jpg").queue();
            } catch (IOException e) {
                event.getChannel().sendMessage("Something went wrong! Try again.").queue();
            }
        });
    }
}
