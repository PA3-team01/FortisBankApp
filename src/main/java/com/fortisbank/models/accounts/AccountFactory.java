package com.fortisbank.models.accounts;
import com.fortisbank.models.Customer;
import java.math.BigDecimal;
import java.util.Date;

public class AccountFactory {
    public static Account createAccount(AccountType type, String accountNumber, Customer customer, Date openedDate, BigDecimal initialBalance, Object... extraParams) {
        switch (type) {
            case CHECKING:
                return new CheckingAccount(accountNumber, customer, openedDate, initialBalance);
            case SAVINGS:
                return new SavingsAccount(accountNumber, customer, openedDate, initialBalance, (BigDecimal) extraParams[0]);
            case CREDIT:
                return new CreditAccount(accountNumber, customer, openedDate, (BigDecimal) extraParams[0], (BigDecimal) extraParams[1]);
            case CURRENCY:
                return new CurrencyAccount(accountNumber, customer, openedDate, initialBalance, (String) extraParams[0], (BigDecimal) extraParams[1]);
            default:
                throw new IllegalArgumentException("Unsupported account type.");
        }
    }
}
