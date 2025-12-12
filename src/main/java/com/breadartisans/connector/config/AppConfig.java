package com.breadartisans.connector.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private static final Properties properties = new Properties();

    static {
        try (FileInputStream input = new FileInputStream("connector.properties")) {
            properties.load(input);
        } catch (IOException exception) {
            System.err.println("No se pudo cargar el archivo de configuración: " + exception.getMessage());
        }
    }

    public static String getPrinterName() {
        return properties.getProperty("printer.name", "EPSON TM-T20II");
    }

    public static String getPrinterProtocol() {
        return properties.getProperty("printer.protocol", "escpos");
    }

    public static int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port", "27323"));
    }

    public static String getEnvironment() {
        return properties.getProperty("connector.environment", "production");
    }

    public static String getAllowedOrigin() {
        return properties.getProperty("cors.allowed_origin", "https://app.breadartisans.com");
    }
}
