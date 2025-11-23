package com.rgiftings.Backend.DTO.Product;

public record ProductAttributeResponse(
        Long id,
        String label,
        Long attributeTypeId,
        String attributeTypeName
) {
}
