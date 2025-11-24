package com.rgiftings.Backend.DTO.Attribute;

import java.util.List;

public record AttributeTypeRequest(
        String type,
        String description,
        List<AttributeValueRequest> attributeValueRequests
) {
}
