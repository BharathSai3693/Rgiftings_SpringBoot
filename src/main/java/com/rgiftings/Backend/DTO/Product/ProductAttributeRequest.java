package com.rgiftings.Backend.DTO.Product;

public record ProductAttributeRequest(
        Long attributeTypeId,
        String label
) {
}
