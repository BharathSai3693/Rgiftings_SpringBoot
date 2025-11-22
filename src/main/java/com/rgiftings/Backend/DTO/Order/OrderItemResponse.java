package com.rgiftings.Backend.DTO.Order;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productName,
        int quantity,
        BigDecimal lineTotalPrice
) {
}
