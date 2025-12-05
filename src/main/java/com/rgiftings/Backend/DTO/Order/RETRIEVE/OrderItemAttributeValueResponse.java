package com.rgiftings.Backend.DTO.Order.RETRIEVE;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemAttributeValueResponse(
        Long orderItemAttributeValueId,
        Long attributeValueId,
        String attributeValueValue,
        Long productAttributeValueId,
        BigDecimal extraPrice,
        String customText,
        String fileUrl
) {

}
