package org.acm.auth;

import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class App {
    public static void main(String[] args) throws LoginException {
        // You first have to create an application on Discord's developers page:
        // https://discordapp.com/developers/applications/me

        // Then convert it to a bot account and get its SECRET token
        String token = "NEVER share your token on GitHub :)";

        JDABuilder builder = new JDABuilder(); // Create a new builder instance
        builder.setToken(token); // Provide the "password" for your bot application
        builder.addEventListeners(new MessageReceiver()); // Register all classes that have an event method override
        builder.build(); // After you've configured everything, initialize the connection

        // Once everything is running, you can invite your bot to your server with this:
        // https://discordapp.com/oauth2/authorize?client_id=APP_ID_FROM_DEVELOPERS_PAGE&scope=bot
    }
}
