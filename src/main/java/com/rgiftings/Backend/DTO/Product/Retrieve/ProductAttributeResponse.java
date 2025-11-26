package com.rgiftings.Backend.DTO.Product.Retrieve;

import java.util.List;

public record ProductAttributeResponse(
        Long id,
        Long attributeId,
        String label,
        List<ProductAttributeValueResponse> values
) {
}
