package com.stoneridge.backend.dto;

public record AuthResponse(String token, UserDTO user) {}
