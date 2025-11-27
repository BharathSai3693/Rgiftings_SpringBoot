package com.rgiftings.Backend.DTO.Order.RETRIEVE;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderItemResponse(
        Integer quantity,
        BigDecimal basePrice,
        Long productId,
        String productImageURL,
        String productName,
        BigDecimal lineExtraPrice,
        BigDecimal lineTotalPrice,
        BigDecimal lineTax,
        List<OrderItemAttributeResponse> orderItemAttributeResponseList
) {
}
