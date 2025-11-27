package com.rgiftings.Backend.Controller;

import com.rgiftings.Backend.DTO.Order.CREATE.OrderRequest;
import com.rgiftings.Backend.DTO.Order.RETRIEVE.OrderResponse;
import com.rgiftings.Backend.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order/place")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest){
        String orderResponse = orderService.placeOrder(orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<List<OrderResponse>> retrieveOrderbyUserId(@PathVariable Long userId){
        List<OrderResponse> orderResponseList = orderService.retrieveOrderbyUserId(userId);
        return  new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }


}
