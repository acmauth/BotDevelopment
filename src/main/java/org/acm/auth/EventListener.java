package org.acm.auth;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            // do not respond to bots
            return;
        }

        String msgContent = event.getMessage().getContentDisplay();

        System.out.println("Responding to " + event.getAuthor().getName());

        if (msgContent.equalsIgnoreCase("-hi")) {
            event.getChannel().sendMessage("Hello there " + event.getAuthor().getAsMention() + "!").queue();
        } else if (msgContent.equalsIgnoreCase("-bye")) {
            event.getChannel().sendMessage("Goodbye!").queue();
        }
    }
}
