package org.acm.auth.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HiCommand extends Command {
    public HiCommand() {
        super("hi", "Greets the user", false, new String[] { "hello", "greet" });
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        String userMention = event.getAuthor().getAsMention();
        event.getChannel().sendMessage("Hi there " + userMention).submit();
    }
}
