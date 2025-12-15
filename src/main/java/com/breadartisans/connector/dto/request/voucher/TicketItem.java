package com.breadartisans.connector.dto.request.voucher;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record TicketItem (
        String name,
        BigDecimal qnty,
        String unit,
        BigDecimal price,
        BigDecimal vat,
        BigDecimal subtotal
) {}
