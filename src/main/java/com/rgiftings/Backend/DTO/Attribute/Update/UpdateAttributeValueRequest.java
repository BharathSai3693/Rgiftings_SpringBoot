package com.rgiftings.Backend.DTO.Attribute.Update;

public record UpdateAttributeValueRequest(
        Long id,
        String value,
        String displayCode
) {
}
