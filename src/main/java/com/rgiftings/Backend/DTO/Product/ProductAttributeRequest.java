package com.rgiftings.Backend.DTO.Product;

import java.util.List;

public record ProductAttributeRequest(
        Long attributeTypeId,
        String label,
        List<ProductAttributeValueRequest> values
) {
}
