package com.holonplatform.vaadin.flow.chat.components;

import com.holonplatform.vaadin.flow.chat.ChatInvitation;
import com.holonplatform.vaadin.flow.chat.ChatRoom;
import com.holonplatform.vaadin.flow.chat.api.ChatService;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A ready-to-use {@link Dialog} for inviting users to a group chat room.
 *
 * <h3>Minimal usage (user-ID only input)</h3>
 * <pre>{@code
 * Button inviteBtn = new Button("Invite", e ->
 *     InviteToGroupDialog.open(chatService, room, currentUserInfo));
 * }</pre>
 *
 * <h3>With an application-provided user list (recommended)</h3>
 * <pre>{@code
 * InviteToGroupDialog dialog = new InviteToGroupDialog(chatService, room, currentUserInfo);
 * dialog.setAvailableUsers(() -> userService.findAll()
 *     .stream()
 *     .map(u -> new InviteToGroupDialog.UserRef(u.getId(), u.getFullName(), u.getAvatarUrl()))
 *     .toList());
 * dialog.open();
 * }</pre>
 *
 * <p>If no user list is provided the dialog shows a simple text field where the
 * inviter types a user ID manually.
 */
@StyleSheet("context://invite-dialog.css")
public class InviteToGroupDialog extends Dialog {

    // ------------------------------------------------------------------ //
    // Public user reference DTO
    // ------------------------------------------------------------------ //

    /**
     * Lightweight reference to an application user — enough for the invite dialog.
     *
     * @param id       unique user identifier (same as {@link UserInfo#getId()})
     * @param name     display name
     * @param imageUrl optional avatar URL (may be null)
     */
    public record UserRef(String id, String name, String imageUrl) {
        public UserRef(String id, String name) { this(id, name, null); }
    }

    // ------------------------------------------------------------------ //
    // State
    // ------------------------------------------------------------------ //

    private final ChatService chatService;
    private final ChatRoom    room;
    private final UserInfo    inviter;

    private Supplier<List<UserRef>> availableUsersSupplier;
    private Function<String, String> userIdToNameResolver = id -> id;

    /** Row container — rebuilt when the dialog opens or the list is refreshed. */
    private final VerticalLayout rows = new VerticalLayout();

    // ------------------------------------------------------------------ //
    // Constructor
    // ------------------------------------------------------------------ //

