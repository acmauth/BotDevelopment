package org.acm.auth;

import net.dv8tion.jda.api.JDABuilder;
import org.acm.auth.cfg.Config;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws LoginException, IOException {
        // We initialize the Config object, which holds information such as the bot's token
        // This way, sensitive information is not visible within the source code!
        Config cfg = new Config();

        // You first have to create an application on Discord's developers page:
        // https://discordapp.com/developers/applications/me

        JDABuilder builder = new JDABuilder(); // Create a new builder instance
        builder.setToken(cfg.getToken()); // Provide the "password" for your bot application
        builder.addEventListeners(new CmdRegistry(cfg)); // Add the command registry to the event listeners
        builder.build(); // After you've configured everything, initialize the connection

        // Once everything is running, you can invite your bot to your server with this:
        // https://discordapp.com/oauth2/authorize?client_id=APP_ID_FROM_DEVELOPERS_PAGE&scope=bot
    }
}
