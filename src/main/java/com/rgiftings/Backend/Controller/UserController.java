package com.rgiftings.Backend.Controller;


import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.rgiftings.Backend.DTO.User.Create.UserCreateRequest;
import com.rgiftings.Backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody UserCreateRequest userCreateRequest) throws FirebaseAuthException {

        ResponseEntity<?> user = userService.registerUser(authHeader,userCreateRequest);
        return user;

    }

    @PostMapping("/login")
    public ResponseEntity<?> registerUser(@RequestHeader("Authorization") String authHeader) throws FirebaseAuthException {

        ResponseEntity<?> user = userService.loginUser(authHeader);
        return user;

    }



}
