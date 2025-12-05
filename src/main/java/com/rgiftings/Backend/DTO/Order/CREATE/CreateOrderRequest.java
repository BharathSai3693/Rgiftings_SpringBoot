package com.rgiftings.Backend.DTO.Order.CREATE;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        Long userId,
        String guestEmail,
        String guestPhone,
        Long addressId,

        // for validation only (4 of these)
        BigDecimal subTotalAmount,
        BigDecimal taxAmount,
//        BigDecimal discountAmount,
        BigDecimal totalAmount,

        List<CreateOrderItemRequest> orderItems
) {}
