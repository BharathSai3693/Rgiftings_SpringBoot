package com.rgiftings.Backend.DTO.User.Create;

public record UserCreateRequest(
        String firebaseUid,
        String name,
        String email,
        String phone
) {
}
