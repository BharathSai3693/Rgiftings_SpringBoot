package com.rgiftings.Backend.DTO.Order.CREATE;

import java.util.List;

public record OrderItemAttributeRequest(
        Long productAttributeId,
        List<OrderItemAttributeValueRequest> orderItemAttributeValueRequestList

) {
}

