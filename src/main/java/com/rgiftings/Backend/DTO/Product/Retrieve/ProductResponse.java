package com.rgiftings.Backend.DTO.Product.Retrieve;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal basePrice,
        Integer stock,
        String category,
        List<ProductImageResponse> imageUrls,
        BigDecimal taxRate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ProductAttributeResponse> productAttributes

) {
}
