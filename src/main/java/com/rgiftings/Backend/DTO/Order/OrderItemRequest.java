package com.rgiftings.Backend.DTO.Order;

import java.math.BigDecimal;

public record OrderItemRequest(
    long productId,
    int quantity,
    BigDecimal itemPrice
) {
}
