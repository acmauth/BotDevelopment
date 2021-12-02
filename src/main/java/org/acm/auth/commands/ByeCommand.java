package org.acm.auth.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Represents a command that farewells the user.
 */
public class ByeCommand extends Command {
    /**
     * Default constructor.
     */
    public ByeCommand() {
        super("bye", "Farewells the user", false, new String[] { "goodbye", "farewell" });
    }

    /**
     * @see Command
     */
    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        event.getChannel().sendMessage("Bye " + event.getAuthor().getAsMention() + "!").queue();
    }
}
