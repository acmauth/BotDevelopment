package org.acm.auth;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageReceivedEventListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        String messageContent = event.getMessage().getContentDisplay();
        if (messageContent.equalsIgnoreCase("-hi")) {
            event.getChannel().sendMessage("Hi " + event.getAuthor().getAsMention() + "!").queue();
        } else if (messageContent.equalsIgnoreCase("-bye")) {
            event.getChannel().sendMessage("Bye " + event.getAuthor().getAsMention() + "!").queue();
        }
    }
}
