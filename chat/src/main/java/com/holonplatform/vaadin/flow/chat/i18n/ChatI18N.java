package com.holonplatform.vaadin.flow.chat.i18n;

/**
 * Message key constants for the chat module.
 *
 * <p>All keys follow the convention {@code chat.<component>.<element>}.
 *
 * <h3>Registering translations</h3>
 * <p>Bundle: {@code com.holonplatform.vaadin.flow.chat.i18n.ChatMessages}
 * <pre>{@code
 * // Holon LocalizationContext (Spring Boot auto-config picks this up automatically):
 * LocalizationContext.builder()
 *     .withMessageProvider(
 *         MessageProvider.fromResourceBundle("com.holonplatform.vaadin.flow.chat.i18n.ChatMessages"))
 *     .build();
 * }</pre>
 *
 * <p>The chat module ships an English default bundle. Add
 * {@code ChatMessages_fr.properties}, {@code ChatMessages_de.properties}, etc. to override.
 *
 * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider
 */
public final class ChatI18N {

    private ChatI18N() {}

    // ------------------------------------------------------------------ //
    // LiveChat
    // ------------------------------------------------------------------ //

    /** "Load older messages" button label. */
    public static final String LIVE_CHAT_LOAD_OLDER   = "chat.live_chat.load_older";
    /** Room title prefix — e.g. "# " before the room name. */
    public static final String LIVE_CHAT_ROOM_PREFIX  = "chat.live_chat.room_prefix";

    // ------------------------------------------------------------------ //
    // Channel list (sidebar)
    // ------------------------------------------------------------------ //

    /** Sidebar section heading. */
    public static final String CHANNEL_LIST_HEADER          = "chat.channel_list.header";
    /** Tooltip for the "create room" (+) button. */
    public static final String CHANNEL_LIST_CREATE_TOOLTIP  = "chat.channel_list.create_tooltip";
    /** Tooltip for the "browse rooms" (⊕) button. */
    public static final String CHANNEL_LIST_BROWSE_TOOLTIP  = "chat.channel_list.browse_tooltip";

    // ------------------------------------------------------------------ //
    // Create-room dialog
    // ------------------------------------------------------------------ //

    /** Dialog heading. */
    public static final String CREATE_ROOM_TITLE                 = "chat.create_room.title";
    /** "Room type" select label. */
    public static final String CREATE_ROOM_TYPE_LABEL            = "chat.create_room.type_label";
    /** Item label for CHANNEL type. */
    public static final String CREATE_ROOM_TYPE_CHANNEL          = "chat.create_room.type_channel";
    /** Item label for GROUP type. */
    public static final String CREATE_ROOM_TYPE_GROUP            = "chat.create_room.type_group";
    /** "Name" field label. */
    public static final String CREATE_ROOM_NAME_LABEL            = "chat.create_room.name_label";
    /** "Name" field placeholder. */
    public static final String CREATE_ROOM_NAME_PLACEHOLDER      = "chat.create_room.name_placeholder";
    /** "Description" field label. */
    public static final String CREATE_ROOM_DESC_LABEL            = "chat.create_room.desc_label";
    /** "Description" field placeholder. */
    public static final String CREATE_ROOM_DESC_PLACEHOLDER      = "chat.create_room.desc_placeholder";
    /** "Private" checkbox label. */
    public static final String CREATE_ROOM_PRIVATE_LABEL         = "chat.create_room.private_label";
    /** Hint text shown below the form. */
    public static final String CREATE_ROOM_GROUP_HINT            = "chat.create_room.group_hint";
    /** Cancel button. */
    public static final String CREATE_ROOM_CANCEL_BTN            = "chat.create_room.cancel_btn";
    /** Create button. */
    public static final String CREATE_ROOM_CREATE_BTN            = "chat.create_room.create_btn";
    /** Validation error when name is blank. */
    public static final String CREATE_ROOM_NAME_REQUIRED         = "chat.create_room.name_required";
    /**
     * Success notification — first argument ({0}) is the room name.
     * Example: {@code "Room "project-alpha" created!"}
     */
    public static final String CREATE_ROOM_CREATED_NOTIFICATION  = "chat.create_room.created_notification";
    /** Error notification prefix — first argument ({0}) is the exception message. */
    public static final String CREATE_ROOM_ERROR_NOTIFICATION    = "chat.create_room.error_notification";

