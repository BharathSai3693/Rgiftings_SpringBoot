package com.rgiftings.Backend.DTO.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal basePrice,
        Integer stock,
        String category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
