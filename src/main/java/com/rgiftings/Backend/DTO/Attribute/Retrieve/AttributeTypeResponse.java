package com.rgiftings.Backend.DTO.Attribute.Retrieve;

import java.util.List;

public record AttributeTypeResponse(
    Long typeId,
    String type,
    String description,
    List<AttributeValueResponse> attributeValues
) {
}
