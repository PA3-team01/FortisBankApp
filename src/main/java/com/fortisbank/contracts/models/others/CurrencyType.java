package com.fortisbank.contracts.models.others;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class representing currency types and their exchange rates.
 */
public class CurrencyType {
    private static CurrencyType instance;
    private final Map<String, BigDecimal> exchangeRates;

    /**
     * Private constructor to initialize the exchange rates map with default values.
     */
    private CurrencyType() {
        exchangeRates = new HashMap<>();

        // Initialize with some default exchange rates (Base Currency: USD)
        exchangeRates.put("USD", BigDecimal.ONE);
        exchangeRates.put("EUR", new BigDecimal("0.92"));
        exchangeRates.put("CAD", new BigDecimal("1.35"));
        exchangeRates.put("GBP", new BigDecimal("0.78"));
        exchangeRates.put("JPY", new BigDecimal("150.25"));
    }

    /**
     * Returns the singleton instance of CurrencyType.
     *
     * @return the singleton instance
     */
    public static CurrencyType getInstance() {
        if (instance == null) {
            instance = new CurrencyType();
        }
        return instance;
    }

    /**
     * Returns the exchange rate for the specified currency code.
     *
     * @param currencyCode the currency code
     * @return the exchange rate
     */
    public BigDecimal getExchangeRate(String currencyCode) {
        return exchangeRates.getOrDefault(currencyCode.toUpperCase(), BigDecimal.ZERO);
    }

    /**
     * Updates the exchange rate for the specified currency code.
     *
     * @param currencyCode the currency code
     * @param newRate the new exchange rate
     * @throws IllegalArgumentException if the currency code is not found
     */
    public void updateExchangeRate(String currencyCode, BigDecimal newRate) {
        if (exchangeRates.containsKey(currencyCode.toUpperCase())) {
            exchangeRates.put(currencyCode.toUpperCase(), newRate);
        } else {
            throw new IllegalArgumentException("Currency not found: " + currencyCode);
        }
    }

    /**
     * Adds a new currency with the specified exchange rate.
     *
     * @param currencyCode the currency code
     * @param rate the exchange rate
     */
    public void addCurrency(String currencyCode, BigDecimal rate) {
        exchangeRates.put(currencyCode.toUpperCase(), rate);
    }

    /**
     * Removes the specified currency from the exchange rates map.
     *
     * @param currencyCode the currency code
     */
    public void removeCurrency(String currencyCode) {
        exchangeRates.remove(currencyCode.toUpperCase());
    }

    /**
     * Returns an unmodifiable map of all exchange rates.
     *
     * @return the map of all exchange rates
     */
    public Map<String, BigDecimal> getAllExchangeRates() {
        return Collections.unmodifiableMap(exchangeRates);
    }
}