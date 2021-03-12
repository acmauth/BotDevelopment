package org.acm.auth;

import net.dv8tion.jda.api.JDABuilder;
import org.acm.auth.config.ConfigFile;
import org.acm.auth.config.ConfigKey;
import org.acm.auth.managers.CommandManager;
import org.acm.auth.managers.ReactionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class App {
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    public static void main(String[] args) throws LoginException, IOException {
        LOGGER.info("Initializing bot!");
        ConfigFile config = new ConfigFile("config.json");
        JDABuilder
                .createDefault(config.getValue(ConfigKey.TOKEN))
                .addEventListeners(new CommandManager(config))
                .addEventListeners(new ReactionManager())
                .build();
    }
}
