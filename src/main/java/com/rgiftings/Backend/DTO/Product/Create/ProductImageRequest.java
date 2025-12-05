package com.rgiftings.Backend.DTO.Product.Create;

import java.time.LocalDateTime;

public record ProductImageRequest(
        String imageUrl,
        Boolean isPrimary,
        LocalDateTime createdAt
) {
}