    // ------------------------------------------------------------------ //
    // Browse-rooms dialog
    // ------------------------------------------------------------------ //

    /** Dialog heading. */
    public static final String BROWSE_ROOMS_TITLE           = "chat.browse_rooms.title";
    /** Search field placeholder. */
    public static final String BROWSE_ROOMS_SEARCH          = "chat.browse_rooms.search";
    /** Close button label. */
    public static final String BROWSE_ROOMS_CLOSE_BTN       = "chat.browse_rooms.close_btn";
    /** Empty-state text. */
    public static final String BROWSE_ROOMS_EMPTY           = "chat.browse_rooms.empty";
    /**
     * Member-count badge (singular) — argument {0} = count.
     * Example: {@code "1 member"}
     */
    public static final String BROWSE_ROOMS_MEMBER_ONE      = "chat.browse_rooms.member_one";
    /**
     * Member-count badge (plural) — argument {0} = count.
     * Example: {@code "5 members"}
     */
    public static final String BROWSE_ROOMS_MEMBERS_MANY    = "chat.browse_rooms.members_many";
    /** "Join" button label. */
    public static final String BROWSE_ROOMS_JOIN_BTN        = "chat.browse_rooms.join_btn";
    /** "Leave" button label. */
    public static final String BROWSE_ROOMS_LEAVE_BTN       = "chat.browse_rooms.leave_btn";
    /**
     * Joined-room notification — argument {0} = room name.
     * Example: {@code "Joined #general"}
     */
    public static final String BROWSE_ROOMS_JOINED          = "chat.browse_rooms.joined";
    /** Join-error notification — argument {0} = error message. */
    public static final String BROWSE_ROOMS_JOIN_ERROR      = "chat.browse_rooms.join_error";
    /**
     * Left-room notification — argument {0} = room name.
     * Example: {@code "Left #general"}
     */
    public static final String BROWSE_ROOMS_LEFT            = "chat.browse_rooms.left";
    /** Leave-error notification — argument {0} = error message. */
    public static final String BROWSE_ROOMS_LEAVE_ERROR     = "chat.browse_rooms.leave_error";

    // ------------------------------------------------------------------ //
    // Typing indicator
    // ------------------------------------------------------------------ //

    /**
     * One person is typing — argument {0} = display name.
     * Example: {@code "Alice is typing…"}
     */
    public static final String TYPING_SINGLE  = "chat.typing.single";
    /**
     * Two people are typing — {0} = first name, {1} = second name.
     * Example: {@code "Alice and Bob are typing…"}
     */
    public static final String TYPING_TWO     = "chat.typing.two";
    /**
     * Three or more people are typing.
     * Example: {@code "Several people are typing…"}
     */
    public static final String TYPING_SEVERAL = "chat.typing.several";
    /** Accessible label for the typing indicator status region. */
    public static final String TYPING_ARIA_LABEL = "chat.typing.aria_label";

    // ------------------------------------------------------------------ //
    // Invite-to-group dialog
    // ------------------------------------------------------------------ //

