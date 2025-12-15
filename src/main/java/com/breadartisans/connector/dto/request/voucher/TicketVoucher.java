package com.breadartisans.connector.dto.request.voucher;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record TicketVoucher(
        Integer code,
        String name,
        Integer pos,
        Integer number,
        String date,
        String hour,
        boolean reprint
) {}
