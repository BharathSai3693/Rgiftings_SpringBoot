package com.rgiftings.Backend.DTO;

import java.math.BigDecimal;

public record OrderItemRequest(
    long productId,
    int quantity,
    BigDecimal itemPrice
) {
}
