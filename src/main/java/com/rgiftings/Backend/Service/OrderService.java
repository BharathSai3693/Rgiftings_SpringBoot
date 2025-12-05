package com.rgiftings.Backend.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.rgiftings.Backend.DTO.Order.CREATE.*;
import com.rgiftings.Backend.DTO.Order.RETRIEVE.OrderItemAttributeResponse;
import com.rgiftings.Backend.DTO.Order.RETRIEVE.OrderItemAttributeValueResponse;
import com.rgiftings.Backend.DTO.Order.RETRIEVE.OrderItemResponse;
import com.rgiftings.Backend.DTO.Order.RETRIEVE.OrderResponse;
import com.rgiftings.Backend.Model.Attribute.AttributeType;
import com.rgiftings.Backend.Model.Attribute.AttributeValue;
import com.rgiftings.Backend.Model.Order.Order;
import com.rgiftings.Backend.Model.Order.OrderItem;
import com.rgiftings.Backend.Model.Order.OrderItemAttribute;
import com.rgiftings.Backend.Model.Order.OrderItemAttributeValue;
import com.rgiftings.Backend.Model.Product.Product;
import com.rgiftings.Backend.Model.Product.ProductAttribute;
import com.rgiftings.Backend.Model.Product.ProductAttributeValue;
import com.rgiftings.Backend.Model.User.User;
import com.rgiftings.Backend.Repository.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeValueRepository attributeValueRepository;

    @Autowired
    private UserRepository userRepository;

    // CREATE/PLACE ORDER
    //Main Order Placing Function
    public String placeOrder(CreateOrderRequest orderRequest) {
        Order newOrder = Order.builder()
                .userId(orderRequest.userId())
                .guestEmail(orderRequest.guestEmail())
                .guestPhone(orderRequest.guestPhone())
                .addressId(orderRequest.addressId())
                .status("PENDING")
                .orderCreatedAt(LocalDateTime.now())
                .orderUpdatedAt(LocalDateTime.now())
                .build();

        BigDecimal subTotalAmount =BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        List<OrderItem> newOrderItems = new ArrayList<>();
        for(CreateOrderItemRequest orderItemRequest : orderRequest.orderItems()){

            Product product = productRepository.findById(orderItemRequest.productId())
                    .orElseThrow(() -> new RuntimeException("Product Not Found"));

            OrderItem orderItem = OrderItem.builder()
                    .order(newOrder)
                    .product(product)
                    .productName(product.getName())
                    .quantity(orderItemRequest.quantity())
                    .basePrice(product.getBasePrice())
                    .taxRate(product.getTaxRate())
                    .build();

            BigDecimal totalExtraPricePerUnit = BigDecimal.ZERO;
            List<OrderItemAttribute> newOrderItemAttributes = new ArrayList<>();
            for(CreateOrderItemAttributeRequest orderItemAttributeRequest : orderItemRequest.orderItemAttributes()){
                AttributeType attributeType = attributeRepository.findById(orderItemAttributeRequest.attributeTypeId())
                        .orElseThrow(() -> new RuntimeException("Attribute Type Not Found"));

                ProductAttribute productAttribute = product.getProductAttributes().stream()
                        .filter(prodAttr -> orderItemAttributeRequest.productAttributeId().equals(prodAttr.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Product Attribute Not found"));

                OrderItemAttribute orderItemAttribute = OrderItemAttribute.builder()
                        .orderItem(orderItem)
                        .attributeTypeId(attributeType.getId())
                        .attributeTypeName(attributeType.getName())
                        .productAttributeLabel(productAttribute.getProductAttributeLabel())
                        .build();

                List<OrderItemAttributeValue> newOrderItemAttributeValues = new ArrayList<>();
                for(CreateOrderItemAttributeValueRequest orderItemAttributeValueRequest : orderItemAttributeRequest.selectedAttributeValues()){

                    // Handle TEXT/FILE input types differently
                    if (orderItemAttributeValueRequest.attributeValueId() != null) {
                        // Regular attribute value handling
                        AttributeValue attributeValue = attributeValueRepository.findById(orderItemAttributeValueRequest.attributeValueId())
                                .orElseThrow(() -> new RuntimeException("Attribute Value Not Found"));

                                        ProductAttributeValue productAttributeValue =
                                        productAttribute.getProductAttributeValues().stream()
                                                .filter(prodAttrVal -> orderItemAttributeValueRequest.productAttributeValueId().equals(prodAttrVal.getId()))
                                                .findFirst()
                                                .orElseThrow(() -> new RuntimeException("Prod Attr Value Not found"));

                                                        OrderItemAttributeValue orderItemAttributeValue =
                                                        OrderItemAttributeValue.builder()
                                                                .orderItemAttribute(orderItemAttribute)
                                                                .attributeValueId(attributeValue.getId())
                                                                .attributeValueValue(attributeValue.getValue())
                                                                .productAttributeValueId(productAttributeValue.getId())
                                                                .extraPrice(productAttributeValue.getExtraPrice())
                                                                .customText(orderItemAttributeValueRequest.customText())
                                                                .fileUrl(orderItemAttributeValueRequest.fileUrl())
                                                                .build();

                        if(productAttributeValue.getExtraPrice() != null){
                            totalExtraPricePerUnit =
                                    totalExtraPricePerUnit.add(productAttributeValue.getExtraPrice());

                        }
                        newOrderItemAttributeValues.add(orderItemAttributeValue);
                    } else {
                        // For TEXT/FILE inputs - no predefined values
                        OrderItemAttributeValue orderItemAttributeValue =
                                OrderItemAttributeValue.builder()
                                        .orderItemAttribute(orderItemAttribute)
                                        .attributeValueId(null)
                                        .attributeValueValue(null)
                                        .productAttributeValueId(null)
                                        .extraPrice(BigDecimal.ZERO)
                                        .customText(orderItemAttributeValueRequest.customText())
                                        .fileUrl(orderItemAttributeValueRequest.fileUrl())
                                        .build();
                        newOrderItemAttributeValues.add(orderItemAttributeValue);
                    }




                }

                orderItemAttribute.setSelectedAttributeValues(newOrderItemAttributeValues);
                newOrderItemAttributes.add(orderItemAttribute);
            }

            orderItem.setLineExtraPrice(totalExtraPricePerUnit.multiply(BigDecimal.valueOf(orderItemRequest.quantity())));
            BigDecimal lineSubTotalPrice = (product.getBasePrice().add(totalExtraPricePerUnit)).multiply(BigDecimal.valueOf(orderItemRequest.quantity()));
            BigDecimal lineTax = lineSubTotalPrice.multiply(product.getTaxRate());
            orderItem.setLineTax(lineTax);
            orderItem.setLineTotalPrice(lineSubTotalPrice.add(orderItem.getLineTax()));
            orderItem.setOrderItemAttributes(newOrderItemAttributes);
            newOrderItems.add(orderItem);

            //
            subTotalAmount = subTotalAmount.add(lineSubTotalPrice);
            taxAmount = taxAmount.add(lineTax);
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        totalAmount = subTotalAmount.add(taxAmount);

        newOrder.setSubTotalAmount(subTotalAmount);
        newOrder.setTaxAmount(taxAmount);
        newOrder.setTotalAmount(totalAmount);
        newOrder.setOrderItems(newOrderItems);

        orderRepository.save(newOrder);

        return "ORDER PLACED";
    }


    //FETCH ORDERS
    //Main Order Retrieving by userId function
    public List<OrderResponse> retrieveOrderbyUserId(String authHeader, Long userId) throws FirebaseAuthException {
        // STEP 1: Fetch all orders for the user
//        List<Order> orders = orderRepository.findByUserIdOrderByOrderCreatedAtDesc(userId);
        String idToken = authHeader.replace("Bearer ", "");
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String firebaseUid = decodedToken.getUid();
        User existingUser = userRepository.findByFirebaseUid(firebaseUid).orElseThrow(() -> new RuntimeException("User not Found to fetch Orders"));
        List<Order> orders = orderRepository.findByUserIdOrderByOrderCreatedAtDesc(existingUser.getId());

        List<OrderResponse> orderResponseList = new ArrayList<>();

        // STEP 2: Loop through each order
        for(Order order : orders){

            List<OrderItemResponse> orderItemResponseList = new ArrayList<>();

            // STEP 3: Loop through each order item
            for(OrderItem orderItem : order.getOrderItems()){

                List<OrderItemAttributeResponse> orderItemAttributeResponseList = new ArrayList<>();

                // STEP 4: Loop through each order item attribute
                for(OrderItemAttribute orderItemAttribute : orderItem.getOrderItemAttributes()){

                    List<OrderItemAttributeValueResponse> orderItemAttributeValueResponseList = new ArrayList<>();

                    // STEP 5: Loop through each selected attribute value
                    for(OrderItemAttributeValue orderItemAttributeValue : orderItemAttribute.getSelectedAttributeValues()){

                        OrderItemAttributeValueResponse orderItemAttributeValueResponse = OrderItemAttributeValueResponse.builder()
                                .orderItemAttributeValueId(orderItemAttributeValue.getId())
                                .attributeValueId(orderItemAttributeValue.getAttributeValueId())
                                .attributeValueValue(orderItemAttributeValue.getAttributeValueValue())
                                .productAttributeValueId(orderItemAttributeValue.getProductAttributeValueId())
                                .extraPrice(orderItemAttributeValue.getExtraPrice())
                                .customText(orderItemAttributeValue.getCustomText())
                                .fileUrl(orderItemAttributeValue.getFileUrl())
                                .build();

                        orderItemAttributeValueResponseList.add(orderItemAttributeValueResponse);
                    }

                    OrderItemAttributeResponse orderItemAttributeResponse = OrderItemAttributeResponse.builder()
                            .orderItemAttributeId(orderItemAttribute.getId())
                            .attributeTypeId(orderItemAttribute.getAttributeTypeId())
                            .attributeTypeName(orderItemAttribute.getAttributeTypeName())
                            .productAttributeLabel(orderItemAttribute.getProductAttributeLabel())
                            .orderItemAttributeValueResponseList(orderItemAttributeValueResponseList)
                            .build();

                    orderItemAttributeResponseList.add(orderItemAttributeResponse);
                }

                // Get product images from Product entity (primary first)
                Product product = orderItem.getProduct();
                List<String> productImageUrls = new ArrayList<>();
                if (product.getImages() != null) {
                    product.getImages().stream()
                            .sorted((a, b) -> Boolean.compare(Boolean.TRUE.equals(b.getIsPrimary()), Boolean.TRUE.equals(a.getIsPrimary())))
                            .forEach(image -> productImageUrls.add(image.getImageUrl()));
                }

                OrderItemResponse orderItemResponse = OrderItemResponse.builder()
                        .orderItemId(orderItem.getId())
                        .productId(product.getId())
                        .productName(orderItem.getProductName())
                        .productImageUrls(productImageUrls)
                        .quantity(orderItem.getQuantity())
                        .basePrice(orderItem.getBasePrice())
                        .taxRate(orderItem.getTaxRate())
                        .lineExtraPrice(orderItem.getLineExtraPrice())
                        .lineTax(orderItem.getLineTax())
                        .lineTotalPrice(orderItem.getLineTotalPrice())
                        .orderItemAttributeResponseList(orderItemAttributeResponseList)
                        .build();

                orderItemResponseList.add(orderItemResponse);
            }

            OrderResponse orderResponse = OrderResponse.builder()
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .guestEmail(order.getGuestEmail())
                    .guestPhone(order.getGuestPhone())
                    .addressId(order.getAddressId())
                    .orderItems(orderItemResponseList)
                    .status(order.getStatus())
                    .orderCreatedAt(order.getOrderCreatedAt())
                    .orderUpdatedAt(order.getOrderUpdatedAt())
                    .totalPrice(order.getSubTotalAmount())
                    .totalTax(order.getTaxAmount())
                    .grandTotal(order.getTotalAmount())
                    .build();

            orderResponseList.add(orderResponse);
        }

        return orderResponseList;
    }


    //Main Order Retrieving by userId function
    public List<OrderResponse> retrieveAllOrders(String authHeader) throws FirebaseAuthException {
        // STEP 1: Fetch all orders for the user
        List<Order> orders = orderRepository.findAll();

        List<OrderResponse> orderResponseList = new ArrayList<>();

        // STEP 2: Loop through each order
        for(Order order : orders){

            List<OrderItemResponse> orderItemResponseList = new ArrayList<>();

            // STEP 3: Loop through each order item
            for(OrderItem orderItem : order.getOrderItems()){

                List<OrderItemAttributeResponse> orderItemAttributeResponseList = new ArrayList<>();

                // STEP 4: Loop through each order item attribute
                for(OrderItemAttribute orderItemAttribute : orderItem.getOrderItemAttributes()){

                    List<OrderItemAttributeValueResponse> orderItemAttributeValueResponseList = new ArrayList<>();

                    // STEP 5: Loop through each selected attribute value
                    for(OrderItemAttributeValue orderItemAttributeValue : orderItemAttribute.getSelectedAttributeValues()){

                        OrderItemAttributeValueResponse orderItemAttributeValueResponse = OrderItemAttributeValueResponse.builder()
                                .orderItemAttributeValueId(orderItemAttributeValue.getId())
                                .attributeValueId(orderItemAttributeValue.getAttributeValueId())
                                .attributeValueValue(orderItemAttributeValue.getAttributeValueValue())
                                .productAttributeValueId(orderItemAttributeValue.getProductAttributeValueId())
                                .extraPrice(orderItemAttributeValue.getExtraPrice())
                                .customText(orderItemAttributeValue.getCustomText())
                                .fileUrl(orderItemAttributeValue.getFileUrl())
                                .build();

                        orderItemAttributeValueResponseList.add(orderItemAttributeValueResponse);
                    }

                    OrderItemAttributeResponse orderItemAttributeResponse = OrderItemAttributeResponse.builder()
                            .orderItemAttributeId(orderItemAttribute.getId())
                            .attributeTypeId(orderItemAttribute.getAttributeTypeId())
                            .attributeTypeName(orderItemAttribute.getAttributeTypeName())
                            .productAttributeLabel(orderItemAttribute.getProductAttributeLabel())
                            .orderItemAttributeValueResponseList(orderItemAttributeValueResponseList)
                            .build();

                    orderItemAttributeResponseList.add(orderItemAttributeResponse);
                }

                // Get product images from Product entity (primary first)
                Product product = orderItem.getProduct();
                List<String> productImageUrls = new ArrayList<>();
                if (product.getImages() != null) {
                    product.getImages().stream()
                            .sorted((a, b) -> Boolean.compare(Boolean.TRUE.equals(b.getIsPrimary()), Boolean.TRUE.equals(a.getIsPrimary())))
                            .forEach(image -> productImageUrls.add(image.getImageUrl()));
                }

                OrderItemResponse orderItemResponse = OrderItemResponse.builder()
                        .orderItemId(orderItem.getId())
                        .productId(product.getId())
                        .productName(orderItem.getProductName())
                        .productImageUrls(productImageUrls)
                        .quantity(orderItem.getQuantity())
                        .basePrice(orderItem.getBasePrice())
                        .taxRate(orderItem.getTaxRate())
                        .lineExtraPrice(orderItem.getLineExtraPrice())
                        .lineTax(orderItem.getLineTax())
                        .lineTotalPrice(orderItem.getLineTotalPrice())
                        .orderItemAttributeResponseList(orderItemAttributeResponseList)
                        .build();

                orderItemResponseList.add(orderItemResponse);
            }

            OrderResponse orderResponse = OrderResponse.builder()
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .guestEmail(order.getGuestEmail())
                    .guestPhone(order.getGuestPhone())
                    .addressId(order.getAddressId())
                    .orderItems(orderItemResponseList)
                    .status(order.getStatus())
                    .orderCreatedAt(order.getOrderCreatedAt())
                    .orderUpdatedAt(order.getOrderUpdatedAt())
                    .totalPrice(order.getSubTotalAmount())
                    .totalTax(order.getTaxAmount())
                    .grandTotal(order.getTotalAmount())
                    .build();

            orderResponseList.add(orderResponse);
        }

        return orderResponseList;
    }



}














