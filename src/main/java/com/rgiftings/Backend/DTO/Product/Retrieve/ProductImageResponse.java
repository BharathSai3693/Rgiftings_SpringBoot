package com.rgiftings.Backend.DTO.Product.Retrieve;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductImageResponse(
        Long id,
        String imageUrl,
        Boolean isPrimary,
        LocalDateTime createdAt
) {
}
