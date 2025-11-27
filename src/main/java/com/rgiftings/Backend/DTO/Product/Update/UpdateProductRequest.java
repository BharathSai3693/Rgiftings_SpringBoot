package com.rgiftings.Backend.DTO.Product.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record UpdateProductRequest(
        Long id,
        String name,
        String description,
        BigDecimal basePrice,
        BigDecimal taxRate,
        Integer stock,
        LocalDateTime updatedAt,
        String category,
        String imageUrl,
        List<UpdateProductAttributeRequest> productAttributeRequestList
) {
}
