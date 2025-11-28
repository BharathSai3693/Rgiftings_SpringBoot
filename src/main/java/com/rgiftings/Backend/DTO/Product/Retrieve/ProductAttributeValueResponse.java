package com.rgiftings.Backend.DTO.Product.Retrieve;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductAttributeValueResponse(
        Long id,
        String attributeValueName,
        BigDecimal extraPrice
) {
}
