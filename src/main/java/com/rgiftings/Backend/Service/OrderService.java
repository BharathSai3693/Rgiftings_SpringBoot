package com.rgiftings.Backend.Service;

import com.rgiftings.Backend.DTO.Order.OrderItemRequest;
import com.rgiftings.Backend.DTO.Order.OrderItemResponse;
import com.rgiftings.Backend.DTO.Order.OrderRequest;
import com.rgiftings.Backend.DTO.Order.OrderResponse;
import com.rgiftings.Backend.Model.Order;
import com.rgiftings.Backend.Model.OrderItem;
import com.rgiftings.Backend.Model.Product;
import com.rgiftings.Backend.Repository.OrderRepository;
import com.rgiftings.Backend.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setUserId(orderRequest.userId());
        order.setGuestEmail(orderRequest.guestEmail());
        order.setGuestPhone(orderRequest.guestPhone());
        order.setAddressId(Long.valueOf(12345));
        order.setStatus("PLACED");
        order.setOrderCreatedAt(LocalDateTime.now());
        order.setOrderUpdatedAt(LocalDateTime.now());

        BigDecimal grandTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemRequest orderItemRequest : orderRequest.items()){
            Product product = productRepository.findById(orderItemRequest.productId()).orElseThrow(() -> new RuntimeException("Product Not Found"));
            product.setStock(product.getStock() - orderItemRequest.quantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .quantity(orderItemRequest.quantity())
                    .unitPrice(orderItemRequest.itemPrice())
                    .lineTotalPrice(orderItemRequest.itemPrice().multiply(BigDecimal.valueOf(orderItemRequest.quantity())))
                    .order(order)
                    .product(product)
                    .build();
            orderItems.add(orderItem);

            grandTotal = grandTotal.add(orderItem.getLineTotalPrice());
        }
        order.setOrderItems(orderItems);
        order.setTotalPrice(grandTotal);
        Order savedOrder = orderRepository.save(order);


        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for(OrderItem orderItem : savedOrder.getOrderItems()){
                OrderItemResponse orderItemResponse = new OrderItemResponse(
                        orderItem.getProduct().getName(),
                        orderItem.getQuantity(),
                        orderItem.getLineTotalPrice()
                );
                orderItemResponses.add(orderItemResponse);
        }


        OrderResponse orderResponse = new OrderResponse(
                savedOrder.getOrderId(),
                savedOrder.getUserId(),
                savedOrder.getGuestEmail(),
                savedOrder.getGuestPhone(),
                orderItemResponses,
                savedOrder.getTotalPrice(),
                savedOrder.getStatus(),
                savedOrder.getOrderCreatedAt()
        );
        return orderResponse;


    }
}
