package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.chat.ChatInvitation;
import com.holonplatform.vaadin.flow.chat.ChatRoom;
import com.holonplatform.vaadin.flow.chat.components.InviteToGroupDialog;
import com.holonplatform.vaadin.flow.chat.components.LiveChat;
import com.holonplatform.vaadin.flow.demo.data.service.DemoChatPersistenceService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Map;

/**
 * Demo page for the {@link LiveChat} component.
 *
 * <h3>How to simulate multiple users (live real-time testing)</h3>
 * <p>Open two (or more) browser tabs/windows pointing to the same URL with different
 * {@code ?user=} query parameters. The parameter selects a pre-defined demo persona:
 *
 * <pre>
 *   Tab 1 (Alice):  {@code http://localhost:8081/live-chat?user=alice}
 *   Tab 2 (Bob):    {@code http://localhost:8081/live-chat?user=bob}
 *   Tab 3 (Carol):  {@code http://localhost:8081/live-chat?user=carol}
 * </pre>
 *
 * <p>If the {@code ?user=} parameter is omitted a user-picker dialog appears
 * so you can choose your persona from within the browser.
 *
 * <h3>Examples on this page</h3>
 * <ol>
 *   <li><strong>Quick test</strong> – two side-by-side panes (Alice + Bob) in the same
 *       browser tab; messages flow between them in real time.</li>
 *   <li><strong>Single channel, DB-backed</strong> – with full persistence, message
 *       history survives navigation, and an "↑ Load older messages" button is visible.</li>
 *   <li><strong>Multi-channel + lazy loading</strong> – channel sidebar, unread badges,
 *       typing indicator, page-size 25 with "Load older" pagination.</li>
 * </ol>
 */
@PageTitle("LiveChat – Holon Demo")
@Route(value = "live-chat", layout = DemoMainLayout.class)
public class LiveChatDemoView extends Div implements BeforeEnterObserver {

    // ── Pre-defined demo personas ─────────────────────────────────────────────

    record DemoUser(String id, String name, String avatar) {
        UserInfo toUserInfo() {
            return new UserInfo(id, name, avatar);
        }
    }

    private static final List<DemoUser> DEMO_USERS = List.of(
            new DemoUser("alice", "Alice",  "https://i.pravatar.cc/150?u=alice"),
            new DemoUser("bob",   "Bob",    "https://i.pravatar.cc/150?u=bob"),
            new DemoUser("carol", "Carol",  "https://i.pravatar.cc/150?u=carol"),
            new DemoUser("dave",  "Dave",   "https://i.pravatar.cc/150?u=dave"),
            new DemoUser("eve",   "Eve",    "https://i.pravatar.cc/150?u=eve")
    );

    // ── Spring-injected singleton service (JPA / H2 backed) ──────────────────

    private final DemoChatPersistenceService chatService;

    private DemoUser currentUser;

    public LiveChatDemoView(DemoChatPersistenceService chatService) {
        this.chatService = chatService;
    }

    // ------------------------------------------------------------------ //
    // Route parameter injection
    // ------------------------------------------------------------------ //

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Read ?user=alice from the URL
        Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
        List<String> userParam = params.getOrDefault("user", List.of());

