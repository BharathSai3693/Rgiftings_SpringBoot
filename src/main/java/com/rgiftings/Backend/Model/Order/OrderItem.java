package com.rgiftings.Backend.Model.Order;

import com.rgiftings.Backend.Model.Product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String productName;      // copy of product.getName(), so that future product edits won't spoil

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal basePrice;
    private BigDecimal taxRate; // tax rate at that time , ex:18%
    private BigDecimal lineExtraPrice; // total extra price per item
    private BigDecimal lineTax;
    private BigDecimal lineTotalPrice; //no tax :(Base price + extra price)*quantity

    @Builder.Default
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemAttribute> orderItemAttributes = new ArrayList<>();
}
