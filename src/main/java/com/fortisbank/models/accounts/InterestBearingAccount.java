package com.fortisbank.models.accounts;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface representing an interest-bearing account.
 */
public interface InterestBearingAccount {

    /**
     * Returns the interest rate applied to the account.
     * The interest rate can represent an annual or monthly rate.
     *
     * @return the interest rate
     */
    BigDecimal getInterestRate();

    /**
     * Returns the date when interest was last applied to the account.
     *
     * @return the last interest applied date
     */
    LocalDate getLastInterestApplied();

    /**
     * Sets the date when interest was last applied to the account.
     *
     * @param date the date to set
     */
    void setLastInterestApplied(LocalDate date);

    /**
     * Checks if the account is eligible for interest calculation.
     *
     * @return true if eligible for interest calculation, false otherwise
     */
    boolean isEligibleForInterestCalculation();
}