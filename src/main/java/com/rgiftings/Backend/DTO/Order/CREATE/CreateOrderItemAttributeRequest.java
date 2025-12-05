package com.rgiftings.Backend.DTO.Order.CREATE;

import java.util.List;

public record CreateOrderItemAttributeRequest(
        Long attributeTypeId,
        String attributeTypeName,
        Long productAttributeId,
        String productAttributeLabel,  // e.g., Frame Size, Handle Size
        List<CreateOrderItemAttributeValueRequest> selectedAttributeValues
) {}
