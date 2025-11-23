package com.rgiftings.Backend.DTO.Product;

public record ProductAttributeValueRequest(
        Long attributeValueId,
        String value,
        String displayCode,
        Double extraPrice
) {
}
