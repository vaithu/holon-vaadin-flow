package com.holonplatform.vaadin.flow.demo;

import com.holonplatform.vaadin.flow.customer.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seeds demo data for the Customer module when the H2 database is empty.
 *
 * <p>Runs once at application startup via {@link CommandLineRunner}.
 * The seed method is idempotent — it does nothing if records already exist.</p>
 */
@Configuration
public class CustomerDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(CustomerDataInitializer.class);

    @Bean
    CommandLineRunner seedCustomers(CustomerService customerService) {
        return args -> {
            try {
                customerService.seedIfEmpty();
            } catch (Exception ex) {
                log.warn("Customer seed failed (may be safe to ignore on first boot): {}", ex.getMessage());
            }
        };
    }
}

