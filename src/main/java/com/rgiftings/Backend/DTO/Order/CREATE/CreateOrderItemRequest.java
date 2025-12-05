package com.rgiftings.Backend.DTO.Order.CREATE;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderItemRequest(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal basePrice,
        BigDecimal taxRate,
        BigDecimal lineExtraPrice,
        BigDecimal lineTax,
        BigDecimal lineTotalPrice,
        List<CreateOrderItemAttributeRequest> orderItemAttributes
) {}
