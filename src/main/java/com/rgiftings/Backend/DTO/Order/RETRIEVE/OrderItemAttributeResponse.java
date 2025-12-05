package com.rgiftings.Backend.DTO.Order.RETRIEVE;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderItemAttributeResponse(
        Long orderItemAttributeId,
        Long attributeTypeId,
        String attributeTypeName,
        String productAttributeLabel,
        List<OrderItemAttributeValueResponse> orderItemAttributeValueResponseList
) {
}
