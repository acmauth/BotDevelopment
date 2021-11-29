package org.acm.auth;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws LoginException, InterruptedException, IOException {
        ConfigFile configFile = new ConfigFile("config.json");
        JDA jda = JDABuilder.createDefault(configFile.getValue(ConfigKey.TOKEN))
                .addEventListeners(new MessageReceivedEventListener())
                .build();

        jda.awaitReady();
    }
}
