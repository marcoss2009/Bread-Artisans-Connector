package com.breadartisans.connector.dto.request.voucher;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.Map;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record TicketTotals (
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        BigDecimal payment,
        BigDecimal change,
        Map<String, BigDecimal> payments,
        Map<String, TicketTax> taxes
) {}

