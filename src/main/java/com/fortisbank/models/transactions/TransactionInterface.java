package com.fortisbank.models.transactions;

public interface TransactionInterface {
    void processTransaction();  // Core method every transaction must implement
    void recordTransaction();   // Saves transaction in account history
}
