package com.rgiftings.Backend.Controller;

import com.rgiftings.Backend.DTO.Order.OrderRequest;
import com.rgiftings.Backend.DTO.Order.OrderResponse;
import com.rgiftings.Backend.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest){
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);

    }



}
