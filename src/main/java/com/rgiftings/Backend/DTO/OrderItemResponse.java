package com.rgiftings.Backend.DTO;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productName,
        int quantity,
        BigDecimal lineTotalPrice
) {
}
