package com.breadartisans.connector.ui;

import com.breadartisans.connector.config.AppConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private JLabel statusLabel;
    private JLabel printerLabel;
    private JLabel protocolLabel;
    private JLabel environmentLabel;
    private JLabel portLabel;

    public MainFrame() {
        super("Bread Artisans Connector");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 280);
        setLocationRelativeTo(null); // Center the frame on screen
        setLayout(new BorderLayout());

        initComponents();
        loadConfigData();
    }

    private void initComponents() {
        // Server status
        statusLabel = new JLabel("Iniciando servicios...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Open Sans", Font.BOLD, 18));
        statusLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        statusLabel.setOpaque(true);
        add(statusLabel, BorderLayout.NORTH);

        // Settings data
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        infoPanel.setBorder(new EmptyBorder(10, 40, 10, 40));

        // Port
        infoPanel.add(new JLabel("Puerto del Servidor:"));
        portLabel = new JLabel("...");
        portLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoPanel.add(portLabel);

        // Environment
        infoPanel.add(new JLabel("Entorno:"));
        environmentLabel = new JLabel("...");
        environmentLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoPanel.add(environmentLabel);

        // Printer
        infoPanel.add(new JLabel("Impresora (CUPS):"));
        printerLabel = new JLabel("...");
        printerLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoPanel.add(printerLabel);

        // Protocol
        infoPanel.add(new JLabel("Protocolo de Impresión:"));
        protocolLabel = new JLabel("...");
        protocolLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoPanel.add(protocolLabel);

        add(infoPanel, BorderLayout.CENTER);

        // Buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton settingsButton = new JButton("Configuración");
        settingsButton.addActionListener(e -> {
            SettingsDialog dialog = new SettingsDialog(this);
            dialog.setVisible(true);
        });

        JButton closeButton = new JButton("Cerrar Conector");
        closeButton.addActionListener(e -> System.exit(0));

        bottomPanel.add(settingsButton, BorderLayout.WEST);
        bottomPanel.add(closeButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadConfigData() {
        int port = AppConfig.getServerPort();
        portLabel.setText(port == 27323 ? "27323 (BREAD)" : String.valueOf(port));

        String environment = AppConfig.getEnvironment();
        environmentLabel.setText(environment.toUpperCase());
        environmentLabel.setForeground("production".equalsIgnoreCase(environment) ? Color.GREEN : Color.BLUE);

        printerLabel.setText(AppConfig.getPrinterName());
        protocolLabel.setText(AppConfig.getPrinterProtocol().toUpperCase());
    }

    /**
     * Update server status display
     *
     * @param isRunning
     * @param message
     */
    public void updateServerStatus(boolean isRunning, String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);

            if (isRunning) {
                statusLabel.setForeground(new Color(46, 204, 113));
                statusLabel.setIcon(UIManager.getIcon("FileView.computerIcon"));
            } else {
                statusLabel.setForeground(Color.RED);
            }
        });
    }
}
