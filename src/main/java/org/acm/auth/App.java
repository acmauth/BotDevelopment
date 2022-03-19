package org.acm.auth;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.acm.auth.config.ConfigFile;
import org.acm.auth.config.ConfigKey;
import org.acm.auth.managers.CommandManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class App {
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    public static void main(String[] args) throws LoginException, InterruptedException, IOException {
        LOGGER.info("Initializing bot!");
        // load the configuration file and then create a JDA instance to interact with Discord providing the token and
        // prefix values stored in the config file
        ConfigFile configFile = new ConfigFile("config.json");
        JDA jda = JDABuilder.createDefault(configFile.getValue(ConfigKey.TOKEN))
                .addEventListeners(new CommandManager(configFile))
                .build();

        jda.awaitReady();
    }
}
