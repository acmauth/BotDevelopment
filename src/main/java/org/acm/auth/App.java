package org.acm.auth;

import net.dv8tion.jda.api.JDABuilder;
import org.acm.auth.config.ConfigFile;
import org.acm.auth.config.ConfigKey;
import org.acm.auth.managers.CommandManager;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws LoginException, IOException {
        ConfigFile config = new ConfigFile("config.json");
        JDABuilder
                .createDefault(config.getValue(ConfigKey.TOKEN))
                .addEventListeners(new CommandManager(config))
                .build();
    }
}
