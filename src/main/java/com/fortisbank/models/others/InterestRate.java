package com.fortisbank.models.others;

import com.fortisbank.models.accounts.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InterestRate {

    private AccountType accountType;
    private BigDecimal rate;
    private LocalDateTime lastUpdated;

    public InterestRate() {
        // Default constructor for Jackson
    }

    public InterestRate(AccountType accountType, BigDecimal rate, LocalDateTime lastUpdated) {
        this.accountType = accountType;
        this.rate = rate;
        this.lastUpdated = lastUpdated;
    }

    @JsonProperty
    public AccountType getAccountType() {
        return accountType;
    }

    @JsonProperty
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    @JsonProperty
    public BigDecimal getRate() {
        return rate;
    }

    @JsonProperty
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @JsonProperty
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    @JsonProperty
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
