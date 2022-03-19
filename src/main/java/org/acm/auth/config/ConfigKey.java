package org.acm.auth.config;

/**
 * Represents a configuration key
 */
public enum ConfigKey {
    TOKEN("token", ""),
    GITHUBTOKEN("github_token", ""),
    PREFIX("prefix", "-"),
    GIPHY_TOKEN("giphy_token", ""),
    DEV_ID("dev_id", "");

    private final String key;
    private final String defaultValue;

    /**
     * Constructs a config key with the specified key name and defaultValue.
     * @param key the config key's name as {@link String}
     * @param defaultValue the key's default value as {@link String}
     */
    ConfigKey(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the key's name.
     * @return the key's name as {@link String}
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the key's default value.
     * @return the key's default value as {@link String}
     */
    public String getDefaultValue() {
        return defaultValue;
    }
}
