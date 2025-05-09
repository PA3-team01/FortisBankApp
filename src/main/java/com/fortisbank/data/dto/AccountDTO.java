package com.fortisbank.data.dto;

import com.fortisbank.business.services.account.InterestRateConfigService;
import com.fortisbank.contracts.models.accounts.*;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.contracts.utils.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * DTO for transferring account data between layers.
 */
public record AccountDTO(
        String accountId,
        String customerId,
        String accountType,
        LocalDate openedDate,
        boolean isActive,
        BigDecimal availableBalance,
        BigDecimal creditLimit,      // CREDIT
        BigDecimal interestRate,     // SAVINGS, CREDIT
        String currencyCode          // CURRENCY
) {
    public AccountDTO {
        if (accountId == null || accountId.isBlank())
            throw new IllegalArgumentException("Account ID cannot be null or blank.");
        if (customerId == null || customerId.isBlank())
            throw new IllegalArgumentException("Customer ID cannot be null or blank.");
        if (accountType == null || accountType.isBlank())
            throw new IllegalArgumentException("Account type cannot be null or blank.");
        if (openedDate == null)
            throw new IllegalArgumentException("Opened date cannot be null.");
        if (availableBalance == null)
            throw new IllegalArgumentException("Available balance cannot be null.");
    }

    public static AccountDTO fromEntity(Account account) {
        return new AccountDTO(
                account.getAccountNumber(),
                account.getCustomer().getUserId(),
                account.getAccountType().name(),
                ValidationUtils.toLocalDate(account.getOpenedDate()),
                account.isActive(),
                account.getAvailableBalance(),
                account instanceof CreditAccount c ? c.getCreditLimit() : null,
                account instanceof SavingsAccount s ? s.getInterestRate() :
                        account instanceof CreditAccount c ? c.getInterestRate() : null,
                account instanceof CurrencyAccount cu ? cu.getCurrencyType() : null
        );
    }

    public Account toEntity(Customer customer) {
        AccountType type = AccountType.valueOf(accountType);
        Date date = ValidationUtils.toDate(openedDate);

        BigDecimal resolvedInterestRate = interestRate;
        if ((type == AccountType.SAVINGS || type == AccountType.CREDIT) && resolvedInterestRate == null) {
            resolvedInterestRate = InterestRateConfigService.getInstance().getRate(type);
        }

        return switch (type) {
            case CHECKING -> AccountFactory.createAccount(type, accountId, customer, date, availableBalance);
            case SAVINGS -> AccountFactory.createAccount(type, accountId, customer, date, availableBalance, resolvedInterestRate);
            case CREDIT -> AccountFactory.createAccount(type, accountId, customer, date, creditLimit, resolvedInterestRate);
            case CURRENCY -> AccountFactory.createAccount(type, accountId, customer, date, availableBalance, currencyCode);
            default -> throw new IllegalStateException("Unsupported account type: " + type);
        };
    }
}
