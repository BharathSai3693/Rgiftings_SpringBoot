package com.rgiftings.Backend.DTO.Product.Retrieve;

public record ProductAttributeValueResponse(
        Long id,
        Long valueId,
        String valueName,
        Double extraPrice
) {
}
