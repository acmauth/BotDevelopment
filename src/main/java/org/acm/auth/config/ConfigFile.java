package org.acm.auth.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Handles a configuration file.
 */
public class ConfigFile {
    private static final Logger LOGGER = LogManager.getLogger(ConfigFile.class);

    private final ConfigKey[] requiredKeys;
    private final File cfgFile;
    private JSONObject json;

    /**
     * Construct a config file handler.
     * @param pathName The path of the config file as {@code String}
     * @throws IOException If an error occurs e.g. when reading the file
     */
    public ConfigFile(String pathName) throws IOException {
        // set the required keys that we need from the config file
        // if one of those keys is missing then the bot can't start 
        this.requiredKeys = new ConfigKey[] { ConfigKey.TOKEN };
        // bind a File object to the config file using the defined pathName
        this.cfgFile = new File(pathName);
        if (this.cfgFile.createNewFile()) {
            LOGGER.info("Creating config file");
            // we just created the file, so let's populate it
            populateFile();
            // ask from the user to add the proper config keys to the config file
            System.out.println("Config file generated! Fill your credentials.");
            // stop the program's execution
            System.exit(0);
        } else { // createNewFile() returns false when the file already exists
            // read the file
            readFile();
            // and then verify it
            verifyFile();
        }
    }

    /**
     * Reads the config file and saves the key-value pairs to the JSON Object instance.
     * @throws IOException If an error occurs when reading the file
     */
    private void readFile() throws IOException {
        LOGGER.info("Reading config file");
        // read the lines of the config file and save each line in a list
        List<String> lines = Files.readAllLines(cfgFile.toPath());
        // combine the elements of the list to a single String
        String fileContent = String.join("", lines);
        // and then convert this single String to a JSON Object
        json = new JSONObject(fileContent);
    }

    /**
     * Verifies the validity of the config file.
     * @throws IllegalStateException if a required key-value pair is missing
     */
    private void verifyFile() throws IllegalStateException{
        // iterate through the required keys
        for (ConfigKey key : this.requiredKeys) {
            // check if a required key-value pair is missing
            if (!json.has(key.getKey())) {
                // throw an exception for the first pair missing
                throw new IllegalStateException("Missing required key: " + key.getKey());
            }
        }
    }

    /**
     * Returns the value that corresponds to a config key
     * @param cfgKey the key which we want the value of as {@code ConfigKey}
     * @return the requested value as {@code String}
     */
    public String getValue(ConfigKey cfgKey) {
        String value;
        if (json.has(cfgKey.getKey())) {
            // the config file has the specified key,
            // so get its value from the JSON object
            value = json.getString(cfgKey.getKey());
            if (value.equals("")) {
                // the value is an empty String,
                // which means that the user didn't
                // specify a value, so get the default
                // value of the requested key
                value = cfgKey.getDefaultValue();
            }
        } else {
            // the speified key is missing from the file,
            // so get its default value
            value = cfgKey.getDefaultValue();
        }
        return value;
    }

    /**
     * Fills the config file with all possible key-value pairs.
     * Initially, the default values are used for each key-value pair.
     * @throws IOException if an error occurs when writing to the file
     */
    private void populateFile() throws IOException {
        // create a JSON Object and add the required key-value pairs
        JSONObject json = new JSONObject();
        for (ConfigKey cfgKey : ConfigKey.values()) {
            LOGGER.debug("Writing default key pair - {}: {}", cfgKey.getKey(), cfgKey.getDefaultValue());
            json.put(cfgKey.getKey(), cfgKey.getDefaultValue());
        }
        // write the key-value pairs to the config file
        // using the path of the cfgFile object
        Files.write(this.cfgFile.toPath(), json.toString(2).getBytes());
    }
}
