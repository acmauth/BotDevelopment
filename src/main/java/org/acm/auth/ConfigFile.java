package org.acm.auth;

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
            // the file was just created
            populateFile();
            System.out.println("Config file generated! Fill your credentials.");
            System.exit(0);
        } else {
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
        for(ConfigKey key : requiredKeys) {
            if(!json.has(key.getKey())) {
                throw new IllegalStateException("Missing required key: " + key.getKey());
            }
        }
    }

    private void populateFile() throws IOException {
        JSONObject json = new JSONObject();
        for(ConfigKey key : ConfigKey.values()) {
            json.put(key.getKey(), key.getDefaultValue());
        }

        Files.write(this.cfgFile.toPath(), json.toString(2).getBytes());
    }

    public String getValue(ConfigKey cfgKey) {
        String value;
        if (json.has(cfgKey.getKey())) {
            value = json.getString(cfgKey.getKey());
            if(value.equals(""))
                value = cfgKey.getDefaultValue();
        } else {
            value = cfgKey.getDefaultValue();
        }

        return value;
    }
}
