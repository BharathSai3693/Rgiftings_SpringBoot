package com.rgiftings.Backend.DTO.Order.CREATE;

import java.math.BigDecimal;

public record CreateOrderItemAttributeValueRequest(
        Long attributeValueId,
        Long productAttributeValueId,
        BigDecimal extraPrice,

        String customText,
        String fileUrl
) {}
