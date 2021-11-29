package org.acm.auth;

public enum ConfigKey {
    TOKEN("token", ""),
    GITHUBTOKEN("github_token", ""),
    PREFIX("prefix", "-");

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
