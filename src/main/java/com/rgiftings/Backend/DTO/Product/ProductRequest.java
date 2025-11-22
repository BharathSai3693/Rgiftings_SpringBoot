package com.rgiftings.Backend.DTO.Product;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        String description,
        BigDecimal basePrice,
        Integer stock,
        String category
) {
}
