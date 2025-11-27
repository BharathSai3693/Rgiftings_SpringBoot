package com.rgiftings.Backend.DTO.Order.CREATE;

import java.math.BigDecimal;
import java.util.List;

public record OrderItemRequest(
        Long productId,
        Integer quantity,
        BigDecimal basePrice,
        BigDecimal extraPrice,
        BigDecimal lineTotalPrice, //no tax (totalPrice * quantiy)
        BigDecimal lineTax,
        BigDecimal taxRate,

        List<OrderItemAttributeRequest> orderItemAttributeRequestList



) {
}

