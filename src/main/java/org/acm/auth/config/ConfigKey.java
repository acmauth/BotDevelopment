package org.acm.auth.config;

public enum ConfigKey {
    TOKEN("token", ""),
    PREFIX("prefix", "-"),
    DEV_ID("dev_id", ""),
    GOOGLE_API_KEY("google_key", "");

    private final String key;
    private final String defaultValue;

    ConfigKey(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
