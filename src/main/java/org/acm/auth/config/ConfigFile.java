package org.acm.auth.config;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ConfigFile {
    private final ConfigKey[] requiredKeys;
    private final File cfgFile;
    private JSONObject json;

    public ConfigFile(String pathName) throws IOException {
        this.requiredKeys = new ConfigKey[] { ConfigKey.TOKEN };
        this.cfgFile = new File(pathName);
        if (this.cfgFile.createNewFile()) {
            // we just created the file,
            // let's populate it
            populateFile();
            System.out.println("Config file generated! Fill your credentials.");
            System.exit(0);
        } else {
            // createNewFile() returns false
            // when the file already exists
            readFile();
            verifyFile();
        }
    }

    private void readFile() throws IOException {
        List<String> lines = Files.readAllLines(cfgFile.toPath());
        String fileContent = String.join("", lines);
        json = new JSONObject(fileContent);
    }

    private void verifyFile() {
        for (ConfigKey key : this.requiredKeys) {
            if (!json.has(key.getKey())) {
                throw new IllegalStateException("Missing required key: " + key.getKey());
            }
        }
    }

    public String getValue(ConfigKey cfgKey) {
        String value;
        if (json.has(cfgKey.getKey())) {
            value = json.getString(cfgKey.getKey());
            if (value.equals("")) {
                value = cfgKey.getDefaultValue();
            }
        } else {
            // config key missing from the file
            // get its default value
            value = cfgKey.getDefaultValue();
        }
        return value;
    }

    private void populateFile() throws IOException {
        JSONObject json = new JSONObject();
        for (ConfigKey cfgKey : ConfigKey.values()) {
            json.put(cfgKey.getKey(), cfgKey.getDefaultValue());
        }
        Files.write(this.cfgFile.toPath(), json.toString(2).getBytes());
    }
}
