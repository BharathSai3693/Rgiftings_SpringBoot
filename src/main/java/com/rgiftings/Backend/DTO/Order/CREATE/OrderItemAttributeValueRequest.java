package com.rgiftings.Backend.DTO.Order.CREATE;

import java.math.BigDecimal;

public record OrderItemAttributeValueRequest(
        Long productAttributeValueId,
        String customText,
        String fileUrl,
        BigDecimal extraPrice
) {
}


