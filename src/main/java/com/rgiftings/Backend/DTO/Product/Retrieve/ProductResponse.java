package com.rgiftings.Backend.DTO.Product.Retrieve;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal basePrice,
        Integer stock,
        String category,
        String imageUrl,
        Set<ProductAttributeResponse> attributes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
