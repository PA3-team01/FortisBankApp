package com.fortisbank.data.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountDTO(
        String accountId,
        String customerId,
        String accountType,
        LocalDate openedDate,
        boolean isActive,
        BigDecimal availableBalance,
        BigDecimal creditLimit
) {}
