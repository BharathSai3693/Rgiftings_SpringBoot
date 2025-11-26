package com.rgiftings.Backend.DTO.Attribute.Update;

import java.util.List;

public record UpdateAttributeRequest(
        Long id,
        String type,
        String description,
        List<UpdateAttributeValueRequest> attributeValues
) {
}
