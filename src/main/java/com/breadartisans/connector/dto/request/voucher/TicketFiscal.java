package com.breadartisans.connector.dto.request.voucher;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record TicketFiscal (
        boolean operation,
        int order,
        String cashier,
        String cae,
        String caeDueDate,
        String afipQr
) {}
