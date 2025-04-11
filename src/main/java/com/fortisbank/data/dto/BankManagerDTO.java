package com.fortisbank.data.dto;

public record BankManagerDTO(
        String userId,
        String firstName,
        String lastName,
        String email,
        String hashedPassword,
        String pinHash,
        String role // Should be "MANAGER"
) {}
