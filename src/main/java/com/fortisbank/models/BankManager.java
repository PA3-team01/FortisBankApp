package com.fortisbank.models;

public class BankManager {
    private String name;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void displayInfo() {
        System.out.println("Bank Manager Name: " + name);
        System.out.println("Bank Manager ID: " + id);
    }
}
