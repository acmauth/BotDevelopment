package org.acm.auth.cfg;

import com.sun.media.sound.InvalidFormatException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * This class represents a config file, which is used to store sensitive data (bot token, database credentials, etc),
 * as well as values to allow for easy customization (prefix, developer id, etc).
 */
public class Config {
    private File configFile; // An instance of the config file we'll access
    private JSONObject json; // The JSON content of the config file
    private String[] values = { "token", "prefix" }; // All values that should be present in the config file

    public Config() throws IOException {
        configFile = new File("./config.json");
        if (!configFile.exists()) {
            // The config file doesn't exist, so we'll attempt to generate it.
            generate();
            // If all went well, we'll notify the user to fill in their credentials.
            System.out.println("Config file generated! Please fill the token.");
            System.exit(0);
        } else {
            // We have a file for the config, read it and get its values.
            List<String> lines = Files.readAllLines(configFile.toPath());
            String content = String.join(" ", lines); // Convert the list of lines to one single string
            json = new JSONObject(content); // Parse the string we read into a JSON object.
            validate(); // Make sure we have all necessary fields!
        }
    }

    /**
     * Get the value from the JSON file that corresponds to "token".
     * @return the bot's token
     */
    public String getToken() {
        return json.getString("token");
    }

    /**
     * Get the value from the JSON file that corresponds to "prefix"
     * @return the bot's global prefix
     */
    public String getPrefix() {
        return json.getString("prefix");
    }

    /**
     * Generate the config file with all possible fields set to their default values.
     * @throws IOException if the file could not be created
     */
    private void generate() throws IOException {
        if (!configFile.createNewFile()) {
            throw new IOException("Config file could not be created!");
        }

        JSONObject obj = new JSONObject();
        obj.put("token", ""); // Add the "token" key with an empty value for the user to fill.
        obj.put("prefix", "?"); // Add the "prefix" key with a default value of '?'
        Files.write(configFile.toPath(), obj.toString(4).getBytes());
    }

    /**
     * Validate that the config file contains all necessary values.
     * @throws InvalidFormatException when the config file misses a required field
     */
    private void validate() throws InvalidFormatException {
        for (String v : values) {
            if (!json.has(v) || json.getString(v).isEmpty()) {
                throw new InvalidFormatException(String.format("Missing %s!", v));
            }
        }
    }
}
