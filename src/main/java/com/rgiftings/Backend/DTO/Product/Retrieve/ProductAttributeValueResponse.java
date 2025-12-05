package com.rgiftings.Backend.DTO.Product.Retrieve;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductAttributeValueResponse(
        Long id,
        Long attributeValueId,
        String attributeValueName,
        BigDecimal extraPrice
) {
}
