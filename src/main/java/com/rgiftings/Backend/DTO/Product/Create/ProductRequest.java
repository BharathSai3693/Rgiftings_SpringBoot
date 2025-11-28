package com.rgiftings.Backend.DTO.Product.Create;

import lombok.Builder;

import java.math.BigDecimal;

import java.util.List;

@Builder
public record ProductRequest(
        String name,
        String description,
        BigDecimal basePrice,
        Integer stock,
        String category,
        String imageUrl,
        BigDecimal taxRate,
        List<ProductAttributeRequest> productAttributes
) {
}
