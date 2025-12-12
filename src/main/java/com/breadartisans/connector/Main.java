package com.breadartisans.connector;

import com.breadartisans.connector.config.AppConfig;
import com.breadartisans.connector.controllers.PrintController;
import com.breadartisans.connector.dto.response.ApiResponse;
import com.breadartisans.connector.ui.MainFrame;
import io.javalin.Javalin;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static MainFrame mainFrame;

    public static void main(String[] args) {
        // Start UI
        // Yes, Swing kjsjsjs
        // Sorry professor Alejandro Peña, but swing it's simple!
        SwingUtilities.invokeLater(() -> {
            mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });

        // Let's go Bread Artisans!
        startServer();
    }

    private static void startServer() {
        new Thread(() -> {
            try {
                // Connector settings
                String printerName = AppConfig.getPrinterName();
                String printerProtocol = AppConfig.getPrinterProtocol();
                String environment = AppConfig.getEnvironment();
                String allowedOrigin = AppConfig.getAllowedOrigin();
                int serverPort  = AppConfig.getServerPort();

                Javalin app = Javalin.create(config -> {
                    config.bundledPlugins.enableCors(cors -> {
                        cors.addRule(it -> {
                            if ("production".equalsIgnoreCase(environment)) {
                                it.allowHost(allowedOrigin);
                            } else {
                                it.anyHost();
                            }
                        });
                    });
                }).start(serverPort); // Default: BREAD (27323)

                // WAIT!
                while (mainFrame == null) Thread.sleep(100);
                mainFrame.updateServerStatus(true, "Conector on-line");

                // Printer service
                PrintController printController = new PrintController();

                // Endpoints
                app.get("/status", ctx -> ctx.json(new ApiResponse(true, "Conector activo")));

                app.post("/print", printController::handlePrint);

            } catch (Exception e) {
                if (mainFrame != null) {
                    mainFrame.updateServerStatus(false, "Error: " + e.getMessage());
                }
                e.printStackTrace();
            }
        }).start();
    }
}