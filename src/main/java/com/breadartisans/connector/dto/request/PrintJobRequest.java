package com.breadartisans.connector.dto.request;

import com.breadartisans.connector.dto.request.voucher.TicketData;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PrintJobRequest(
        @JsonProperty("content")TicketData data,
        boolean cutPaper,
        boolean openCashDrawer
) {
}
