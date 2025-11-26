package com.rgiftings.Backend.DTO.Attribute.Create;

import java.util.List;

public record AttributeTypeRequest(
        String type,
        String description,
        List<AttributeValueRequest> attributeValueRequests
) {
}
