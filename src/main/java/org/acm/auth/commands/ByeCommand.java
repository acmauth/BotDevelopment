package org.acm.auth.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ByeCommand extends Command {
    public ByeCommand() {
        super("bye", "Farewells the user", false, new String[] { "goodbye", "farewell" });
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        String userMention = event.getAuthor().getAsMention();
        event.getChannel().sendMessage("Goodbye " + userMention).submit();
    }
}
