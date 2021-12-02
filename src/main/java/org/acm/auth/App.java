package org.acm.auth;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.acm.auth.config.ConfigFile;
import org.acm.auth.config.ConfigKey;
import org.acm.auth.managers.CommandManager;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws LoginException, InterruptedException, IOException {
        // load the configuration file and then create a JDA instance to interact with Discord providing the token and
        // prefix values stored in the config file
        ConfigFile configFile = new ConfigFile("config.json");
        JDA jda = JDABuilder.createDefault(configFile.getValue(ConfigKey.TOKEN))
                .addEventListeners(new CommandManager(configFile.getValue(ConfigKey.PREFIX)))
                .build();

        jda.awaitReady();
    }
}
