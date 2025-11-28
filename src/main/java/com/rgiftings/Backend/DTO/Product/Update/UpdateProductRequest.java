package com.rgiftings.Backend.DTO.Product.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record UpdateProductRequest(
        Long id,
        String name,
        String description,
        BigDecimal basePrice,
        Integer stock,
        String category,
        String imageUrl,
        BigDecimal taxRate,
        List<UpdateProductAttributeRequest> productAttributes
) {
}
