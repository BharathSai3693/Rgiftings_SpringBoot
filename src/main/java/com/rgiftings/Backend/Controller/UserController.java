package com.rgiftings.Backend.Controller;


import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.rgiftings.Backend.DTO.User.Create.UserCreateRequest;
import com.rgiftings.Backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        ResponseEntity<?> user;
        user = userService.registerUser(authHeader,userCreateRequest);
        return user;
    }

    @PostMapping("/adminregister")
    public ResponseEntity<?> registerAdmin(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody UserCreateRequest userCreateRequest) throws FirebaseAuthException {
        ResponseEntity<?> admin;
        admin = userService.registerAdmin(authHeader,userCreateRequest);
        return admin;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestHeader("Authorization") String authHeader) throws FirebaseAuthException {
        ResponseEntity<?> user;
        user = userService.loginUser(authHeader);
        return user;

    }

    @PostMapping("/adminlogin")
    public ResponseEntity<?> loginAdmin(@RequestHeader("Authorization") String authHeader) throws FirebaseAuthException {
        ResponseEntity<?> user;
        user = userService.loginAdmin(authHeader);
        return user;

    }



}
