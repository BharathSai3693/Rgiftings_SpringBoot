package com.rgiftings.Backend.DTO.Attribute.Create;

import java.util.List;

public record AttributeTypeRequest(
        String name,
        String inputType,
        List<AttributeValueRequest> attributeValues
) {
}
