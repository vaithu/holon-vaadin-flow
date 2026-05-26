package com.holonplatform.vaadin.flow.customer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration that enables the customer module.
 *
 * <p>Registered via {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}
 * so consuming applications do not need any explicit {@code @ComponentScan} for the customer package.</p>
 *
 * <p>The consuming application still needs to ensure {@link com.holonplatform.vaadin.flow.customer.entity.Customer}
 * is included in its JPA entity scan:</p>
 * <pre>{@code @EntityScan(basePackages = {"...", "com.holonplatform.vaadin.flow.customer.entity"})}</pre>
 */
@Configuration
@ComponentScan("com.holonplatform.vaadin.flow.customer")
public class CustomerAutoConfiguration {
}


