package com.rgiftings.Backend.Model.Order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Long userId;
    private String guestEmail;
    private String guestPhone;
    private Long addressId;

    private String status;
    private LocalDateTime orderCreatedAt;
    private LocalDateTime orderUpdatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    private BigDecimal totalPrice;
    private BigDecimal totalTax;
    private BigDecimal grandTotal;






}
