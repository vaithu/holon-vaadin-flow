package com.holonplatform.vaadin.flow.chat.components;

import com.holonplatform.vaadin.flow.chat.ChatRoom;
import com.holonplatform.vaadin.flow.chat.ChatRoomMember;
import com.holonplatform.vaadin.flow.chat.api.ChatService;
import com.holonplatform.vaadin.flow.chat.i18n.ChatI18N;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.io.Serial;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Dialog for creating a new Chat Room (public channel or private group).
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * CreateRoomDialog dialog = new CreateRoomDialog(chatService, localUserId)
 *     .onCreated(newRoom -> channelList.refresh());
 * dialog.open();
 * }</pre>
 *
 * <h3>Room types</h3>
 * <ul>
 *   <li><strong>Channel</strong> – named room visible to everyone. Optional "Private" toggle
 *       restricts visibility to invited members.</li>
 *   <li><strong>Group</strong> – always private multi-person chat room, like a group DM.</li>
 * </ul>
 *
 * <p>All visual styling is in {@code chat-room-dialog.css}.
 */
@StyleSheet("context://chat-room-dialog.css")
public class CreateRoomDialog extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ChatService chatService;
    private final String localUserId;

    private final Select<ChatRoom.Type> typeSelect = new Select<>();
    private final TextField nameField = new TextField();
    private final TextArea descriptionField = new TextArea();
    private final Checkbox privateCheck = new Checkbox("Private (invite only)");

    private Consumer<ChatRoom> onCreated;

    public CreateRoomDialog(ChatService chatService, String localUserId) {
        if (chatService == null) throw new IllegalArgumentException("chatService must not be null");
        if (localUserId == null) throw new IllegalArgumentException("localUserId must not be null");
        this.chatService = chatService;
        this.localUserId = localUserId;

        addClassName("create-room-dialog");
        setWidth("440px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);  // prevent accidental close mid-form

        add(buildHeader(), buildForm(), buildFooter());
    }

    /** Registers a callback invoked after the room is successfully created. */
    public CreateRoomDialog onCreated(Consumer<ChatRoom> callback) {
        this.onCreated = callback;
        return this;
    }

    // ------------------------------------------------------------------ //
    // Build
    // ------------------------------------------------------------------ //

    private H3 buildHeader() {
        H3 title = new H3(LocalizationProvider.localize("Create a new room", ChatI18N.CREATE_ROOM_TITLE));
        title.addClassName("create-room-dialog__title");
        return title;
    }

    private FormLayout buildForm() {
        typeSelect.addClassName("create-room-dialog__type");
        typeSelect.setLabel(LocalizationProvider.localize("Room type", ChatI18N.CREATE_ROOM_TYPE_LABEL));
        typeSelect.setItems(ChatRoom.Type.CHANNEL, ChatRoom.Type.GROUP);
        typeSelect.setItemLabelGenerator(t -> switch (t) {
            case CHANNEL -> LocalizationProvider.localize("Channel  (#)", ChatI18N.CREATE_ROOM_TYPE_CHANNEL);
            case GROUP   -> LocalizationProvider.localize("Group  (@)",   ChatI18N.CREATE_ROOM_TYPE_GROUP);
            default      -> t.name();
        });
        typeSelect.setValue(ChatRoom.Type.CHANNEL);
        typeSelect.addValueChangeListener(e -> {
            boolean isGroup = e.getValue() == ChatRoom.Type.GROUP;
            privateCheck.setValue(isGroup);
            privateCheck.setEnabled(!isGroup);  // groups are always private
        });

        nameField.addClassName("create-room-dialog__name");
        nameField.setLabel(LocalizationProvider.localize("Name", ChatI18N.CREATE_ROOM_NAME_LABEL));
        nameField.setPlaceholder(LocalizationProvider.localize("e.g. project-alpha", ChatI18N.CREATE_ROOM_NAME_PLACEHOLDER));
        nameField.setRequiredIndicatorVisible(true);
        nameField.setMaxLength(80);
        nameField.addValueChangeListener(e -> {
            if (typeSelect.getValue() == ChatRoom.Type.CHANNEL) {
                String clean = e.getValue().toLowerCase().replaceAll("\\s+", "-");
                if (!clean.equals(e.getValue())) {
                    nameField.setValue(clean);
                }
            }
        });

        descriptionField.addClassName("create-room-dialog__description");
        descriptionField.setLabel(LocalizationProvider.localize("Description (optional)", ChatI18N.CREATE_ROOM_DESC_LABEL));
        descriptionField.setPlaceholder(LocalizationProvider.localize("What is this room about?", ChatI18N.CREATE_ROOM_DESC_PLACEHOLDER));
        descriptionField.setMaxLength(500);
        descriptionField.setMaxHeight("80px");

        privateCheck.addClassName("create-room-dialog__private");
        privateCheck.setLabel(LocalizationProvider.localize("Private (invite only)", ChatI18N.CREATE_ROOM_PRIVATE_LABEL));
        privateCheck.setEnabled(true);

        Paragraph hint = new Paragraph(LocalizationProvider.localize(
                "Group chats are always private \u2014 only invited participants can join.",
                ChatI18N.CREATE_ROOM_GROUP_HINT));
        hint.addClassName("create-room-dialog__hint");

        FormLayout form = new FormLayout(typeSelect, nameField, descriptionField, privateCheck, hint);
        form.addClassName("create-room-dialog__form");
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        return form;
    }

    private HorizontalLayout buildFooter() {
        Button cancel = new Button(LocalizationProvider.localize("Cancel", ChatI18N.CREATE_ROOM_CANCEL_BTN),
                e -> close());
        cancel.addClassName("create-room-dialog__cancel");

        Button create = new Button(LocalizationProvider.localize("Create room", ChatI18N.CREATE_ROOM_CREATE_BTN),
                e -> handleCreate());
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.addClassName("create-room-dialog__submit");

        HorizontalLayout footer = new HorizontalLayout(cancel, create);
        footer.addClassName("create-room-dialog__footer");
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setSpacing(true);
        return footer;
    }

    // ------------------------------------------------------------------ //
    // Logic
    // ------------------------------------------------------------------ //

    private void handleCreate() {
        String name = nameField.getValue().trim();
        if (name.isBlank()) {
            nameField.setErrorMessage(
                    LocalizationProvider.localize("Name is required", ChatI18N.CREATE_ROOM_NAME_REQUIRED));
            nameField.setInvalid(true);
            return;
        }
        nameField.setInvalid(false);

        String id   = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String desc = descriptionField.getValue().trim();
        ChatRoom.Type type = typeSelect.getValue();
        boolean priv = privateCheck.getValue();

        ChatRoom room;
        if (type == ChatRoom.Type.GROUP) {
            room = ChatRoom.group(id, name, desc.isEmpty() ? null : desc);
        } else {
            room = ChatRoom.channel(id, name, desc.isEmpty() ? null : desc);
            room.setPrivateRoom(priv);
        }

        try {
            chatService.saveRoom(room);
            chatService.joinRoom(localUserId, room.getId());
            chatService.findMembership(localUserId, room.getId()).ifPresent(m ->
                    m.setRole(ChatRoomMember.Role.OWNER.name()));

            Notification n = Notification.show(
                    LocalizationProvider.localize("Room \u201c{0}\u201d created!", ChatI18N.CREATE_ROOM_CREATED_NOTIFICATION, name));
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            n.setPosition(Notification.Position.BOTTOM_END);
            n.setDuration(3000);

            close();
            if (onCreated != null) onCreated.accept(room);
        } catch (Exception ex) {
            Notification err = Notification.show(
                    LocalizationProvider.localize("Error creating room: {0}", ChatI18N.CREATE_ROOM_ERROR_NOTIFICATION,
                            ex.getMessage()));
            err.addThemeVariants(NotificationVariant.LUMO_ERROR);
            err.setDuration(5000);
        }
    }
}
