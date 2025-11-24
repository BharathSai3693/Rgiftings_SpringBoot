package com.rgiftings.Backend.DTO.Attribute;

import java.util.List;

public record AttributeTypeResponse(
    Long typeId,
    String type,
    String description,
    List<AttributeValueResponse> attributeValues
) {
}
