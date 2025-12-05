package com.rgiftings.Backend.DTO.Product.Update;

import java.time.LocalDateTime;

public record UpdateProductImageRequest(
        Long id,
        String imageUrl,
        Boolean isPrimary,
        LocalDateTime createdAt
) {
}


