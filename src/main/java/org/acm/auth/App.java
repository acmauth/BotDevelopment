package org.acm.auth;

import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;

public class App {
    public static void main(String[] args) throws LoginException {
        String token = "my.token.here";
        JDABuilder
                .createDefault(token)
                .addEventListeners(new EventClass())
                .build();
    }
}
