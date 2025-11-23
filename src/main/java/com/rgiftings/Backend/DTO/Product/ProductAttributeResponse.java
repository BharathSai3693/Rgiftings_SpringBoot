package com.rgiftings.Backend.DTO.Product;

import java.util.List;

public record ProductAttributeResponse(
        Long id,
        String label,
        Long attributeTypeId,
        String attributeTypeName,
        List<ProductAttributeValueResponse> values
) {
}
