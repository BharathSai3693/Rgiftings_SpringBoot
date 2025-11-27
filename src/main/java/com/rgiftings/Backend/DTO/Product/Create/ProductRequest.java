package com.rgiftings.Backend.DTO.Product.Create;

import java.math.BigDecimal;

import java.util.List;

public record ProductRequest(
        String name,
        String description,
        BigDecimal basePrice,
        Integer stock,
        BigDecimal taxRate,
        String category,
        String imageUrl,
        List<ProductAttributeRequest> attributes
) {
}