    /**
     * Creates an invite dialog for the given room and inviter.
     *
     * @param chatService the service backing persistence (not null)
     * @param room        the room users will be invited to (must not be null)
     * @param inviter     the currently logged-in user sending invitations (not null)
     */
    public InviteToGroupDialog(ChatService chatService, ChatRoom room, UserInfo inviter) {
        if (chatService == null) throw new IllegalArgumentException("chatService must not be null");
        if (room        == null) throw new IllegalArgumentException("room must not be null");
        if (inviter     == null) throw new IllegalArgumentException("inviter must not be null");

        this.chatService = chatService;
        this.room        = room;
        this.inviter     = inviter;

        setHeaderTitle("Invite people to " + roomDisplayName(room));
        setWidth("420px");
        setDraggable(true);

        buildContent();

        Button closeBtn = new Button(VaadinIcon.CLOSE.create(), e -> close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        getHeader().add(closeBtn);
    }

    // ------------------------------------------------------------------ //
    // Configuration
    // ------------------------------------------------------------------ //

    /**
     * Provides the list of users shown in the dialog.
     * The supplier is called each time the dialog opens or is refreshed.
     * Already-invited and already-member users are displayed as disabled rows.
     *
     * <p>If not set, the dialog shows a manual user-ID text field instead.
     *
     * @param supplier a zero-arg supplier returning the available users
     * @return this dialog (fluent)
     */
    public InviteToGroupDialog setAvailableUsers(Supplier<List<UserRef>> supplier) {
        this.availableUsersSupplier = supplier;
        return this;
    }

    /**
     * Optionally provide a function that resolves a raw user ID to a display name.
     * Used only in manual-ID mode (when no {@link #setAvailableUsers} supplier was set).
     *
     * @param resolver function: userId → displayName; defaults to identity (userId)
     * @return this dialog (fluent)
     */
    public InviteToGroupDialog setUserIdToNameResolver(Function<String, String> resolver) {
        if (resolver != null) this.userIdToNameResolver = resolver;
        return this;
    }

    // ------------------------------------------------------------------ //
    // Static opener helper
    // ------------------------------------------------------------------ //

    /**
     * Convenience factory: creates and immediately opens an invite dialog.
     *
     * @param chatService the service
     * @param room        target room
     * @param inviter     currently logged-in user
     * @return the opened dialog
     */
    public static InviteToGroupDialog open(ChatService chatService, ChatRoom room, UserInfo inviter) {
        InviteToGroupDialog d = new InviteToGroupDialog(chatService, room, inviter);
        d.open();
        return d;
    }

    // ------------------------------------------------------------------ //
    // Layout
    // ------------------------------------------------------------------ //

    private void buildContent() {
        rows.setPadding(false);
        rows.setSpacing(false);

        // Rebuilt on open so membership state is fresh
        addOpenedChangeListener(e -> {
            if (e.isOpened()) refreshContent();
        });

        add(rows);
    }

    private void refreshContent() {
        rows.removeAll();

        if (availableUsersSupplier != null) {
            buildUserListContent();
        } else {
            buildManualIdContent();
        }
    }

    // ── Mode A: application provides a user list ────────���──────────────

    private void buildUserListContent() {
        List<UserRef> users;
        try {
            users = availableUsersSupplier.get();
        } catch (Exception ex) {
            rows.add(new Paragraph("Could not load users: " + ex.getMessage()));
            return;
        }

        // Already-members and already-invited (PENDING) sets
        List<String> memberIds  = chatService.findRoomMembers(room.getId())
                .stream().map(m -> m.getUserId()).toList();
        List<String> invitedIds = chatService.findRoomInvitations(room.getId())
                .stream()
                .filter(ChatInvitation::checkPending)
                .map(ChatInvitation::getInviteeId)
                .toList();

        // Exclude the inviter themselves
        List<UserRef> candidates = users.stream()
                .filter(u -> !u.id().equals(inviter.getId()))
                .toList();

        if (candidates.isEmpty()) {
            rows.add(new Paragraph("No users available to invite."));
            return;
        }

        for (UserRef user : candidates) {
            boolean isMember  = memberIds.contains(user.id());
            boolean isInvited = invitedIds.contains(user.id());
            rows.add(buildUserRow(user, isMember, isInvited));
        }
    }

    private Div buildUserRow(UserRef user, boolean isMember, boolean isInvited) {
        var row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.addClassName("invite-dialog__row");

        // Left: name
        var nameSpan = new Span(user.name());
        nameSpan.addClassName("invite-dialog__name");
        row.add(nameSpan);
        row.setFlexGrow(1, nameSpan);

        // Right: state badge or invite button
        if (isMember) {
            var badge = new Span("Already a member");
            badge.addClassName("invite-dialog__badge--member");
            row.add(badge);
        } else if (isInvited) {
            var badge = new Span("Invited ✓");
            badge.addClassName("invite-dialog__badge--invited");
            row.add(badge);
        } else {
            var inviteBtn = new Button("Invite");
            inviteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            inviteBtn.addClickListener(e -> {
                doInvite(user.id(), user.name());
                // Replace button with "Invited ✓" badge
                row.remove(inviteBtn);
                var badge = new Span("Invited ✓");
                badge.addClassName("invite-dialog__badge--invited");
                row.add(badge);
            });
            row.add(inviteBtn);
        }

        var wrapper = new Div(row);
        wrapper.addClassName("invite-dialog__row-wrapper");
        return wrapper;
    }

    // ── Mode B: manual user-ID input ──────────────────────────────────

    private void buildManualIdContent() {
        var hint = new Paragraph(
                "Enter the user ID (or email) of the person you want to invite to "
                + roomDisplayName(room) + ".");
        hint.addClassName("invite-dialog__hint");

        var field = new TextField();
        field.setPlaceholder("User ID or email…");
        field.setWidthFull();
        field.setPrefixComponent(VaadinIcon.USER.create());
        field.setClearButtonVisible(true);

        var sendBtn = new Button("Send invitation", VaadinIcon.PAPERPLANE.create());
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendBtn.setWidthFull();
        sendBtn.addClickListener(e -> {
            String userId = field.getValue().trim();
            if (userId.isBlank()) {
                field.setInvalid(true);
                field.setErrorMessage("Please enter a user ID.");
                return;
            }
            field.setInvalid(false);
            String name = userIdToNameResolver.apply(userId);
            doInvite(userId, name);
            field.clear();
        });
        field.addKeyPressListener(com.vaadin.flow.component.Key.ENTER, e -> sendBtn.click());

        // Show previously sent (PENDING) invitations
        List<ChatInvitation> pending = chatService.findRoomInvitations(room.getId())
                .stream().filter(ChatInvitation::checkPending).toList();

        rows.add(hint, field, sendBtn);

        if (!pending.isEmpty()) {
            var sentHeader = new Span("Pending invitations:");
            sentHeader.addClassName("invite-dialog__sent-header");
            rows.add(sentHeader);
            for (ChatInvitation inv : pending) {
                rows.add(buildSentInvitationRow(inv));
            }
        }
    }

    private Div buildSentInvitationRow(ChatInvitation inv) {
        var name = new Span(inv.getInviteeId());
        name.addClassName("invite-dialog__name");

        var badge = new Span("Pending ⏳");
        badge.addClassName("invite-dialog__badge--pending");

        var row = new HorizontalLayout(name, badge);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.setFlexGrow(1, name);

        var wrapper = new Div(row);
        wrapper.addClassName("invite-dialog__row-wrapper");
        return wrapper;
    }

    // ------------------------------------------------------------------ //
    // Core invite action
    // ------------------------------------------------------------------ //

    private void doInvite(String inviteeId, String inviteeName) {
        try {
            chatService.inviteToRoom(
                    room.getId(),
                    roomDisplayName(room),
                    inviter.getId(),
                    inviter.getName() != null ? inviter.getName() : inviter.getId(),
                    inviteeId
            );
            Notification n = Notification.show(
                    "Invitation sent to " + inviteeName, 3000,
                    Notification.Position.BOTTOM_END);
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification n = Notification.show(
                    "Could not send invitation: " + ex.getMessage(), 4000,
                    Notification.Position.BOTTOM_END);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // ------------------------------------------------------------------ //
    // Helpers
    // ------------------------------------------------------------------ //

    private static String roomDisplayName(ChatRoom room) {
        if (room.getName() != null && !room.getName().isBlank()) return "#" + room.getName();
        return room.getId();
    }
}



