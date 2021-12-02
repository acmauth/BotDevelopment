package org.acm.auth.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Represents a command that greets the user.
 */
public class HiCommand extends Command {
    /**
     * Default constructor
     */
    public HiCommand() {
        super("hi", "Greets the user", false, new String[] { "hello", "greet" });
    }

    /**
     * @see Command
     */
    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        event.getChannel().sendMessage("Hi " + event.getAuthor().getAsMention() + "!").queue();
    }
}
