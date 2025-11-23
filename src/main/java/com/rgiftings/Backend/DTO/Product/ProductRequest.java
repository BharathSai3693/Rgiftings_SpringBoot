package com.rgiftings.Backend.DTO.Product;

import java.math.BigDecimal;

import java.util.List;

public record ProductRequest(
        String name,
        String description,
        BigDecimal basePrice,
        Integer stock,
        String category,
        String imageUrl,
        List<ProductAttributeRequest> attributes
) {
}
