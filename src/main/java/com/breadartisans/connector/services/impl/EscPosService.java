package com.breadartisans.connector.services.impl;

import com.breadartisans.connector.dto.request.PrintJobRequest;
import com.breadartisans.connector.dto.request.voucher.*;
import com.breadartisans.connector.services.PrinterService;
import com.breadartisans.connector.utils.PrinterUtils;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.barcode.QRCode;
import com.github.anastaciocintra.output.PrinterOutputStream;
import javax.print.PrintService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class EscPosService implements PrinterService {
    private String printerName;

    public EscPosService(String printerName) {
        this.printerName = printerName;
    }

    @Override
    public void print(PrintJobRequest request) throws Exception {
        var data = request.data();
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        if (printService == null) throw new Exception("Impresora no encontrada: " + printerName);

        try (PrinterOutputStream outputStream = new PrinterOutputStream(printService)) {
            EscPos escpos = new EscPos(outputStream);

            // Print header
            printHeader(escpos, data.business());

            // Print voucher data
            printVoucherData(escpos, data.voucher());

            // Print client data
            printClientData(escpos, data.client());

            // Print items
            printItems(escpos, data.items());

            // Print totals
            printTotals(escpos, data.totals(), data.voucher().code());

            // Print operation
            printOperation(escpos, data.fiscal(), data.legalAlerts());

            // Print footer
            printFooter(escpos);

            // Cut paper?
            if (request.cutPaper())
                escpos.cut(EscPos.CutMode.FULL);

            // Open cash drawer?
            if (request.openCashDrawer())
                escpos.write(27).write(112).write(0).write(25).write(250);

            escpos.close();
        }
    }

    private void printHeader(EscPos escpos, TicketBusiness business) throws IOException {
        // Centrado y Negrita para el nombre comercial
        Style headerStyle = new Style()
                .setFontSize(Style.FontSize._1, Style.FontSize._1)
                .setJustification(Style.Justification.Left_Default)
                .setBold(false);

        escpos.writeLF(headerStyle, business.commercialName());
        escpos.writeLF(headerStyle, business.legalName());
        escpos.writeLF(headerStyle, business.taxIdentificator() + ": " + business.taxNumber());
        escpos.writeLF(headerStyle, "Ing. Brutos: " + business.grossIncomeNumber());

        if (business.commercialAddress() != null) {
            escpos.writeLF(headerStyle, "Dom. Legal: " + business.legalAddress().toUpperCase());
            escpos.writeLF(headerStyle, "Dom. Comercial: " + business.commercialAddress().toUpperCase());
        } else
            escpos.writeLF(headerStyle, business.legalAddress().toUpperCase());
        escpos.writeLF(headerStyle, "Inicio de actividades: " + business.startDate());
        escpos.writeLF(headerStyle, business.taxCategory().toUpperCase());
    }

    private void printVoucherData(EscPos escpos, TicketVoucher voucher) throws IOException {
        escpos.writeLF(PrinterUtils.lineBreaker());

        Style voucherDataStyle = new Style()
                .setFontSize(Style.FontSize._1, Style.FontSize._1)
                .setJustification(Style.Justification.Center)
                .setBold(true);

        String voucherCode = String.format("%03d", voucher.code());
        String posNumber = String.format("%05d", voucher.pos());
        String voucherNumber = String.format("%08d", voucher.number());
        String reprintLabel = voucher.reprint() ? "DUPLICADO" : "ORIGINAL";
        escpos.writeLF(voucherDataStyle, "Cód. " + voucherCode + " - " + voucher.name().toUpperCase() + " (" + reprintLabel + ")");

        Style voucherNumberDataStyle = new Style()
                .setFontSize(Style.FontSize._1, Style.FontSize._1)
                .setJustification(Style.Justification.Right)
                .setBold(false);
        escpos.writeLF(voucherNumberDataStyle, "N° " + posNumber + "-" + voucherNumber);

        Style voucherDateDataStyle = new Style()
                .setFontSize(Style.FontSize._1, Style.FontSize._1)
                .setJustification(Style.Justification.Right)
                .setBold(false);
        String dateTime = PrinterUtils.twoColumns("Fecha: " + voucher.date(), "Hora: " + voucher.hour());
        escpos.writeLF(voucherDateDataStyle, dateTime);
    }

    private void printClientData(EscPos escpos, TicketClient client) throws IOException {
        escpos.writeLF(PrinterUtils.lineBreaker());

        if (client.name() != null) {
            Style clientStyle = new Style()
                    .setFontSize(Style.FontSize._1, Style.FontSize._1)
                    .setJustification(Style.Justification.Left_Default)
                    .setBold(false);

            escpos.writeLF(clientStyle, client.name().toUpperCase());
            escpos.writeLF(clientStyle, client.taxIdentificator().toUpperCase() + " " + client.taxNumber());

            if (client.address() != null)
                escpos.writeLF(clientStyle, client.address().toUpperCase());

            escpos.writeLF(clientStyle, client.taxCategory().toUpperCase());
        } else {
            Style noClientStyle = new Style()
                    .setFontSize(Style.FontSize._1, Style.FontSize._1)
                    .setJustification(Style.Justification.Center)
                    .setBold(false);

            escpos.writeLF(noClientStyle, "A CONSUMIDOR FINAL");
        }

        escpos.writeLF(PrinterUtils.lineBreaker());
    }

    private void printItems(EscPos escpos, Map<String, TicketItem> items) throws IOException {
        Style itemStyle = new Style()
                .setFontSize(Style.FontSize._1, Style.FontSize._1)
                .setJustification(Style.Justification.Left_Default)
                .setBold(false);

        for (Map.Entry<String, TicketItem> entry : items.entrySet()) {
            String barcode = entry.getKey();
            TicketItem item = entry.getValue();
            escpos.writeLF(itemStyle, item.name() + " (" + barcode + ")");

            String row = PrinterUtils.buildRow(
                    new PrinterUtils.Column(item.qnty() + item.unit() + " - " + item.price().toString(), 20, PrinterUtils.Align.LEFT),
                    new PrinterUtils.Column("(" + item.vat().toString() + ")", 7, PrinterUtils.Align.CENTER),
                    new PrinterUtils.Column(item.subtotal().toString(), 21, PrinterUtils.Align.RIGHT)
            );
            escpos.writeLF(itemStyle, row);
        }
    }

    private void printTotals(EscPos escpos, TicketTotals totals, int voucherCode) throws IOException {
        Style subtotalStyle = new Style()
                .setFontSize(Style.FontSize._1, Style.FontSize._1)
                .setJustification(Style.Justification.Left_Default)
                .setBold(false);

        Style totalStyle = new Style()
                .setFontSize(Style.FontSize._2, Style.FontSize._1)
                .setJustification(Style.Justification.Left_Default)
                .setBold(true);

        // Print taxes?
        switch (voucherCode) {
            case 1:
            case 2:
            case 3:
            case 51:
            case 52:
            case 53:
                escpos.writeLF(subtotalStyle, PrinterUtils.lineBreaker());
                String subtotal = PrinterUtils.twoColumns("SUBTOTAL", totals.subtotal().toString());
                escpos.writeLF(subtotalStyle, subtotal);
                escpos.feed(1);

                // Iterate taxes
                for (Map.Entry<String, TicketTax> entry : totals.taxes().entrySet()) {
                    String taxCode = entry.getKey();
                    TicketTax tax = entry.getValue();
                    String netRow = PrinterUtils.twoColumns("NETO GRAVADO " + taxCode + "%", tax.subtotalNet().toString());
                    String taxRow = PrinterUtils.twoColumns("IVA " + taxCode + "%", tax.subtotalTax().toString());
                    escpos.writeLF(subtotalStyle, netRow);
                    escpos.writeLF(subtotalStyle, taxRow);
                }
                break;
            default:
                break;
        }

        escpos.feed(1);

        String totalRow = PrinterUtils.twoColumnsBold("TOTAL", totals.total().toString());
        String paymentRow = PrinterUtils.twoColumns("RECIBI(MOS)", totals.payment().toString());
        String changeRow = PrinterUtils.twoColumnsBold("CAMBIO", totals.change().toString());

        escpos.writeLF(totalStyle, totalRow);
        escpos.writeLF(subtotalStyle, paymentRow);

        // Print payments
        for (Map.Entry<String, BigDecimal> entry : totals.payments().entrySet()) {
            String method = entry.getKey();
            BigDecimal amount = entry.getValue();
            String paymentMethodRow = PrinterUtils.twoColumns(method.toUpperCase(), amount.toString());
            escpos.writeLF(subtotalStyle, paymentMethodRow);
        }

        escpos.writeLF(totalStyle, changeRow);

        // Law 27.743 alert
        switch (voucherCode) {
            case 6:
            case 7:
            case 8:
            case 11:
            case 12:
            case 13:
                escpos.feed(1);
                escpos.writeLF(subtotalStyle, PrinterUtils.lineBreaker());
                escpos.writeLF(subtotalStyle, "REGIMEN DE TRANSPARENCIA FISCAL AL CONSUMIDOR");
                escpos.writeLF(subtotalStyle, "(LEY 27.743)");
                escpos.feed(1);

                String taxesContent = PrinterUtils.twoColumns("IVA Contenido", totals.tax().toString());
                escpos.writeLF(subtotalStyle, taxesContent);
                escpos.feed(1);

                escpos.writeLF(subtotalStyle, "LOS IMPUESTOS INFORMADOS SON SOLO LOS QUE");
                escpos.writeLF(subtotalStyle, "CORRESPONDEN A NIVEL NACIONAL.");

                escpos.writeLF(subtotalStyle, PrinterUtils.lineBreaker());
                break;
            default:
                break;
        }
    }

    private void printOperation(EscPos escpos, TicketFiscal operation, Map<String, String> legalAlerts) throws IOException {
        Style operationStyle = new Style()
                .setFontSize(Style.FontSize._1, Style.FontSize._1)
                .setJustification(Style.Justification.Left_Default)
                .setBold(false);

        Style gratefulnessStyle = new Style()
                .setFontSize(Style.FontSize._1, Style.FontSize._1)
                .setJustification(Style.Justification.Center)
                .setBold(true);

        escpos.feed(1);

        escpos.writeLF(operationStyle, "Operación: " + (operation.operation() ? "On-line" : "Off-line"));
        escpos.writeLF(operationStyle, "Número de orden: " + String.valueOf(operation.order()));
        escpos.writeLF(operationStyle, "Cajero: " + operation.cashier());
        escpos.feed(1);

        escpos.writeLF(operationStyle, "ORIENTACION AL CONSUMIDOR");
        for(Map.Entry<String, String> entry : legalAlerts.entrySet()) {
            escpos.writeLF(operationStyle, entry.getValue());
        }

        // QR
        if (operation.afipQr() != null && !operation.afipQr().isEmpty()) {
            QRCode qrcode = new QRCode();
            qrcode.setSize(4);
            qrcode.setJustification(EscPosConst.Justification.Center);
            escpos.write(qrcode, operation.afipQr());
        }

        escpos.feed(1);

        escpos.writeLF(gratefulnessStyle, "¡Muchas gracias por su compra!");
        escpos.feed(1);

        // CAE
        escpos.writeLF(operationStyle, PrinterUtils.lineBreaker());
        escpos.writeLF(gratefulnessStyle, "COMPROBANTE AUTORIZADO");
        String caeRow = PrinterUtils.twoColumns("CAE: " + operation.cae(), "CAE Vto.: " + operation.caeDueDate());
        escpos.writeLF(operationStyle, caeRow);
        escpos.writeLF(operationStyle, PrinterUtils.lineBreaker());
    }

    private void printFooter(EscPos escpos) throws IOException {
        Style copyrightStyle = new Style()
                .setFontSize(Style.FontSize._1, Style.FontSize._1)
                .setJustification(Style.Justification.Center)
                .setBold(false);

        escpos.feed(1);
        escpos.writeLF(copyrightStyle, "Comprobante generado con Bread Artisans");
        escpos.writeLF(copyrightStyle, "www.breadartisans.com");
        escpos.feed(4);
    }
}
