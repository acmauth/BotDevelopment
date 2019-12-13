package org.acm.auth.cmds;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelloCommand extends Command {

    public HelloCommand() {
        super("hello", "Greets you", false);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        event.getChannel().sendMessage("hi there!").queue();
    }
}
