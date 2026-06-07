package com.holonplatform.vaadin.flow.chat.components;

import com.vaadin.collaborationengine.CollaborationEngine;
import com.vaadin.collaborationengine.CollaborationMap;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;

import com.holonplatform.vaadin.flow.chat.i18n.ChatI18N;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;

import java.io.Serial;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Real-time "is typing..." indicator powered by Collaboration Kit's {@link CollaborationMap}
 * with Signal-driven reactive rendering and debounce-based auto-clear.
 *
 * <h3>Signal architecture</h3>
 * <pre>
 *  startTyping()
 *    ├── CollaborationMap.put(userId, now)  ─► every session: handleMapChange()
 *    │                                              └── recomputeSignals()
 *    │                                                    ├── displayText.set("Alice is typing…")
 *    │                                                    └── isVisible.set(true)
 *    │                                                          └─► Signal.effect() fires
 *    │                                                                └── label.setText(…) ✓
 *    └── SCHEDULER.schedule(TYPING_TIMEOUT_MS)
 *          └── ui.access(stopTyping)
 *                └── CollaborationMap.put(userId, null)
 *                       └── (same chain)  isVisible.set(false)
 *                                          Signal.effect() fires
 *                                          getContent().setVisible(false) ✓
 * </pre>
 *
 * <p><strong>Auto-clear</strong>: after {@value #TYPING_TIMEOUT_MS} ms of no new keystrokes
 * the debounce scheduler calls {@link #stopTyping()} automatically, which propagates to all
 * connected sessions via the Collaboration Kit and triggers the reactive effect to hide the
 * indicator — no timer polling on the reader side needed.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * TypingIndicator indicator = new TypingIndicator(userInfo, "room-id");
 * messageInput.addKeyPressListener(e -> indicator.startTyping());
 * sendButton.addClickListener(e -> indicator.stopTyping());
 * content(indicator);
 * }</pre>
 *
 * <p>All visual styling is in {@code typing-indicator.css}.
 */
@StyleSheet("context://typing-indicator.css")
public class TypingIndicator extends Composite<Div> {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Milliseconds of idle time before the typing entry is auto-cleared. */
    private static final long TYPING_TIMEOUT_MS = 4_000L;

    private static final String MAP_NAME = "chat-typing";
    /** Side-channel map: userId → display name; each user writes their own entry. */
    private static final String NAMES_MAP_NAME = "chat-typing-names";

    /** Shared daemon scheduler for auto-clear debounce (pooled — handles high concurrency). */
    private static final ScheduledExecutorService SCHEDULER =
            Executors.newScheduledThreadPool(
                    Math.max(2, Runtime.getRuntime().availableProcessors()),
                    r -> {
                        Thread t = new Thread(r, "typing-indicator-debounce");
                        t.setDaemon(true);
                        return t;
                    });

    // ------------------------------------------------------------------ //
    // State
    // ------------------------------------------------------------------ //

    private final UserInfo localUser;
    private final String roomId;

    /** Local snapshot: userId → last keystroke instant (null = stopped). */
    private final Map<String, Instant> typingSnapshot = new ConcurrentHashMap<>();

    /**
     * Signal holding the label text — updated by {@link #recomputeSignals()}.
     * {@code Signal.effect()} in {@link #onAttach} observes this and updates the DOM.
     */
    private final ValueSignal<String> displayText = new ValueSignal<>("");

    /**
     * Signal controlling indicator visibility.
     * Setting it to {@code false} automatically hides the component via the effect.
     */
    private final ValueSignal<Boolean> isVisible = new ValueSignal<>(false);

    /** Debounce handle — cancelled and rescheduled on every keystroke. */
    private ScheduledFuture<?> clearTask;

    // ------------------------------------------------------------------ //
    // Collaboration Kit
    // ------------------------------------------------------------------ //

    private CollaborationMap sharedMap;
    /** Stores userId → display name so we can resolve names from other participants. */
    private CollaborationMap namesMap;
    private Registration topicRegistration;

    // ------------------------------------------------------------------ //
    // UI
    // ------------------------------------------------------------------ //

    private final Span dots = new Span();
    private final Span label = new Span();

    // ------------------------------------------------------------------ //
    // Constructor
    // ------------------------------------------------------------------ //

    /**
     * Creates a new typing indicator bound to the given user and room.
     *
     * @param localUser information about the currently logged-in user (not null)
     * @param roomId    identifies the chat room / Collaboration Kit topic (not null)
     */
    public TypingIndicator(UserInfo localUser, String roomId) {
        if (localUser == null) throw new IllegalArgumentException("localUser must not be null");
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("roomId must not be blank");

        this.localUser = localUser;
        this.roomId = roomId;

        getContent().addClassName("typing-indicator");

        dots.addClassName("typing-indicator__dots");
        dots.add(new Span(), new Span(), new Span());   // 3 animated dots

        label.addClassName("typing-indicator__label");

        getContent().add(dots, label);
    }

    // ------------------------------------------------------------------ //
    // Lifecycle
    // ------------------------------------------------------------------ //

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        openTopicConnection();

        // Signal.effect() — lifecycle-bound to getContent(); auto-disposes on detach.
        // Re-runs automatically whenever displayText or isVisible changes.
        Signal.effect(getContent(), () -> {
            getContent().setVisible(isVisible.get());
            label.setText(displayText.get());
        });
    }

    @Override
    protected void onDetach(DetachEvent event) {
        stopTyping();           // remove our own entry from the shared map
        cancelClearTask();
        closeTopicConnection();
        super.onDetach(event);
    }

    // ------------------------------------------------------------------ //
    // Public API
    // ------------------------------------------------------------------ //

    /**
     * Marks the local user as currently typing.
     * Resets the auto-clear debounce timer — call on every keystroke.
     */
    public void startTyping() {
        executeWithMap(map -> map.put(localUser.getId(), Instant.now()));
        // Publish our display name so other sessions can resolve it
        if (namesMap != null) {
            String name = localUser.getName() != null && !localUser.getName().isBlank()
                    ? localUser.getName() : localUser.getId();
            namesMap.put(localUser.getId(), name);
        }

        // Debounce: cancel any pending auto-clear and reschedule for TYPING_TIMEOUT_MS from now.
        cancelClearTask();
        getUI().ifPresent(ui ->
                clearTask = SCHEDULER.schedule(
                        () -> ui.access(this::stopTyping),
                        TYPING_TIMEOUT_MS,
                        TimeUnit.MILLISECONDS));
    }

    /**
     * Marks the local user as no longer typing.
     * Also called automatically after {@value #TYPING_TIMEOUT_MS} ms of idle keystrokes.
     */
    public void stopTyping() {
        executeWithMap(map -> map.put(localUser.getId(), null));
        cancelClearTask();
    }

    // ------------------------------------------------------------------ //
    // Collaboration Kit connection
    // ------------------------------------------------------------------ //

    private void openTopicConnection() {
        topicRegistration = CollaborationEngine.getInstance()
                .openTopicConnection(
                        getContent(),
                        "typing/" + roomId,
                        localUser,
                        connection -> {
                            sharedMap = connection.getNamedMap(MAP_NAME);
                            sharedMap.subscribe(e ->
                                    handleMapChange(e.getKey(), e.getValue(Instant.class)));
                            namesMap = connection.getNamedMap(NAMES_MAP_NAME);
                            return null;
                        });
    }

    private void closeTopicConnection() {
        if (topicRegistration != null) {
            topicRegistration.remove();
            topicRegistration = null;
        }
        sharedMap = null;
        namesMap  = null;
    }

    private void executeWithMap(java.util.function.Consumer<CollaborationMap> action) {
        if (sharedMap != null) {
            action.accept(sharedMap);
        }
    }

    // ------------------------------------------------------------------ //
    // Map change → Signal update (runs on UI thread via Collaboration Kit)
    // ------------------------------------------------------------------ //

    private void handleMapChange(String userId, Instant typedAt) {
        if (typedAt == null) {
            typingSnapshot.remove(userId);
        } else {
            typingSnapshot.put(userId, typedAt);
        }
        recomputeSignals();
    }

    /**
     * Recomputes the active-typers list and publishes results to signals.
     * {@code Signal.effect()} in {@link #onAttach} then automatically syncs the DOM.
     */
    private void recomputeSignals() {
        Instant cutoff = Instant.now().minusMillis(TYPING_TIMEOUT_MS);

        List<String> activeNames = typingSnapshot.entrySet().stream()
                .filter(e -> !e.getKey().equals(localUser.getId()))
                .filter(e -> e.getValue().isAfter(cutoff))
                .map(e -> resolveDisplayName(e.getKey()))
                .sorted()
                .toList();

        if (activeNames.isEmpty()) {
            // Order matters: set text before hiding to avoid flicker.
            displayText.set("");
            isVisible.set(false);
        } else {
            String text = switch (activeNames.size()) {
                case 1  -> LocalizationProvider.localize(
                                "{0} is typing\u2026", ChatI18N.TYPING_SINGLE,
                                activeNames.getFirst());
                case 2  -> LocalizationProvider.localize(
                                "{0} and {1} are typing\u2026", ChatI18N.TYPING_TWO,
                                activeNames.get(0), activeNames.get(1));
                default -> LocalizationProvider.localize(
                                "Several people are typing\u2026", ChatI18N.TYPING_SEVERAL);
            };
            displayText.set(text);
            isVisible.set(true);
        }
    }

    // ------------------------------------------------------------------ //
    // Helpers
    // ------------------------------------------------------------------ //

    private void cancelClearTask() {
        if (clearTask != null) {
            clearTask.cancel(false);
            clearTask = null;
        }
    }

    /**
     * Resolves a user ID to its display name using the shared names map.
     * Falls back to the raw userId only when no name has been published.
     */
    private String resolveDisplayName(String userId) {
        if (namesMap != null) {
            String name = namesMap.get(userId, String.class);
            if (name != null && !name.isBlank()) return name;
        }
        return userId;
    }
}

