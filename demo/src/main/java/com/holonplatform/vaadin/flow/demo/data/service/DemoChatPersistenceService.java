package com.holonplatform.vaadin.flow.demo.data.service;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.vaadin.flow.chat.ChatRoom;
import com.holonplatform.vaadin.flow.chat.internal.DefaultChatService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring-managed singleton that wraps {@link DefaultChatService}, giving it
 * transactional semantics via Spring AOP and enabling seed data via
 * {@link com.holonplatform.vaadin.flow.demo.data.DemoDataInitializer}.
 *
 * <p>All Vaadin route components that need chat functionality should inject this
 * bean rather than instantiating {@code DefaultChatService} directly.
 */
@Service
@Transactional
public class DemoChatPersistenceService extends DefaultChatService {

    private static final Logger log = LoggerFactory.getLogger(DemoChatPersistenceService.class);

    private static final List<String> DEMO_USER_IDS =

            List.of("alice", "bob", "carol", "dave", "eve");

    public DemoChatPersistenceService(Datastore datastore) {
        super(datastore);
    }

    /**
     * Seeds default channels and populates all demo users as initial members.
     * Called by {@link com.holonplatform.vaadin.flow.demo.data.DemoDataInitializer}
     * after the JPA schema is created.
     *
     * <p>Idempotent — if channels already exist the method returns immediately.
     */
    public void seedIfEmpty() {
        //noinspection deprecation — findAllChannels is deprecated at the API level but needed here for seeding
        if (!findAllChannels().isEmpty()) {
            log.debug("Chat channels already seeded — skipping");
            return;
        }

        List<ChatRoom> rooms = List.of(
                ChatRoom.channel("general",       "general",       "General discussion"),
                ChatRoom.channel("random",         "random",        "Off-topic"),
                ChatRoom.channel("engineering",    "engineering",   "Tech talk"),
                ChatRoom.channel("design",         "design",        "Design decisions"),
                ChatRoom.channel("announcements",  "announcements", "Company news")
        );

        rooms.forEach(this::saveRoom);

        // All demo users start as members of every default channel
        for (String userId : DEMO_USER_IDS) {
            for (ChatRoom room : rooms) {
                joinRoom(userId, room.getId());
            }
        }

        log.info("Seeded {} demo chat channels with {} users each", rooms.size(), DEMO_USER_IDS.size());
    }
}

