package com.rgiftings.Backend.DTO.Product.Update;

public record UpdateProductAttributeValueRequest(
        Long id,
        Long AttributeValueId,
        Double extraPrice
) {
}
