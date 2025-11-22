package com.rgiftings.Backend.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long userId,
        String guestEmail,
        String guestPhone,
        List<OrderItemResponse> items,
        BigDecimal totalPrice,
        String status,
        LocalDateTime orderDate

) {
}
