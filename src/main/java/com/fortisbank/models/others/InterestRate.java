package com.fortisbank.models.others;

import com.fortisbank.models.accounts.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Class representing the interest rate for a specific account type.
 */
public class InterestRate {

    /**
     * The type of account the interest rate applies to.
     */
    private AccountType accountType;

    /**
     * The interest rate value.
     */
    private BigDecimal rate;

    /**
     * The date and time when the interest rate was last updated.
     */
    private LocalDateTime lastUpdated;

    /**
     * Default constructor for Jackson.
     */
    public InterestRate() {
        // Default constructor for Jackson
    }

    /**
     * Constructor initializing the interest rate with specified values.
     *
     * @param accountType the type of account
     * @param rate the interest rate value
     * @param lastUpdated the date and time when the interest rate was last updated
     */
    public InterestRate(AccountType accountType, BigDecimal rate, LocalDateTime lastUpdated) {
        this.accountType = accountType;
        this.rate = rate;
        this.lastUpdated = lastUpdated;
    }

    /**
     * Returns the type of account the interest rate applies to.
     *
     * @return the account type
     */
    @JsonProperty
    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * Sets the type of account the interest rate applies to.
     *
     * @param accountType the account type to set
     */
    @JsonProperty
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    /**
     * Returns the interest rate value.
     *
     * @return the interest rate value
     */
    @JsonProperty
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * Sets the interest rate value.
     *
     * @param rate the interest rate value to set
     */
    @JsonProperty
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * Returns the date and time when the interest rate was last updated.
     *
     * @return the last updated date and time
     */
    @JsonProperty
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets the date and time when the interest rate was last updated.
     *
     * @param lastUpdated the last updated date and time to set
     */
    @JsonProperty
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}