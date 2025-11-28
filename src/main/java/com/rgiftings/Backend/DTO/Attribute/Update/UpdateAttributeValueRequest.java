package com.rgiftings.Backend.DTO.Attribute.Update;

import lombok.Builder;

@Builder
public record UpdateAttributeValueRequest(
        Long id,
        String value
) {
}
