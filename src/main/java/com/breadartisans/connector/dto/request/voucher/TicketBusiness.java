package com.breadartisans.connector.dto.request.voucher;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record TicketBusiness (
        String commercialName,
        String legalName,
        String taxIdentificator,
        String taxNumber,
        String grossIncomeNumber,
        String legalAddress,
        String commercialAddress,
        String startDate,
        String taxCategory
) {}
