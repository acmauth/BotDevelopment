package org.acm.auth;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Random;

// This class extends ListenerAdapter, which has methods for every possible event that Discord may send.
public class MessageReceiver extends ListenerAdapter {
    // Pseudorandom number generator to be used in various commands
    private static final Random RANDOM = new Random();

    @Override
    // We override the empty method that is present in ListenerAdapter.
    // This is called internally by JDA whenever a message is received.
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        // Make sure we don't reply to bot accounts, including ourselves!
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentDisplay(); // Get the message's content, as shown in the chat.
        String[] args = message.split("\\s+"); // Split the received message on all whitespace characters.

        if (message.equalsIgnoreCase("?hi")) {
            // We received a message that matches ?hi (ignoring case sensitivity).
            // We can specify a behavior below; let's just reply with a static message for now.
            event.getChannel().sendMessage("hi there").queue();
            // Make sure to use queue() for the message creation request to actually get sent!
        } else if (message.toLowerCase().startsWith("?dice")) {
            // We detected that a message that starts with ?dice was sent.
            // Let's find out whether it had a (valid) argument to determine the # of sides.
            int sides;
            if (args.length == 1) {
                // No argument (the message received was just ?dice), so by default we have 6 sides.
                sides = 6;
            } else {
                // We have at least one extra argument. Check to see if the 1st one is a valid integer.
                if (isPositiveInteger(args[1])) {
                    // We have a number we can use as the number of sides.
                    sides = Integer.parseInt(args[1]);
                } else {
                    // The user entered an invalid argument. Show an error message!
                    event.getChannel().sendMessage("That's not a number!").queue();
                    return; // Do not proceed with normal execution when encountering an error.
                }
            }
            int number = RANDOM.nextInt(sides) + 1; // nextInt(bound) returns an int within [0, bound)
            event.getChannel().sendMessage(""+number).queue(); // Always queue()!
        }
    }

    /**
     * Compare the given string to a regular expression that matches 1 or more digits.
     * @param str The string to check if it's a valid positive integer.
     * @return true if the string represents a positive integer, false otherwise.
     */
    private boolean isPositiveInteger(String str) {
        return str.matches("\\d+");
    }
}
