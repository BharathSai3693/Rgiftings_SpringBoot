package com.rgiftings.Backend.DTO.Attribute.Retrieve;

import lombok.Builder;

@Builder
public record AttributeValueResponse(
        Long id,
        String value
) {
}
