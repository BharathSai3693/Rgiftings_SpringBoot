package com.rgiftings.Backend.DTO.Attribute.Retrieve;

import lombok.Builder;

import java.util.List;

@Builder
public record AttributeTypeResponse(
    Long id,
    String name,
    String inputType,
    List<AttributeValueResponse> attributeValues
) {
}
