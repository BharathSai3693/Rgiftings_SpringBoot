package com.rgiftings.Backend.DTO.Product.Retrieve;

import java.math.BigDecimal;

public record ProductAttributeValueResponse(
        Long id,
        Long valueId,
        String valueName,
        BigDecimal extraPrice
) {
}
