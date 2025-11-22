package com.rgiftings.Backend.DTO;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        Long userId,              // null for guest
        String guestEmail,        // required for guest
        String guestPhone,        // required for guest
        List<OrderItemRequest> items,
        BigDecimal totalPrice
    )
{
}
