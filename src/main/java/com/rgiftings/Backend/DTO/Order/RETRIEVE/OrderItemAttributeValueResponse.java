package com.rgiftings.Backend.DTO.Order.RETRIEVE;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemAttributeValueResponse(

        String ProductAttributeValue_value,
        BigDecimal extraPrice,
        String customText,
        String fileUrl
) {

}
