package com.rgiftings.Backend.DTO.Product;

public record ProductAttributeValueResponse(
        Long id,
        Long attributeValueId,
        String value,
        String displayCode,
        Double extraPrice
) {
}
