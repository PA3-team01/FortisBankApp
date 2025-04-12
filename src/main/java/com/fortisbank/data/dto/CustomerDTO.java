package com.fortisbank.data.dto;

import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.contracts.models.users.Role;

/**
 * DTO for transferring Customer data between layers.
 */
public record CustomerDTO(
        String userId,
        String firstName,
        String lastName,
        String email,
        String hashedPassword,
        String pinHash,
        String role,
        String phoneNumber
) {
    public CustomerDTO {
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
        if (!"CUSTOMER".equals(role))
            throw new IllegalArgumentException("Role must be 'CUSTOMER'.");
        if (phoneNumber == null || phoneNumber.isBlank())
            throw new IllegalArgumentException("Phone number cannot be null or blank.");
    }

    /**
     * Converts a Customer entity to a DTO.
     */
    public static CustomerDTO fromEntity(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("Customer entity cannot be null.");

        return new CustomerDTO(
                customer.getUserId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getHashedPassword(),
                customer.getPINHash(),
                customer.getRole().name(),
                customer.getPhoneNumber()
        );
    }

    /**
     * Converts this DTO to a Customer entity.
     */
    public Customer toEntity() {
        Customer customer = new Customer();
        customer.setUserId(userId);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setHashedPassword(hashedPassword);
        customer.setPINHash(pinHash);
        customer.setRole(Role.CUSTOMER);
        customer.setPhoneNumber(phoneNumber);
        return customer;
    }
}
