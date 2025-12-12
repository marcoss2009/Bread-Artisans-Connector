package com.breadartisans.connector.ui;

import com.breadartisans.connector.enums.Environment;
import com.breadartisans.connector.enums.PrinterProtocol;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class SettingsDialog extends JDialog {
    private JTextField portField;
    private JTextField printerNameField;
    private JComboBox<PrinterProtocol> protocolBox;
    private JComboBox<Environment> environmentBox;
    private JTextField originField;

    public SettingsDialog(Frame parent) {
        super(parent, "Configuración del Conector", true);
        setSize(450, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Fields
        // 1. Port
        formPanel.add(new JLabel("Puerto:"));
        portField = new JTextField();
        portField.setToolTipText("Ej: 27323 (BREAD)");
        formPanel.add(portField);

        // 2. Printer
        formPanel.add(new JLabel("Impresora (CUPS):"));
        printerNameField = new JTextField();
        printerNameField.setToolTipText("Ej: EPSON_TM_TIII");
        formPanel.add(printerNameField);

        // 3. Printer protocol
        formPanel.add(new JLabel("Protocolo de impresión:"));
        protocolBox = new JComboBox<>(PrinterProtocol.values());
        formPanel.add(protocolBox);

        // 4. Environment
        formPanel.add(new JLabel("Entorno del conector:"));
        environmentBox = new JComboBox<>(Environment.values());
        formPanel.add(environmentBox);

        // 5. Allowed Origin
        formPanel.add(new JLabel("Origen permitido (CORS):"));
        originField = new JTextField();
        originField.setToolTipText("Ej: https://app.breadartisans.com");
        formPanel.add(originField);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Guardar");
        JButton cancelButton = new JButton("Cancelar");

        saveButton.addActionListener(e -> saveSettings());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load default settings when opening
        loadCurrentSettings();
    }

    private void loadCurrentSettings() {
        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream("connector.properties")) {
            props.load(in);

            portField.setText(props.getProperty("server.port", "27323"));
            printerNameField.setText(props.getProperty("printer.name", "Default_Printer"));

            String protoStr = props.getProperty("printer.protocol", "escpos");
            protocolBox.setSelectedItem(PrinterProtocol.fromString(protoStr));

            String envStr = props.getProperty("connector.environment", "development");
            environmentBox.setSelectedItem(Environment.fromString(envStr));

            originField.setText(props.getProperty("cors.allowed_origin", "https://app.breadartisans.com"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se encontró archivo de configuración. Se usarán defaults.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveSettings() {
        Properties props = new Properties();

        // Validate port
        try {
            Integer.parseInt(portField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El puerto debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        props.setProperty("server.port", portField.getText());
        props.setProperty("printer.name", printerNameField.getText());
        props.setProperty("printer.protocol", ((PrinterProtocol) protocolBox.getSelectedItem()).getValue());
        props.setProperty("connector.environment", ((Environment) environmentBox.getSelectedItem()).getValue());
        props.setProperty("cors.allowed_origin", originField.getText());

        try (FileOutputStream out = new FileOutputStream("connector.properties")) {
            props.store(out, "Configuración del Conector Bread Artisans");
            JOptionPane.showMessageDialog(this,
                    "Configuración guardada.\n⚠️ DEBE REINICIAR EL CONECTOR PARA APLICAR CAMBIOS.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
