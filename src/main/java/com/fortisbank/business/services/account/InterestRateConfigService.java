package com.fortisbank.business.services.account;

import com.fortisbank.models.accounts.AccountType;
import com.fortisbank.models.others.InterestRate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Singleton service to load and manage interest rates from a config file.
 */
public class InterestRateConfigService {
    private static final Path CONFIG_PATH = Paths.get("config/interest_rates.json");
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static InterestRateConfigService instance;

    private List<InterestRate> rates;

    // Private constructor for singleton
    private InterestRateConfigService() {
        loadRates();
    }

    public static synchronized InterestRateConfigService getInstance() {
        if (instance == null) {
            instance = new InterestRateConfigService();
        }
        return instance;
    }

    public void loadRates() {
        try {
            rates = Files.exists(CONFIG_PATH)
                    ? Arrays.asList(mapper.readValue(CONFIG_PATH.toFile(), InterestRate[].class))
                    : new ArrayList<>();
        } catch (IOException e) {
            rates = new ArrayList<>();
            e.printStackTrace();
        }
    }

    public void saveRates() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(CONFIG_PATH.toFile(), rates);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BigDecimal getRate(AccountType type) {
        return rates.stream()
                .filter(r -> r.getAccountType() == type)
                .map(InterestRate::getRate)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    public void updateRate(AccountType type, BigDecimal newRate) {
        InterestRate existing = rates.stream()
                .filter(r -> r.getAccountType() == type)
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setRate(newRate);
            existing.setLastUpdated(LocalDateTime.now());
        } else {
            rates.add(new InterestRate(type, newRate, LocalDateTime.now()));
        }

        saveRates();
    }

    public List<InterestRate> getAllRates() {
        return rates;
    }
}
