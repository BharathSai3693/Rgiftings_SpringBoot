package com.rgiftings.Backend.DTO.Product.Update;

import java.util.List;

public record UpdateProductAttributeRequest(
        Long id,
        Long attributeTypeId,
        String productAttributeLabel,
        List<UpdateProductAttributeValueRequest> productAttributeValues
) {
}
