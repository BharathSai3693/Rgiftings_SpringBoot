package com.rgiftings.Backend.Model.Order;

import com.rgiftings.Backend.Model.Attribute.AttributeValue;
import com.rgiftings.Backend.Model.Product.ProductAttributeValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class OrderItemAttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "order_item_attribute_id", nullable = false)
    private OrderItemAttribute orderItemAttribute;

    private Long attributeValueId;
    private String attributeValueValue; // SMALL, LARGE, RED, YELLOW

    @Column(nullable = true)
    private Long productAttributeValueId; // For Analytical Purpose

    @Column(nullable = true)
    private BigDecimal extraPrice;

    @Builder.Default
    private String customText = null;
    private String fileUrl;

}
