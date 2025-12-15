package com.breadartisans.connector.dto.request.voucher;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.Map;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record TicketData(
        TicketBusiness business,
        TicketVoucher voucher,
        TicketClient client,
        Map<String, TicketItem> items,
        TicketTotals totals,
        TicketFiscal fiscal,
        Map<String, String> legalAlerts
) {
}

