package com.fortisbank.models.accounts;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface InterestBearingAccount {
    BigDecimal getInterestRate(); // Can represent annual or monthly rate
    LocalDate getLastInterestApplied();
    void setLastInterestApplied(LocalDate date);
    boolean isEligibleForInterestCalculation();
}
