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
    private OrderItemAttribute orderItemAttribute;

    @ManyToOne()
    @JoinColumn(nullable = false)
    private ProductAttributeValue productAttributeValue;

    private BigDecimal extraPrice;
    private String customText;
    private String fileUrl;


}
