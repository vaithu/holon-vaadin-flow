package com.holonplatform.vaadin.flow.demo.data;

import com.holonplatform.vaadin.flow.demo.data.service.DemoChatPersistenceService;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inserts seed data into the H2 in-memory database on application startup.
 */
@Component
public class DemoDataInitializer implements ApplicationRunner {

    private final ProductService productService;
    private final DemoChatPersistenceService chatService;

    public DemoDataInitializer(ProductService productService,
                               DemoChatPersistenceService chatService) {
        this.productService = productService;
        this.chatService    = chatService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        productService.seedIfEmpty();
        chatService.seedIfEmpty();
    }
}
