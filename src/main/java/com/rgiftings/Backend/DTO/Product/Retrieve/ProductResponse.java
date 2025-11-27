package com.rgiftings.Backend.DTO.Product.Retrieve;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal basePrice,
        BigDecimal taxRate,
        Integer stock,
        String category,
        String imageUrl,
        Set<ProductAttributeResponse> attributes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
