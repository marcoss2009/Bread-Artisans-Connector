package com.breadartisans.connector.dto.request;

public record PrintJobRequest(
        String content,
        boolean cutPaper,
        boolean openCashDrawer
) {
}
