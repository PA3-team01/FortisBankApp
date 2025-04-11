package com.fortisbank.data.dto;

public record CustomerDTO(
        String userId,
        String firstName,
        String lastName,
        String email,
        String hashedPassword,
        String pinHash,
        String role, // Should be "CUSTOMER"
        String phoneNumber
) {}
