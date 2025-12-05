package com.rgiftings.Backend.DTO.Product.Create;

import com.rgiftings.Backend.Model.Product.ProductImage;
import lombok.Builder;

import java.math.BigDecimal;

import java.util.List;

@Builder
public record ProductRequest(
        String name,
        String description,
        BigDecimal basePrice,
        Integer stock,
        String category,
        List<ProductImageRequest> imageUrls,
        BigDecimal taxRate,
        List<ProductAttributeRequest> productAttributes
) {
}
