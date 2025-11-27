package com.rgiftings.Backend.Model.Order;

import com.rgiftings.Backend.Model.Product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    private Integer quantity;
    private BigDecimal basePrice;
    private BigDecimal taxRate;
    @ManyToOne
    private Order order;
    @ManyToOne
    private Product product;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemAttribute> orderItemAttributeList;

    private BigDecimal lineExtraPrice; // total extra price per item
    private BigDecimal lineTotalPrice; //no tax :(Base price + extra price)*quantity
    private BigDecimal lineTax;


}
