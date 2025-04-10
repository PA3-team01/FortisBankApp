package com.fortisbank.business.services.account;

    import com.fortisbank.contracts.models.accounts.AccountType;
    import com.fortisbank.contracts.models.others.InterestRate;
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
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * Singleton service to load and manage interest rates from a config file.
     */
    public class InterestRateConfigService {
        private static final Logger LOGGER = Logger.getLogger(InterestRateConfigService.class.getName());
        private static final Path CONFIG_PATH = Paths.get("config/interest_rates.json");
        private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        private static InterestRateConfigService instance;

        private List<InterestRate> rates;

        // Private constructor for singleton
        private InterestRateConfigService() {
            loadRates();
        }

        /**
         * Returns the singleton instance of InterestRateConfigService.
         *
         * @return the singleton instance of InterestRateConfigService
         */
        public static synchronized InterestRateConfigService getInstance() {
            if (instance == null) {
                instance = new InterestRateConfigService();
            }
            return instance;
        }

        /**
         * Loads interest rates from the config file.
         */
        public void loadRates() {
            try {
                rates = Files.exists(CONFIG_PATH)
                        ? Arrays.asList(mapper.readValue(CONFIG_PATH.toFile(), InterestRate[].class))
                        : new ArrayList<>();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to load interest rates from config file: {0}", e.getMessage());
                throw new RuntimeException("Error loading interest rates", e);
            }
        }

        /**
         * Saves the current interest rates to the config file.
         */
        public void saveRates() {
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(CONFIG_PATH.toFile(), rates);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to save interest rates to config file: {0}", e.getMessage());
                throw new RuntimeException("Error saving interest rates", e);
            }
        }

        /**
         * Retrieves the interest rate for the given account type.
         *
         * @param type the account type
         * @return the interest rate for the given account type
         */
        public BigDecimal getRate(AccountType type) {
            return rates.stream()
                    .filter(r -> r.getAccountType() == type)
                    .map(InterestRate::getRate)
                    .findFirst()
                    .orElse(BigDecimal.ZERO);
        }

        /**
         * Updates the interest rate for the given account type.
         *
         * @param type the account type
         * @param newRate the new interest rate
         */
        public void updateRate(AccountType type, BigDecimal newRate) {
            try {
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
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to update interest rate for account type {0}: {1}", new Object[]{type, e.getMessage()});
                throw new RuntimeException("Error updating interest rate", e);
            }
        }

        /**
         * Retrieves all interest rates.
         *
         * @return the list of all interest rates
         */
        public List<InterestRate> getAllRates() {
            return rates;
        }
    }