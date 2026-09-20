package com.holonplatform.vaadin.flow.demo.data.service;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.vaadin.flow.chat.internal.DefaultChatService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Spring-managed singleton that wraps {@link DefaultChatService}, giving it
 * transactional semantics via Spring AOP.
 *
 * <p>All Vaadin route components that need chat functionality should inject this
 * bean rather than instantiating {@code DefaultChatService} directly.
 *
 * <p>Seed data (default channels and demo user memberships) is loaded via
 * {@code data.sql} on startup rather than programmatically.
 */
@Service
@Transactional
public class DemoChatPersistenceService extends DefaultChatService {

    public DemoChatPersistenceService(Datastore datastore) {
        super(datastore);
    }
}

