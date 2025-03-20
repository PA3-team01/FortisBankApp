package com.fortisbank.models.accounts;

import java.math.BigDecimal;

public interface AccountInterface {
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount);
    void transfer(Account targetAccount, BigDecimal amount);
}
