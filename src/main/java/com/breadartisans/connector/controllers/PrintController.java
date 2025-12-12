package com.breadartisans.connector.controllers;

import com.breadartisans.connector.dto.request.PrintJobRequest;
import com.breadartisans.connector.dto.response.ApiResponse;
import com.breadartisans.connector.services.PrinterService;
import com.breadartisans.connector.services.PrinterServiceFactory;
import io.javalin.http.Context;

public class PrintController {
    private final PrinterService printerService = PrinterServiceFactory.getConfiguredService();

    public void handlePrint(Context ctx) {
        try {
            PrintJobRequest request = ctx.bodyAsClass(PrintJobRequest.class);

            printerService.print(request);

            ctx.json(new ApiResponse(true, "Impreso correctamente"));
        } catch (Exception e) {
            ctx.status(500).json(new ApiResponse(false, "Error al imprimir: " + e.getMessage()));
        }
    }
}
