package com.fortisbank.data.dto;

import com.fortisbank.contracts.models.users.BankManager;
import com.fortisbank.contracts.models.users.Role;

/**
 * DTO for transferring BankManager data between layers.
 */
public record BankManagerDTO(
        String userId,
        String firstName,
        String lastName,
        String email,
        String hashedPassword,
        String pinHash,
        String role
) {
    public BankManagerDTO {
        if (userId == null || userId.isBlank())
            throw new IllegalArgumentException("User ID cannot be null or blank.");
        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("First name cannot be null or blank.");
        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Last name cannot be null or blank.");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email cannot be null or blank.");
        if (hashedPassword == null || hashedPassword.isBlank())
            throw new IllegalArgumentException("Hashed password cannot be null or blank.");
        if (pinHash == null || pinHash.isBlank())
            throw new IllegalArgumentException("PIN hash cannot be null or blank.");
        if (!"MANAGER".equals(role))
            throw new IllegalArgumentException("Role must be 'MANAGER'.");
    }

    /**
     * Converts a BankManager entity to a DTO.
     */
    public static BankManagerDTO fromEntity(BankManager manager) {
        if (manager == null) throw new IllegalArgumentException("BankManager entity cannot be null.");

        return new BankManagerDTO(
                manager.getUserId(),
                manager.getFirstName(),
                manager.getLastName(),
                manager.getEmail(),
                manager.getHashedPassword(),
                manager.getPINHash(),
                manager.getRole().name()
        );
    }

    /**
     * Converts this DTO to a BankManager entity.
     */
    public BankManager toEntity() {
        BankManager manager = new BankManager();
        manager.setUserId(userId);
        manager.setFirstName(firstName);
        manager.setLastName(lastName);
        manager.setEmail(email);
        manager.setHashedPassword(hashedPassword);
        manager.setPINHash(pinHash);
        manager.setRole(Role.MANAGER);
        return manager;
    }
}
