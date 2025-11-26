package com.rgiftings.Backend.DTO.Attribute.Retrieve;

public record AttributeValueResponse(
        Long valueId,
        String displayCode,
        String value
) {
}
