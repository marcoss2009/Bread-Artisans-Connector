package com.breadartisans.connector.enums;

public enum Environment {
    DEVELOPMENT("development"),
    PRODUCTION("production");

    private final String value;

    Environment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // Helper
    public static Environment fromString(String text) {
        for (Environment b : Environment.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return PRODUCTION; // Default
    }
}
