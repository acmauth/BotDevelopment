package org.acm.auth.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/*************************************************************************
* Implementation of the math.random() function to flip a coin on command *
**************************************************************************/


public class CointossCommand extends Command {
    public CointossCommand() {
        super("cointoss", "A simple coin toss", false, new String[]{"coin-toss", "coin-flip", "flip-a-coin"});
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        String coin = Math.random() > 0.5 ? "Heads!" : "Tails!";
        event.getChannel().sendMessage("It's " + coin).queue();
    }
}
