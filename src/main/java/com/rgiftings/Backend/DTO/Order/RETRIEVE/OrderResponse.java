package com.rgiftings.Backend.DTO.Order.RETRIEVE;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(
        Long orderId,
        Long userId,
        String guestEmail,
        String guestPhone,
        Long addressId,
        List<OrderItemResponse> orderItems,
        String status,
        LocalDateTime orderCreatedAt,
        LocalDateTime orderUpdatedAt,
        BigDecimal totalPrice,
        BigDecimal totalTax,
        BigDecimal grandTotal

) {
}
