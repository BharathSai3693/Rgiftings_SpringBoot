package com.rgiftings.Backend.DTO.Attribute.Update;

import com.rgiftings.Backend.DTO.Attribute.Create.AttributeValueRequest;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateAttributeRequest(
        Long id,
        String name,
        String inputType,
        List<UpdateAttributeValueRequest> attributeValues
) {
}
