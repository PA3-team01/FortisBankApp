package com.fortisbank.models.accounts;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CurrencyType {
    private static CurrencyType instance;
    private final Map<String, BigDecimal> exchangeRates;

    private CurrencyType() {
        exchangeRates = new HashMap<>();

        // Initialize with some default exchange rates (Base Currency: USD)
        exchangeRates.put("USD", BigDecimal.ONE);
        exchangeRates.put("EUR", new BigDecimal("0.92"));
        exchangeRates.put("CAD", new BigDecimal("1.35"));
        exchangeRates.put("GBP", new BigDecimal("0.78"));
        exchangeRates.put("JPY", new BigDecimal("150.25"));
    }

    public static CurrencyType getInstance() {
        if (instance == null) {
            instance = new CurrencyType();
        }
        return instance;
    }

    public BigDecimal getExchangeRate(String currencyCode) {
        return exchangeRates.getOrDefault(currencyCode.toUpperCase(), BigDecimal.ZERO);
    }

    public void updateExchangeRate(String currencyCode, BigDecimal newRate) {
        if (exchangeRates.containsKey(currencyCode.toUpperCase())) {
            exchangeRates.put(currencyCode.toUpperCase(), newRate);
        } else {
            throw new IllegalArgumentException("Currency not found: " + currencyCode);
        }
    }

    public void addCurrency(String currencyCode, BigDecimal rate) {
        exchangeRates.put(currencyCode.toUpperCase(), rate);
    }

    public void removeCurrency(String currencyCode) {
        exchangeRates.remove(currencyCode.toUpperCase());
    }

    public Map<String, BigDecimal> getAllExchangeRates() {
        return Collections.unmodifiableMap(exchangeRates);
    }
}
