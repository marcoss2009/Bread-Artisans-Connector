package com.breadartisans.connector.services.impl;

import com.breadartisans.connector.dto.request.PrintJobRequest;
import com.breadartisans.connector.services.PrinterService;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.output.PrinterOutputStream;
import javax.print.PrintService;

public class EscPosService implements PrinterService {
    private String printerName;

    public EscPosService(String printerName) {
        this.printerName = printerName;
    }

    @Override
    public void print(PrintJobRequest request) throws Exception {
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);

        try (PrinterOutputStream outputStream = new PrinterOutputStream(printService)) {
            EscPos escpos = new EscPos(outputStream);
            escpos.writeLF(request.content());

            // Cut paper?
            if (request.cutPaper()) {
                escpos.feed(2);
                escpos.cut(EscPos.CutMode.FULL);
            }

            escpos.close();
        }
    }
}
