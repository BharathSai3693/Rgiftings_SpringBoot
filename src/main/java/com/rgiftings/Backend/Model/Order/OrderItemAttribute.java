package com.rgiftings.Backend.Model.Order;

import com.rgiftings.Backend.Model.Product.ProductAttribute;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class OrderItemAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_item_id",nullable = false)
    private OrderItem orderItem;

    private Long attributeTypeId; //Analytical purpose
    private String attributeTypeName;
    private String productAttributeLabel; // Product Attribute Label (Frame Size)

    @Builder.Default
    @OneToMany(mappedBy = "orderItemAttribute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemAttributeValue> selectedAttributeValues = new ArrayList<>();




}
