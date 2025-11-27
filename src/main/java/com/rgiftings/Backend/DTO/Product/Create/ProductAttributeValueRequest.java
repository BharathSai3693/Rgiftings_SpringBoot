package com.rgiftings.Backend.DTO.Product.Create;

import java.math.BigDecimal;

public record ProductAttributeValueRequest(
        Long attributeValueId,
        BigDecimal extraPrice
) {
}
