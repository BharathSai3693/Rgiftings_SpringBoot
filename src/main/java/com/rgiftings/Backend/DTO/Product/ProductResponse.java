package com.rgiftings.Backend.DTO.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal basePrice,
        Integer stock,
        String category,
        List<ProductAttributeResponse> attributes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
