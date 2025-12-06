package com.rgiftings.Backend.Controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.rgiftings.Backend.DTO.Order.CREATE.CreateOrderRequest;
import com.rgiftings.Backend.DTO.Order.RETRIEVE.OrderResponse;
import com.rgiftings.Backend.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order/place")
    public ResponseEntity<String> placeOrder(@RequestBody CreateOrderRequest orderRequest){
        String orderResponse = orderService.placeOrder(orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<List<OrderResponse>> retrieveOrderbyUserId(@RequestHeader("Authorization") String authHeader, @PathVariable Long userId) throws FirebaseAuthException {
        List<OrderResponse> orderResponseList = orderService.retrieveOrderbyUserId(authHeader, userId);
        return  new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/orders")
    public ResponseEntity<List<OrderResponse>> retrieveOrderbyUserId(@RequestHeader("Authorization") String authHeader) throws FirebaseAuthException {
        System.out.println("Called Admin Orders");
        List<OrderResponse> orderResponseList = orderService.retrieveAllOrders(authHeader);
        return  new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }


}
