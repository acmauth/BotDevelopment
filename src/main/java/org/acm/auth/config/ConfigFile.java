package org.acm.auth.config;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Handles a config file.
 */
public class ConfigFile {
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
        this.requiredKeys = new ConfigKey[] { ConfigKey.TOKEN };
        // bind a File object to the config file using the incoming pathName
        this.cfgFile = new File(pathName);
        if (this.cfgFile.createNewFile()) {
            // we just created the file,
            // let's populate it
            populateFile();
            // ask from the user to add the proper config keys to the config file
            System.out.println("Config file generated! Fill your credentials.");
            System.exit(0);
        } else {
            // createNewFile() returns false when the file already exists
            // read the file
            readFile();
            // and then verify it
            verifyFile();
        }
    }

    /**
     * Reads the config file and saves the key-value pairs to the private JSON Object attribute
     * @throws IOException If an error occurs when reading the file
     */
    private void readFile() throws IOException {
        // read the lines of the config file and save each line as a list element
        List<String> lines = Files.readAllLines(cfgFile.toPath());
        // convert the elements of the list to a single String
        String fileContent = String.join("", lines);
        // and then convert this single String to a JSON Object
        json = new JSONObject(fileContent);
    }

    /**
     * Verifies the config file.
     * @throws IllegalStateException if a required key-value pair is missing
     */
    private void verifyFile() throws IllegalStateException{
        // iterate through the required keys and if a required key-value pair is missing throw an IllegalStateException
        for (ConfigKey key : this.requiredKeys) {
            if (!json.has(key.getKey())) {
                throw new IllegalStateException("Missing required key: " + key.getKey());
            }
        }
    }

    /**
     * Returns the value of a config key
     * @param cfgKey the key that we want its value as {@code ConfigKey}
     * @return the requested value as {@code String}
     */
    public String getValue(ConfigKey cfgKey) {
        String value;
        if (json.has(cfgKey.getKey())) {
            // the config file has the requested key so get its value using the json attribute
            value = json.getString(cfgKey.getKey());
            if (value.equals("")) { // the value is the empty String( the user did not fill his own value ) so get the
                                    // default value of the requested key
                value = cfgKey.getDefaultValue();
            }
        } else {
            // config key missing from the file so get its default value
            value = cfgKey.getDefaultValue();
        }
        return value;
    }

    /**
     * Fills the config file with the required key-value pairs so the user can go and fill his own credentials.
     * At the start, default values are used for each key-value pair
     * @throws IOException if an error occurs when writing to the file
     */
    private void populateFile() throws IOException {
        // create a JSON Object and add to it the required key-value pairs
        JSONObject json = new JSONObject();
        for (ConfigKey cfgKey : ConfigKey.values()) {
            json.put(cfgKey.getKey(), cfgKey.getDefaultValue());
        }
        // when finished, write the key-value pairs to the config file using the path of the attribute cfgFile
        Files.write(this.cfgFile.toPath(), json.toString(2).getBytes());
    }
}
