package org.acm.auth.cmds;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.utils.NumberUtil;

import java.util.Random;

public class DiceCommand extends Command {
    private static final Random R_GEN = new Random(); // Create a static random number generator

    public DiceCommand() {
        super("dice", "Rolls a dice", true);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        int bound = 6; // Default number of sides = 6
        if (args.length > 0) {
            // If the user specifies an argument, interpret that as the amount of sides of the dice
            if (NumberUtil.isPositiveInteger(args[0])) {
                // The first argument is a valid number so we can parse it
                bound = Integer.parseInt(args[0]);
            } else {
                // The user's input was invalid so we must show an error message
                event.getChannel().sendMessage("That's not a valid number!").queue();
                return;
            }
        }
        int number = R_GEN.nextInt(bound) + 1; // nextInt(b) returns an integer between [0, b)
        event.getChannel().sendMessage(String.format("You rolled %d!", number)).queue();
    }
}
