package com.fortisbank.models.accounts;
import com.fortisbank.models.Customer;
import java.math.BigDecimal;
import java.util.Date;

public class CheckingAccount extends Account {
    private static final int FREE_TRANSACTION_LIMIT = 2;
    private static final BigDecimal TRANSACTION_FEE = new BigDecimal("5.00");
    private int transactionCount = 0; // TODO: handle this better in database maybe? (check transactions count per month)

    public CheckingAccount(String accountNumber, Customer customer, Date openedDate, BigDecimal initialBalance) {
        super(accountNumber, customer, AccountType.CHECKING, openedDate, initialBalance);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        super.withdraw(amount);
        applyTransactionFee();
    }

    @Override
    public void transfer(Account targetAccount, BigDecimal amount) {
        super.transfer(targetAccount, amount);
        applyTransactionFee();
    }

    private void applyTransactionFee() {
        if (transactionCount >= FREE_TRANSACTION_LIMIT) {
            applyFees(TRANSACTION_FEE, "Transaction fee after " + FREE_TRANSACTION_LIMIT + " free transactions.");
        }
        transactionCount++;
    }
}
