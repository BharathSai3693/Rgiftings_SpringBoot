package com.rgiftings.Backend.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.rgiftings.Backend.DTO.User.Create.UserCreateRequest;
import com.rgiftings.Backend.Model.User.User;
import com.rgiftings.Backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> registerUser(String authHeader, UserCreateRequest userCreateRequest) throws FirebaseAuthException {

        String idToken = authHeader.replace("Bearer ", "");
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String firebaseUid = decodedToken.getUid();

        Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);
        if(existingUser.isPresent()){
            return ResponseEntity.ok(existingUser.get());
        }

        User newUser = User.builder()
                .firebaseUid(firebaseUid)
                .name(userCreateRequest.name())
                .email(userCreateRequest.email())
                .phone(userCreateRequest.phone())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);

    }

    public ResponseEntity<?> loginUser(String authHeader) throws FirebaseAuthException {
        String idToken = authHeader.replace("Bearer ", "");
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String firebaseUid = decodedToken.getUid();

        Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);

        return ResponseEntity.status(HttpStatus.OK).body(existingUser);
    }
}
