package com.breadartisans.connector.dto.request.voucher;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record TicketClient (
        String name,
        String taxIdentificator,
        String taxNumber,
        String address,
        String taxCategory
) {}