        if (!userParam.isEmpty()) {
            String userId = userParam.getFirst().toLowerCase();
            currentUser = DEMO_USERS.stream()
                    .filter(u -> u.id().equals(userId))
                    .findFirst()
                    .orElse(DEMO_USERS.getFirst());
            buildContent();
        }
        // If no param → show picker dialog after attach
    }

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (currentUser == null) {
            showUserPickerDialog();
        }
    }

    // ------------------------------------------------------------------ //
    // User picker dialog (shown when ?user= is absent)
    // ------------------------------------------------------------------ //

    private void showUserPickerDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setHeaderTitle("Who are you? (pick a demo user)");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(false);

        Span hint = new Span("Tip: open another tab with ?user=bob to chat with yourself");
        hint.addClassName("live-chat-demo__hint");
        content.add(hint);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        for (DemoUser user : DEMO_USERS) {
            Button btn = new Button(user.name());
            btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            btn.addClickListener(e -> {
                currentUser = user;
                // Update URL without reloading so the user can bookmark/share
                String newUrl = "live-chat?user=" + user.id();
                UI.getCurrent().getPage().getHistory().replaceState(null, newUrl);
                dialog.close();
                buildContent();
            });
            buttons.add(btn);
        }
        content.add(buttons);
        dialog.add(content);
        dialog.open();
    }

    // ------------------------------------------------------------------ //
    // Page content
    // ------------------------------------------------------------------ //

    private void buildContent() {
        removeAll();
        addClassName("app-view");

        add(
                new H1("LiveChat"),
                new Paragraph(
                        "Logged in as: " + currentUser.name() +
                        " · Open another tab with ?user=bob to test real-time " +
                        "between two users · Try: "
                ),
                buildTabLinks(),
                example1_sideBySide(),
                example2_directMessage(),
                example3_singleChannelPersisted(),
                example4_multiChannel(),
                example5_groupInvite()
        );
    }

    private Div buildTabLinks() {
        Div links = new Div();
        links.addClassName("live-chat-demo__tab-links");
        for (DemoUser user : DEMO_USERS) {
            Span link = new Span("?user=" + user.id() + "  ");
            link.addClassName("live-chat-demo__tab-link");
            link.getElement().setAttribute("title", "Open as " + user.name());
            link.addClickListener(e ->
                    UI.getCurrent().getPage().open("live-chat?user=" + user.id(), "_blank"));
            links.add(link);
        }
        return links;
    }

    // ── Example 1 ────────────────────────────────────────────────────────────

    private Div example1_sideBySide() {
        var section = section("1 — Quick test (side-by-side panes in one tab)",
                "Two panes opened as different users. Messages flow in real time. "
                + "This requires @Push — already enabled on DemoApplication.");

        UserInfo alice = new UserInfo("alice", "Alice", "https://i.pravatar.cc/150?u=alice");
        UserInfo bob   = new UserInfo("bob",   "Bob",   "https://i.pravatar.cc/150?u=bob");

        LiveChat chatAlice = LiveChat.builder(alice)
                .room("demo-quick")
                .withTypingIndicator()
                .build();
        chatAlice.addClassName("demo-chat-pane");

        LiveChat chatBob = LiveChat.builder(bob)
                .room("demo-quick")
                .withTypingIndicator()
                .build();
        chatBob.addClassName("demo-chat-pane");

        Div wrapper = new Div();
        wrapper.addClassName("demo-chat-grid");
        wrapper.add(chatAlice, chatBob);

        section.add(wrapper);
        return section;
    }

    // ── Example 2 ────────────────────────────────────────────────────────────

    private Div example2_directMessage() {
        var section = section("2 — Direct message (1-to-1)",
                "Private 1-to-1 conversation between Alice and Bob. "
                + "Call .directMessage() on the builder to hide avatars and names — "
                + "both parties already know who they are talking to. "
                + "Own messages appear on the RIGHT in brand blue; the other person's on the LEFT.");

        // Canonical DM room ID — same result computed from either user's perspective
        String dmRoomId = ChatRoom.direct("alice", "bob").getId();  // "dm/alice:bob"

        UserInfo alice = new UserInfo("alice-dm", "Alice", "https://i.pravatar.cc/150?u=alice");
        UserInfo bob   = new UserInfo("bob-dm",   "Bob",   "https://i.pravatar.cc/150?u=bob");

        LiveChat chatAlice = LiveChat.builder(alice)
                .room(dmRoomId)
                .withTypingIndicator()
                .directMessage()       // ← hides avatar + name for ALL messages
                .build();
        chatAlice.addClassName("demo-chat-pane");

        LiveChat chatBob = LiveChat.builder(bob)
                .room(dmRoomId)
                .withTypingIndicator()
                .directMessage()
                .build();
        chatBob.addClassName("demo-chat-pane");

        Div wrapper = new Div();
        wrapper.addClassName("demo-chat-grid");
        wrapper.add(chatAlice, chatBob);

        section.add(wrapper);
        return section;
    }

    // ── Example 3 ────────────────────────────────────────────────────────────

    private Div example3_singleChannelPersisted() {
        var section = section("3 — Single channel, DB-backed + lazy loading",
                "Open in a second tab as a different user to see real-time sync. "
                + "Messages persist in-memory for the lifetime of the JVM. "
                + "Click '↑ Load older messages' to page through history.");

        LiveChat chat = LiveChat.builder(currentUser.toUserInfo())
                .room("announcements")
                .withPersistence(chatService)
                .withTypingIndicator()
                .withPageSize(10)   // small page size so "Load older" is easy to trigger
                .build();
        chat.addClassName("demo-chat-full");

        section.add(chat);
        return section;
    }

    // ── Example 4 ────────────────────────────────────────────────────────────

    private Div example4_multiChannel() {
        var section = section("4 — Multi-channel with sidebar + lazy loading",
                "Channel sidebar with snapshot unread counts. Switch channels to see "
                + "read-receipt tracking. Page size = 25.");

        LiveChat chat = LiveChat.builder(currentUser.toUserInfo())
                .room("general")
                .withChannels(chatService)
                .withRoomManagement()
                .withPersistence(chatService)
                .withTypingIndicator()
                .withPageSize(25)
                .build();
        chat.addClassName("demo-chat-tall");

        section.add(chat);
        return section;
    }

    // ── Example 5 — Group invite dialog ──────────────────────────────────────

    private Div example5_groupInvite() {
        var section = section("5 — Group invitation",
                "Use InviteToGroupDialog to invite users to a group room. "
                + "The dialog supports two modes: a searchable user list (when you provide "
                + "a user supplier) and a manual user-ID input. "
                + "Accepted invitations automatically content the user as a room member.");

        // The group room we'll invite users into
        ChatRoom groupRoom = ChatRoom.group("demo-group-1", "Project Alpha",
                "Demo group room for invitation example");
        chatService.saveRoom(groupRoom);
        chatService.joinRoom(currentUser.id(), groupRoom.getId());   // inviter is already a member

        // Inviter = current user
        UserInfo inviterInfo = currentUser.toUserInfo();

        // ── Mode A: with available users list ──
        var openListMode = new Button("Open invite dialog (user list)", VaadinIcon.USER_CARD.create(),
                e -> {
                    InviteToGroupDialog dialog = new InviteToGroupDialog(
                            chatService, groupRoom, inviterInfo);
                    dialog.setAvailableUsers(() ->
                            DEMO_USERS.stream()
                                    .filter(u -> !u.id().equals(currentUser.id()))
                                    .map(u -> new InviteToGroupDialog.UserRef(u.id(), u.name()))
                                    .toList());
                    dialog.open();
                });
        openListMode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // ── Mode B: manual ID input ──
        var openManualMode = new Button("Open invite dialog (manual ID)", VaadinIcon.EDIT.create(),
                e -> InviteToGroupDialog.open(chatService, groupRoom, inviterInfo));

        // ── Pending invitations panel for the current user ──
        var pendingSection = new Div();
        pendingSection.addClassName("invite-demo__pending");
        refreshPendingPanel(pendingSection);

        var refreshBtn = new Button("Refresh my pending invitations",
                VaadinIcon.REFRESH.create(),
                e -> refreshPendingPanel(pendingSection));
        refreshBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        var btnRow = new HorizontalLayout(openListMode, openManualMode);
        btnRow.setSpacing(true);

        section.add(btnRow, pendingSection, refreshBtn);
        return section;
    }

    /**
     * Rebuilds the pending-invitations list for the current user inside the given container.
     */
    private void refreshPendingPanel(Div container) {
        container.removeAll();
        List<ChatInvitation> pending = chatService.findPendingInvitations(currentUser.id());
        if (pending.isEmpty()) {
            container.add(new Paragraph("No pending invitations for you."));
            return;
        }
        container.add(new Paragraph("Your pending invitations:"));
        for (ChatInvitation inv : pending) {
            var row = new HorizontalLayout();
            row.setAlignItems(FlexComponent.Alignment.CENTER);
            row.setSpacing(true);
            row.add(new Span("Join #" + inv.getRoomName()
                    + "  (invited by " + inv.getInviterName() + ")"));

            var acceptBtn = new Button("Accept", e -> {
                chatService.acceptInvitation(inv.getId());
                refreshPendingPanel(container);
                Notification.show("Joined #" + inv.getRoomName(), 2500,
                        Notification.Position.BOTTOM_END);
            });
            acceptBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);

            var declineBtn = new Button("Decline", e -> {
                chatService.declineInvitation(inv.getId());
                refreshPendingPanel(container);
            });
            declineBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY,
                    ButtonVariant.LUMO_SMALL);

            row.add(acceptBtn, declineBtn);
            container.add(row);
        }
    }

    // ── Section helper ────────────────────────────────────────────────────────

    private Div section(String title, String description) {
        Div section = new Div();
        section.addClassName("demo-section");
        section.add(new H2(title));
        section.add(new Paragraph(description));
        return section;
    }
}
