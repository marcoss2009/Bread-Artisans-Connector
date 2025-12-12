package com.breadartisans.connector.services;

import com.breadartisans.connector.dto.request.PrintJobRequest;

public interface PrinterService {
    /**
     * Print something...
     *
     * @param request Content to print
     * @throws Exception (PaperOut, IO Error, etc)
     */
    void print(PrintJobRequest request) throws Exception;
}
