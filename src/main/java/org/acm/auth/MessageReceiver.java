package org.acm.auth;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

// This class extends ListenerAdapter, which has methods for every possible event that Discord may send.
public class MessageReceiver extends ListenerAdapter {
    @Override
    // We override the empty method that is present in ListenerAdapter.
    // This is called internally by JDA whenever a message is received.
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        // Make sure we don't reply to bot accounts, including ourselves!
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentDisplay(); // Get the message's content, as shown in the chat.
        if (message.equalsIgnoreCase("?hi")) {
            // We received a message that matches ?hi (ignoring case sensitivity).
            // We can specify a behavior below; let's just reply with a static message for now.
            event.getChannel().sendMessage("hi there").queue();
            // Make sure to use queue() for the message creation request to actually get sent!
        }
    }
}
