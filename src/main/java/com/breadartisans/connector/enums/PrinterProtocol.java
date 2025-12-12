package com.breadartisans.connector.enums;

public enum PrinterProtocol {
    ESCPOS("escpos"),
    EPSON_FISCAL("epson_fiscal"),
    HASAR_FISCAL("hasar_fiscal");

    private final String value;

    PrinterProtocol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // Helper
    public static PrinterProtocol fromString(String text) {
        for (PrinterProtocol b : PrinterProtocol.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return ESCPOS; // Default
    }
}
