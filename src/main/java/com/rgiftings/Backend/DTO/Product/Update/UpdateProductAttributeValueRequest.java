package com.rgiftings.Backend.DTO.Product.Update;

import java.math.BigDecimal;

public record UpdateProductAttributeValueRequest(
        Long id,
        Long AttributeValueId,
        BigDecimal extraPrice
) {
}
