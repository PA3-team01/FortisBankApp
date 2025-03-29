package com.fortisbank.models.users;

public abstract class User {
    protected String userId;
    protected String email;
    protected String hashedPassword; // application password
    protected String PINHash; // PIN hash for authentication
    protected String firstName;
    protected String lastName;
    protected Role role;

    public enum Role {
        CUSTOMER,
        MANAGER
    }

    public  String getFullName(){
        return firstName + " " + lastName;
    };
}

