package com.rgiftings.Backend.Model.Order;

import com.rgiftings.Backend.Model.Product.ProductAttribute;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JoinColumn(nullable = false)
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ProductAttribute productAttribute;

    @OneToMany(mappedBy = "orderItemAttribute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemAttributeValue> selectedValues;




}
