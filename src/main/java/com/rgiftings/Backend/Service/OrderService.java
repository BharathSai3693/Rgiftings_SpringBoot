package com.rgiftings.Backend.Service;

import com.rgiftings.Backend.DTO.Order.CREATE.OrderItemAttributeRequest;
import com.rgiftings.Backend.DTO.Order.CREATE.OrderItemAttributeValueRequest;
import com.rgiftings.Backend.DTO.Order.CREATE.OrderItemRequest;
import com.rgiftings.Backend.DTO.Order.RETRIEVE.OrderItemAttributeResponse;
import com.rgiftings.Backend.DTO.Order.RETRIEVE.OrderItemAttributeValueResponse;
import com.rgiftings.Backend.DTO.Order.RETRIEVE.OrderItemResponse;
import com.rgiftings.Backend.DTO.Order.CREATE.OrderRequest;
import com.rgiftings.Backend.DTO.Order.RETRIEVE.OrderResponse;
import com.rgiftings.Backend.Model.Order.Order;
import com.rgiftings.Backend.Model.Order.OrderItem;
import com.rgiftings.Backend.Model.Order.OrderItemAttribute;
import com.rgiftings.Backend.Model.Order.OrderItemAttributeValue;
import com.rgiftings.Backend.Model.Product.Product;
import com.rgiftings.Backend.Model.Product.ProductAttribute;
import com.rgiftings.Backend.Model.Product.ProductAttributeValue;
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

    // CREATE/PLACE ORDER
    //Main Order Placing Function
    public String placeOrder(OrderRequest orderRequest) {
        Order newOrder = new Order();
        newOrder.setUserId(orderRequest.userId());
        newOrder.setGuestEmail(orderRequest.guestEmail());
        newOrder.setGuestPhone(orderRequest.guestPhone());
        newOrder.setAddressId(Long.valueOf(12345));
        newOrder.setStatus("PLACED");
        newOrder.setOrderCreatedAt(LocalDateTime.now());
        newOrder.setOrderUpdatedAt(LocalDateTime.now());
        AddOrderItems(newOrder, orderRequest);
        CalculateOrderTotals(newOrder);

        orderRepository.save(newOrder);
        return "ORDER PLACED";
//        return orderResponse;

    }

    // Add Order Items to Order
    public void AddOrderItems(Order newOrder, OrderRequest orderRequest){
        List<OrderItemRequest> orderItemRequestList = orderRequest.orderItems();

        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemRequest orderItemRequest : orderItemRequestList){
            OrderItem newOrderItem = new OrderItem();

            Product product = productRepository.findById(orderItemRequest.productId()).orElseThrow(() -> new RuntimeException("Product Not Found"));
            newOrderItem.setProduct(product);
            newOrderItem.setBasePrice(product.getBasePrice());
            newOrderItem.setTaxRate(product.getTaxRate());
            newOrderItem.setQuantity(orderItemRequest.quantity());
            newOrderItem.setOrder(newOrder);
            AddOrderItemAttributes(newOrderItem, orderItemRequest, product);
            CalculateOrderItemPrices(newOrderItem);

            orderItems.add(newOrderItem);
        }
        newOrder.setOrderItems(orderItems);
    }

    // Add OrderItemAttributes to OrderItems
    public void AddOrderItemAttributes(OrderItem newOrderItem, OrderItemRequest orderItemRequest, Product product){
        List<OrderItemAttributeRequest> orderItemAttributeRequestList = orderItemRequest.orderItemAttributeRequestList();

        List<OrderItemAttribute> orderItemAttributeList = new ArrayList<>();
        for(OrderItemAttributeRequest orderItemAttributeRequest : orderItemAttributeRequestList){
            OrderItemAttribute newOrderItemAttribute = new OrderItemAttribute();
            newOrderItemAttribute.setOrderItem(newOrderItem);

            ProductAttribute productAttribute = product.getProductAttributes().stream()
                            .filter(attr -> attr.getId().equals(orderItemAttributeRequest.productAttributeId()))
                                    .findFirst()
                                            .orElseThrow(() -> new RuntimeException("Product Attribute not found id: "+ orderItemAttributeRequest.productAttributeId()));
            newOrderItemAttribute.setProductAttribute(productAttribute);
            AddOrderItemAttributeVales(newOrderItemAttribute, productAttribute, orderItemAttributeRequest);
            orderItemAttributeList.add(newOrderItemAttribute);
        }

        newOrderItem.setOrderItemAttributeList(orderItemAttributeList);
    }

    // Add OrderItemAttributeValues to OrderItemAttributes
    public void AddOrderItemAttributeVales(OrderItemAttribute newOrderItemAttribute, ProductAttribute productAttribute, OrderItemAttributeRequest orderItemAttributeRequest){
        List<OrderItemAttributeValueRequest> orderItemAttributeValueRequestList = orderItemAttributeRequest.orderItemAttributeValueRequestList();

        List<OrderItemAttributeValue> orderItemAttributeValueList = new ArrayList<>();
        for(OrderItemAttributeValueRequest orderItemAttributeValueRequest : orderItemAttributeValueRequestList){
            OrderItemAttributeValue orderItemAttributeValue = new OrderItemAttributeValue();

            ProductAttributeValue productAttributeValue = productAttribute.getProductAttributeValues().stream()
                            .filter(value -> value.getId().equals(orderItemAttributeValueRequest.productAttributeValueId()))
                                    .findFirst()
                                            .orElseThrow(() -> new RuntimeException("Product Attribute Value Not Found with Id: " + orderItemAttributeValueRequest.productAttributeValueId()));

            orderItemAttributeValue.setProductAttributeValue(productAttributeValue);
            orderItemAttributeValue.setExtraPrice(productAttributeValue.getExtraPrice());
            orderItemAttributeValue.setOrderItemAttribute(newOrderItemAttribute);
            orderItemAttributeValue.setCustomText(orderItemAttributeValueRequest.customText());
            orderItemAttributeValue.setFileUrl(orderItemAttributeValueRequest.fileUrl());
            orderItemAttributeValueList.add(orderItemAttributeValue);
        }

        newOrderItemAttribute.setSelectedValues(orderItemAttributeValueList);
    }

    // Calculate Order Item Prices (Price, tax per Item(quantity included))
    public void CalculateOrderItemPrices(OrderItem orderItem){
        BigDecimal lineExtraPrice  = BigDecimal.ZERO;

        if(orderItem.getOrderItemAttributeList() != null){
            for(OrderItemAttribute orderItemAttribute : orderItem.getOrderItemAttributeList()){

                if(orderItemAttribute.getSelectedValues() != null){
                    for(OrderItemAttributeValue orderItemAttributeValue : orderItemAttribute.getSelectedValues()){
                        if(orderItemAttributeValue.getExtraPrice() != null){
                            lineExtraPrice = lineExtraPrice.add(orderItemAttributeValue.getExtraPrice());
                        }
                    }

                }
            }
        }

        orderItem.setLineExtraPrice(lineExtraPrice);
        //Per-Unit final price
        BigDecimal unitPrice = orderItem.getBasePrice().add(lineExtraPrice);

        // Calculate line Total Price (No Tax)
        BigDecimal lineTotalPrice =  unitPrice.multiply(BigDecimal.valueOf(orderItem.getQuantity()));
        orderItem.setLineTotalPrice(lineTotalPrice);

        // Calculate line tax
        if(orderItem.getTaxRate()==null){
            throw new RuntimeException("Tax Rate can't be Null");
        }
        else{
            BigDecimal lineTax = lineTotalPrice.multiply(orderItem.getTaxRate());
            orderItem.setLineTax(lineTax);
        }
    }

    // Calculate Order Grand Total
    public void CalculateOrderTotals(Order order){
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;

        if(order.getOrderItems()!= null){
            for(OrderItem orderItem : order.getOrderItems()){
                totalPrice = totalPrice.add(orderItem.getLineTotalPrice());
                totalTax = totalTax.add(orderItem.getLineTax());
            }

        }

        order.setTotalPrice(totalPrice);
        order.setTotalTax(totalTax);
        order.setGrandTotal(totalPrice.add(totalTax));
    }


    //FETCH ORDERS
    //Main Order Retrieving by userId function
    public List<OrderResponse> retrieveOrderbyUserId(Long userId) {

//        List<Order> orders =  orderRepository.findByUserIdOrderByOrderCreatedAtDesc(userId);
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> orderResponseList = new ArrayList<>();
        for(Order order : orders){


            List<OrderItemResponse> orderItemResponseList = new ArrayList<>();
            for(OrderItem orderItem : order.getOrderItems()){

                List<OrderItemAttributeResponse> orderItemAttributeResponseList = new ArrayList<>();
                for(OrderItemAttribute orderItemAttribute : orderItem.getOrderItemAttributeList()){

                    List<OrderItemAttributeValueResponse> orderItemAttributeValueResponseList = new ArrayList<>();
                    for(OrderItemAttributeValue orderItemAttributeValue : orderItemAttribute.getSelectedValues()){
                        OrderItemAttributeValueResponse orderItemAttributeValueResponse = OrderItemAttributeValueResponse.builder()
                                .ProductAttributeValue_value(orderItemAttributeValue.getProductAttributeValue().getAttributeValue().getValue())
                                .extraPrice(orderItemAttributeValue.getExtraPrice())
                                .customText(orderItemAttributeValue.getCustomText())
                                .fileUrl(orderItemAttributeValue.getFileUrl())
                                .build();

                        orderItemAttributeValueResponseList.add(orderItemAttributeValueResponse);
                    }


                    OrderItemAttributeResponse orderItemAttributeResponse = OrderItemAttributeResponse.builder()
                            .productAttributeId(orderItemAttribute.getProductAttribute().getId())
                            .productAttributeLabel(orderItemAttribute.getProductAttribute().getProductAttributeLabel())
                            .orderItemAttributeValueResponseList(orderItemAttributeValueResponseList)
                            .build();
                    orderItemAttributeResponseList.add(orderItemAttributeResponse);
                }




                OrderItemResponse orderItemResponse = OrderItemResponse.builder()
                        .quantity(orderItem.getQuantity())
                        .basePrice(orderItem.getBasePrice())
                        .productId(orderItem.getProduct().getId())
                        .productImageURL(orderItem.getProduct().getImageUrl())
                        .productName(orderItem.getProduct().getName())
                        .lineExtraPrice(orderItem.getLineExtraPrice())
                        .lineTotalPrice(orderItem.getLineTotalPrice())
                        .lineTax(orderItem.getLineTax())
                        .orderItemAttributeResponseList(orderItemAttributeResponseList)
                        .build();


                orderItemResponseList.add(orderItemResponse);
            }

            OrderResponse orderResponse = OrderResponse.builder()
                    .orderId(order.getOrderId()).userId(order.getUserId())
                    .guestEmail(order.getGuestEmail()).guestPhone(order.getGuestPhone())
                    .addressId(order.getAddressId()).status(order.getStatus())
                    .orderUpdatedAt(order.getOrderUpdatedAt()).orderCreatedAt(order.getOrderCreatedAt())
                    .totalPrice(order.getTotalPrice()).totalTax(order.getTotalTax())
                    .grandTotal(order.getGrandTotal()).orderItems(orderItemResponseList)
                    .build();

            orderResponseList.add(orderResponse);
        }



        return orderResponseList;
    }




}















