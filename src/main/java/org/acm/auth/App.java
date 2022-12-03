package org.acm.auth;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class App {
    public static void main(String[] args) throws LoginException {
        String token = "my.token.here";
        JDABuilder
                .createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new EventListener())
                .build();

        System.out.println("Waiting for commands to respond...");
    }
}
