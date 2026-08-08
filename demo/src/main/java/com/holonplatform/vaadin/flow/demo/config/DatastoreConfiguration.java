package com.holonplatform.vaadin.flow.demo.config;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.datastore.jpa.JpaDatastore;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Explicit Datastore configuration for the demo application.
 *
 * <p>The {@code holon-datastore-jpa-spring-boot} auto-configuration registers the
 * {@link Datastore} bean via an {@link org.springframework.context.annotation.ImportBeanDefinitionRegistrar},
 * which IntelliJ's Spring plugin cannot trace statically.  In Spring Boot 4.1 the
 * {@code @ConditionalOnSingleCandidate(EntityManagerFactory.class)} condition on the
 * auto-configuration can also fail to fire at the right moment.
 *
 * <p>Defining the bean here explicitly:
 * <ul>
 *   <li>eliminates the "No beans of 'Datastore' type found" IDE warning, and</li>
 *   <li>guarantees the bean is present at runtime regardless of auto-config ordering.</li>
 * </ul>
 * The {@link ConditionalOnMissingBean} guard prevents a duplicate if the
 * auto-configuration somehow also creates one.
 */
@Configuration
public class DatastoreConfiguration {

    @Bean
    @ConditionalOnMissingBean(Datastore.class)
    public Datastore datastore(EntityManagerFactory entityManagerFactory) {
        return JpaDatastore.builder()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}

