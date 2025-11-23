package com.rgiftings.Backend.DTO.Product;

public record ProductAttributeValueRequest(
        Long attributeValueId,
        // When attributeValueId is null, value lets clients submit a brand-new attribute value inline
        String value,
        String displayCode,
        Double extraPrice
) {
}
