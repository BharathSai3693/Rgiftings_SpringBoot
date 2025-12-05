package com.rgiftings.Backend.DTO.Product.Retrieve;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductAttributeResponse(
        Long id,
        Long attributeTypeId,
        String attributeName,
        String attributeInputType,
        String productAttributeLabel,
        List<ProductAttributeValueResponse> productAttributeValues
) {
}
