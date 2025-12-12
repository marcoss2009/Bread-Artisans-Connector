package com.breadartisans.connector.services;

import com.breadartisans.connector.config.AppConfig;
import com.breadartisans.connector.services.impl.EscPosService;

public class PrinterServiceFactory {
    public static PrinterService getConfiguredService() {
        String protocol = AppConfig.getPrinterProtocol().toLowerCase();

        return switch(protocol) {
            case "escpos" -> new EscPosService(AppConfig.getPrinterName());
            // case "epson_fiscal" -> new EpsonFiscalService(AppConfig.getPrinterName());
            // case "hasar_fiscal" -> new HasarFiscalService(AppConfig.getPrinterName());
            default -> throw new RuntimeException("Protocolo no soportado: " + protocol);
        };
    }
}