    /**
     * Dialog header — argument {0} = room display name.
     * Example: {@code "Invite people to #general"}
     */
    public static final String INVITE_TITLE              = "chat.invite.title";
    /** Accessible label for the dialog close button. */
    public static final String INVITE_CLOSE_ARIA         = "chat.invite.close_aria";
    /** Error paragraph when the user list cannot be loaded — {0} = error message. */
    public static final String INVITE_ERROR_LOADING      = "chat.invite.error_loading";
    /** Empty-state paragraph when no invitable users remain. */
    public static final String INVITE_NO_USERS           = "chat.invite.no_users";
    /** Badge label for a user who is already a room member. */
    public static final String INVITE_ALREADY_MEMBER     = "chat.invite.already_member";
    /** Badge label for a user who has already been invited. */
    public static final String INVITE_BADGE_INVITED      = "chat.invite.badge_invited";
    /** Invite button label. */
    public static final String INVITE_BTN                = "chat.invite.btn";
    /**
     * Hint paragraph in manual-ID mode — {0} = room display name.
     * Example: {@code "Enter the user ID (or email) of the person you want to invite to #general."}
     */
    public static final String INVITE_HINT               = "chat.invite.hint";
    /** Placeholder for the manual user-ID text field. */
    public static final String INVITE_FIELD_PLACEHOLDER  = "chat.invite.field_placeholder";
    /** Send invitation button label. */
    public static final String INVITE_SEND_BTN           = "chat.invite.send_btn";
    /** Validation error shown when the user-ID field is blank. */
    public static final String INVITE_FIELD_REQUIRED     = "chat.invite.field_required";
    /** Section header above pending invitations list. */
    public static final String INVITE_PENDING_HEADER     = "chat.invite.pending_header";
    /** Badge label for a pending invitation. */
    public static final String INVITE_PENDING_BADGE      = "chat.invite.pending_badge";
    /**
     * Success notification — {0} = invitee display name.
     * Example: {@code "Invitation sent to Alice"}
     */
    public static final String INVITE_SENT_NOTIFICATION  = "chat.invite.sent_notification";
    /**
     * Error notification — {0} = error message.
     * Example: {@code "Could not send invitation: connection refused"}
     */
    public static final String INVITE_ERROR_NOTIFICATION = "chat.invite.error_notification";

    // ------------------------------------------------------------------ //
    // LiveChat – accessible labels
    // ------------------------------------------------------------------ //

    /** Marker appended to the sender abbreviation for edited messages. */
    public static final String LIVE_CHAT_EDITED_MARKER   = "chat.live_chat.edited_marker";
    /** Accessible label for the "Load older messages" button. */
    public static final String LIVE_CHAT_LOAD_OLDER_ARIA = "chat.live_chat.load_older_aria";
    /**
     * Accessible label for the message list region — {0} = room name.
     * Example: {@code "Messages for general"}
     */
    public static final String LIVE_CHAT_MESSAGES_ARIA   = "chat.live_chat.messages_aria";
    /** Accessible label for the avatar group showing who is in the room. */
    public static final String LIVE_CHAT_AVATARS_ARIA    = "chat.live_chat.avatars_aria";

    // ------------------------------------------------------------------ //
    // Channel list – accessible labels
    // ------------------------------------------------------------------ //

    /** Accessible label for the channel list nav/region. */
    public static final String CHANNEL_LIST_ARIA         = "chat.channel_list.aria_label";
    /**
     * Accessible label for a public/channel room row — {0} = room name.
     * Example: {@code "# general channel"}
     */
    public static final String CHANNEL_LIST_CHANNEL_ARIA = "chat.channel_list.channel_aria";
    /**
     * Accessible label for a direct-message room row — {0} = room name.
     * Example: {@code "@ Alice direct message"}
     */
    public static final String CHANNEL_LIST_DIRECT_ARIA  = "chat.channel_list.direct_aria";
    /**
     * Accessible label for the unread badge — {0} = count.
     * Example: {@code "3 unread messages"}
     */
    public static final String CHANNEL_LIST_UNREAD_ARIA  = "chat.channel_list.unread_aria";

    // ------------------------------------------------------------------ //
    // Browse-rooms dialog – accessible labels
    // ------------------------------------------------------------------ //

    /** Accessible label for the room-type icon in a channel row. */
    public static final String BROWSE_ROOMS_ICON_CHANNEL = "chat.browse_rooms.icon_channel";
    /** Accessible label for the room-type icon in a group row. */
    public static final String BROWSE_ROOMS_ICON_GROUP   = "chat.browse_rooms.icon_group";
    /**
     * Accessible label for the search field.
     */
    public static final String BROWSE_ROOMS_SEARCH_ARIA  = "chat.browse_rooms.search_aria";
}

