package org.acm.auth.config;

/**
 * Represents a configuration key
 */
public enum ConfigKey {
    TOKEN("token", ""),
    PREFIX("prefix", "-"),
    DEV_ID("dev_id", ""),
    GOOGLE_API_KEY("google_key", "");

    private final String key;
    private final String defaultValue;

    /**
     * Construct a config key with the specified key name and defaultValue.
     * @param key the config key's name as {@code String}
     * @param defaultValue the key's default value as {@code String}
     */
    ConfigKey(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the key's name.
     * @return the key's name as {@code String}
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the key's default value.
     * @return the key's default value as {@code String}
     */
    public String getDefaultValue() {
        return defaultValue;
    }
}
