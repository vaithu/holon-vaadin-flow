package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationBinder;
import com.vaadin.collaborationengine.CollaborationEngine;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Adds opt-in real-time collaboration to any Vaadin form.
 *
 * <h3>Architecture — dual binder</h3>
 * <p>Internally two binders share the same field instances:
 * <ul>
 *   <li>{@code localBinder} ({@link BeanValidationBinder}) — always active; handles
 *       {@code readBean}/{@code writeBean} and Jakarta validation in <em>local mode</em>.</li>
 *   <li>{@code collabBinder} ({@link CollaborationBinder}) — activated by {@link #enable()};
 *       syncs field values across sessions via the Collaboration Engine topic.</li>
 * </ul>
 * Because Vaadin's {@code Binder} only tracks its own listeners, binding the same
 * {@code HasValue} field to two binders works without conflicts.  {@code CollaborationBinder}
 * is never asked to call {@code readBean()} (which it forbids), so no
 * {@link UnsupportedOperationException} or CE race-condition NPE can occur.
 *
 * <h3>Signals integration</h3>
 * <ul>
 *   <li>The toggle button state is driven by an internal {@code ValueSignal<Boolean>}
 *       via {@code Signal.effect(owner, runnable)}.
 *       No manual refresh calls — the button updates reactively.</li>
 *   <li>{@link #enabledSignal()} exposes the signal so <em>external</em> components
 *       can also react (e.g., dim the form, show a status badge). </li>
 *   <li>{@link #onCollaborationStarted(Component, Consumer)} uses a CE
 *       {@link com.vaadin.collaborationengine.CollaborationMap} to push a
 *       cross-session invitation to <em>all other users</em> currently viewing the
 *       same entity.</li>
 * </ul>
 *
 * <h3>Option A — generateFormLayout (auto-create fields)</h3>
 * <pre>{@code
 * var collab = CollaborationFormSupport.of(Contact.class, localUser);
 * FormLayout form = collab.generateFormLayout("id", "createdAt");
 *
 * toolbar.add(collab.avatarGroup(), collab.toggleButton());
 * collab.setEntity("contact/" + id, contact);
 * }</pre>
 *
 * <h3>Option B — autoBindFields (declare fields by name)</h3>
 * <pre>{@code
 * private final TextField  name  = new TextField("Name");
 * private final EmailField email = new EmailField("Email");
 *
 * var collab = CollaborationFormSupport.of(Contact.class, localUser);
 * collab.autoBindFields(this);
 * }</pre>
 *
 * <h3>Dependency</h3>
 * Requires {@code com.vaadin:collaboration-engine} on the runtime classpath.
 *
 * @param <T> the bean type managed by the binder
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CollaborationFormSupport<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** CE {@link com.vaadin.collaborationengine.CollaborationMap} key for invitation broadcasts. */
    private static final String INVITE_MAP_NAME = "collab-form-invitations";

    /** Applied to the toggle button while collaboration is active. */
    public static final String CLASS_ACTIVE   = "collab-form--active";
    /** Applied to the toggle button while collaboration is inactive. */
    public static final String CLASS_INACTIVE = "collab-form--inactive";

    private static final Pattern CAMEL_SPLIT =
            Pattern.compile("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");

    // ── State ─────────────────────────────────────────────────────────────────

    private final Class<T>                  beanType;
    private final UserInfo                  localUser;

    /**
     * Plain binder used in <em>local mode</em>.  Fully supports {@code readBean()}
     * and {@code writeBean()}.  Always bound to the form fields.
     */
    private final BeanValidationBinder<T>   localBinder;

    /**
     * CE binder used only when collaboration is <em>active</em>.
     * Also bound to the same field instances as {@code localBinder}.
     * Its {@code setTopic()} is never called before {@link #enable()}.
     */
    private final CollaborationBinder<T>    collabBinder;

    private final CollaborationAvatarGroup  avatarGroup;
    private final Button                    toggleBtn;

    /**
     * Reactive enabled flag — drives the toggle button and is exposed publicly
     * so external components can bind to it via {@link Signal#effect}.
     */
    private final ValueSignal<Boolean>      enabledSignal = new ValueSignal<>(false);

    private String                  topicId;
    private SerializableSupplier<T> beanSupplier;

    // ── Constructor ───────────────────────────────────────────────────────────

    private CollaborationFormSupport(Class<T> beanType, UserInfo localUser) {
        this.beanType     = beanType;
        this.localUser    = localUser;
        this.localBinder  = new BeanValidationBinder<>(beanType);
        this.collabBinder = new CollaborationBinder<>(beanType, localUser);
        this.avatarGroup  = new CollaborationAvatarGroup(localUser, null);
        this.avatarGroup.setVisible(false);
        this.toggleBtn    = buildToggleButton();
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    /**
     * Creates a {@code CollaborationFormSupport} for the given bean class.
     *
     * @param beanType  the bean class (not null)
     * @param localUser information about the currently logged-in user (not null)
     */
    public static <T> CollaborationFormSupport<T> of(Class<T> beanType, UserInfo localUser) {
        if (beanType  == null) throw new IllegalArgumentException("beanType must not be null");
        if (localUser == null) throw new IllegalArgumentException("localUser must not be null");
        return new CollaborationFormSupport<>(beanType, localUser);
    }

    // ── Option A: auto-generate form layout ───────────────────────────────────

    /**
     * Introspects the bean class, creates appropriate Vaadin field components,
     * binds them to <em>both</em> binders (local + CE), and returns a {@link FormLayout}.
     *
     * <p>Type mapping: {@code String}→{@link TextField}/{@link EmailField},
     * int/long→{@link IntegerField}, double/float/BigDecimal→{@link NumberField},
     * boolean→{@link Checkbox}, {@link LocalDate}→{@link DatePicker},
     * {@link LocalDateTime}→{@link DateTimePicker}, Enum→{@link ComboBox}.
     *
     * @param excludeFields bean property names to omit
     */
    public FormLayout generateFormLayout(String... excludeFields) {
        return generateFormLayout(null, excludeFields);
    }

    /**
     * Same as {@link #generateFormLayout(String...)} with a post-build configurator.
     *
     * @param configurator optional post-build customisation (may be null)
     * @param excludeFields bean property names to omit
     */
    public FormLayout generateFormLayout(Consumer<FormLayout> configurator,
                                         String... excludeFields) {
        var form    = new FormLayout();
        var exclude = excludeFields.length > 0 ? Set.of(excludeFields) : Set.<String>of();

        for (Field f : resolveFields(beanType)) {
            if (exclude.contains(f.getName())) continue;
            if (isAutoSkipped(f))              continue;
            Component component = createComponent(f);
            if (component == null)             continue;
            // Bind to BOTH binders — same HasValue instance, separate listener registrations.
            localBinder .forField((HasValue) component).bind(f.getName());
            collabBinder.forField((HasValue) component).bind(f.getName());
            form.add(component);
        }

        if (configurator != null) configurator.accept(form);
        return form;
    }

    // ── Option B: bind pre-declared fields ───────────────────────────────────

    /**
     * Scans {@code fieldsHolder} for {@link HasValue} instance fields whose Java
     * variable names match bean property names, binding them with Jakarta
     * validation from the bean class.  Fields are bound to <em>both</em> binders.
     *
     * @param fieldsHolder pass {@code this} from a view
     */
    public void autoBindFields(Object fieldsHolder) {
        localBinder .bindInstanceFields(fieldsHolder);
        collabBinder.bindInstanceFields(fieldsHolder);
    }

    /**
     * Same as {@link #autoBindFields(Object)} but skips the named properties.
     */
    public void autoBindFields(Object fieldsHolder, String... excludeFields) {
        if (excludeFields == null || excludeFields.length == 0) {
            autoBindFields(fieldsHolder);
            return;
        }
        var exclude = Set.of(excludeFields);
        for (Field f : resolveFields(fieldsHolder.getClass())) {
            if (exclude.contains(f.getName())) continue;
            f.setAccessible(true);
            try {
                Object value = f.get(fieldsHolder);
                if (value instanceof HasValue<?, ?> hv) {
                    try { localBinder .forField((HasValue) hv).bind(f.getName()); }
                    catch (IllegalArgumentException ignored) { /* not a bean property */ }
                    try { collabBinder.forField((HasValue) hv).bind(f.getName()); }
                    catch (IllegalArgumentException ignored) { /* not a bean property */ }
                }
            } catch (IllegalAccessException ignored) { /* skip */ }
        }
    }

    // ── Entity registration ───────────────────────────────────────────────────

    /**
     * Registers the entity currently being edited and populates the form fields.
     *
     * <p>In <em>local mode</em> (collaboration off) fields are populated via
     * {@code localBinder.readBean(bean)} — no CE involvement, no NPE risk.
     * The CE topic is only activated when {@link #enable()} is called explicitly.
     */
    public void setEntity(String topicId, T bean) {
        setEntity(topicId, () -> bean);
    }

    /**
     * Supplier variant — the latest bean is re-fetched when collaboration (re-)opens.
     */
    public void setEntity(String topicId, SerializableSupplier<T> beanSupplier) {
        if (topicId      == null) throw new IllegalArgumentException("topicId must not be null");
        if (beanSupplier == null) throw new IllegalArgumentException("beanSupplier must not be null");

        this.topicId      = topicId;
        this.beanSupplier = beanSupplier;

        avatarGroup.setVisible(true);
        avatarGroup.setTopic(topicId);

        if (isEnabled()) {
            // Already in collab mode — update the CE topic to the new entity.
            collabBinder.setTopic(topicId, beanSupplier);
        } else {
            // Local mode: use the plain BeanValidationBinder — fully supports readBean().
            // collabBinder.setTopic() is NOT called here, avoiding the CE NPE race.
            localBinder.readBean(beanSupplier.get());
        }
    }

    /**
     * Clears the current entity — disables collaboration and empties all fields.
     */
    public void clearEntity() {
        if (isEnabled()) {
            // Same CE 7.x guard as disable() — capture supplier before nulling fields below.
            SerializableSupplier<T> safeSupplier = beanSupplier != null ? beanSupplier : this::newEmptyBean;
            collabBinder.setTopic(null, safeSupplier);
        }
        enabledSignal.set(false);
        T empty = newEmptyBean();
        if (empty != null) localBinder.readBean(empty);  // clear fields
        this.topicId      = null;
        this.beanSupplier = null;
        avatarGroup.setTopic(null);
        avatarGroup.setVisible(false);
    }

    // ── Enable / disable ──────────────────────────────────────────────────────

    /**
     * Activates collaboration on the current entity topic.
     *
     * <p>Current field values are captured first so the user's local edits survive
     * the CE re-connect.  CE only calls the supplier when the topic has no existing
     * data, so a peer already in the topic always wins.
     */
    public void enable() {
        if (isEnabled() || topicId == null) return;
        enabledSignal.set(true);
        // Capture what the user has typed locally so CE seeds the topic with those values.
        T draft = writeCurrentDraft();
        SerializableSupplier<T> effectiveSupplier = draft != null ? () -> draft : beanSupplier;
        collabBinder.setTopic(topicId, effectiveSupplier);
        broadcastCollabStarted(true);
    }

    /**
     * Deactivates collaboration — reverts to local-only mode.
     * CE preserves field values on {@code setTopic(null,null)} disconnect so no
     * re-population is needed.  Avatar group (presence) stays active.
     */
    public void disable() {
        if (!isEnabled()) return;
        enabledSignal.set(false);
        // CE 7.x always calls initialBeanSupplier.get() in setTopic() even when disconnecting
        // (null topic). Passing null as supplier causes NPE — always supply the current bean or a
        // fresh empty instance as a safe fallback.
        SerializableSupplier<T> safeSupplier = beanSupplier != null ? beanSupplier : this::newEmptyBean;
        collabBinder.setTopic(null, safeSupplier);
        broadcastCollabStarted(false);
    }

    /** Toggles collaboration on/off. */
    public void toggle() {
        if (isEnabled()) disable(); else enable();
    }

    /** Returns {@code true} when collaboration is currently active. */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabledSignal.peek());
    }

    // ── Signal accessors ──────────────────────────────────────────────────────

    /**
     * Returns the reactive enabled-state signal.
     *
     * <pre>{@code
     * Signal.effect(formDiv, () -> {
     *     boolean on = collab.enabledSignal().get();
     *     formDiv.setClassName("collab-active", on);
     * });
     * }</pre>
     */
    public ValueSignal<Boolean> enabledSignal() {
        return enabledSignal;
    }

    // ── Cross-session invitation ───────────────────────────────────────────────

    /**
     * Registers a handler that fires in <em>this</em> session when <em>another</em>
     * user calls {@link #enable()} on the same entity topic.
     *
     * <p>The subscription is lifecycle-bound to {@code owner}: removed on detach.
     */
    public void onCollaborationStarted(Component owner, Consumer<String> handler) {
        if (topicId == null) return;
        openInvitationSubscription(owner, handler);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    /**
     * The underlying {@link CollaborationBinder}.
     * Use for {@code writeBeanIfValid()} / {@code addStatusChangeListener()} etc.
     */
    public CollaborationBinder<T> binder() { return collabBinder; }

    /**
     * The {@link CollaborationAvatarGroup} — visible once an entity is set
     * (shows presence), even before collaboration is toggled on.
     */
    public CollaborationAvatarGroup avatarGroup() { return avatarGroup; }

    /**
     * The pre-wired toggle button — state driven reactively by {@link #enabledSignal()}.
     */
    public Button toggleButton() { return toggleBtn; }

    // ── Private — CE invitation ───────────────────────────────────────────────

    private void broadcastCollabStarted(boolean active) {
        if (topicId == null) return;
        try {
            CollaborationEngine.getInstance().openTopicConnection(
                    toggleBtn, topicId, localUser,
                    connection -> {
                        var map = connection.getNamedMap(INVITE_MAP_NAME);
                        map.put(localUser.getId(), active ? localUser.getName() : null);
                        return null;
                    });
        } catch (Exception ignored) {
            // CE not configured (dev/test) — skip silently
        }
    }

    private void openInvitationSubscription(Component owner, Consumer<String> handler) {
        try {
            CollaborationEngine.getInstance().openTopicConnection(
                    owner, topicId, localUser,
                    connection -> {
                        var map = connection.getNamedMap(INVITE_MAP_NAME);
                        map.subscribe(event -> {
                            String inviterName = event.getValue(String.class);
                            if (inviterName != null
                                    && !localUser.getId().equals(event.getKey())) {
                                handler.accept(inviterName);
                            }
                        });
                        return null;
                    });
        } catch (Exception ignored) {
            // CE not configured — skip silently
        }
    }

    // ── Private — helpers ─────────────────────────────────────────────────────

    /**
     * Writes current field values into a fresh bean.  Returns {@code null} on failure.
     * Uses {@code localBinder} (safe in all modes) to avoid any CE dependency.
     */
    private T writeCurrentDraft() {
        try {
            T draft = beanType.getDeclaredConstructor().newInstance();
            localBinder.writeBeanIfValid(draft);
            return draft;
        } catch (Exception e) {
            return null;
        }
    }

    /** Creates a fresh empty bean via no-arg constructor; returns {@code null} on failure. */
    private T newEmptyBean() {
        try {
            return beanType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    // ── Private — introspection ───────────────────────────────────────────────

    private static Iterable<Field> resolveFields(Class<?> clazz) {
        Map<String, Field> seen = new LinkedHashMap<>();
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) seen.putIfAbsent(f.getName(), f);
        }
        return seen.values();
    }

    private static boolean isAutoSkipped(Field f) {
        int mod = f.getModifiers();
        if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) return true;
        if (f.isAnnotationPresent(com.holonplatform.core.beans.Identifier.class)) return true;
        if (f.isAnnotationPresent(com.holonplatform.core.beans.Version.class))    return true;
        return switch (f.getName()) {
            case "id", "version", "serialVersionUID" -> true;
            default -> false;
        };
    }

    private static Component createComponent(Field f) {
        Class<?> type  = f.getType();
        String   label = toLabel(f.getName());

        if (type == String.class) {
            boolean hasEmail = Arrays.stream(f.getAnnotations())
                    .anyMatch(a -> a.annotationType().getSimpleName().equals("Email"));
            return hasEmail ? new EmailField(label) : new TextField(label);
        }
        if (type == Integer.class || type == int.class
                || type == Long.class    || type == long.class) return new IntegerField(label);
        if (type == Double.class || type == double.class
                || type == Float.class   || type == float.class
                || type == BigDecimal.class)               return new NumberField(label);
        if (type == Boolean.class || type == boolean.class) return new Checkbox(label);
        if (type == LocalDate.class)                        return new DatePicker(label);
        if (type == LocalDateTime.class)                    return new DateTimePicker(label);
        if (type.isEnum()) {
            ComboBox box = new ComboBox<>(label);
            box.setItems(type.getEnumConstants());
            return box;
        }
        return null;
    }

    static String toLabel(String name) {
        if (name == null || name.isBlank()) return name;
        String spaced = CAMEL_SPLIT.matcher(name).replaceAll(" ");
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }

    // ── Private — toggle button with reactive Signal.effect ──────────────────

    private Button buildToggleButton() {
        Button btn = Components.button()
                .text("Collaborate")
                .icon(VaadinIcon.USERS)
                .tertiary()
                .styleName(CLASS_INACTIVE)
                .withClickListener(e -> toggle())
                .build();

        Signal.effect(btn, () -> {
            boolean on = enabledSignal.get();
            if (on) {
                btn.setText("Stop sharing");
                btn.setIcon(VaadinIcon.USER.create());
                btn.removeThemeVariants(ButtonVariant.LUMO_TERTIARY);
                btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                btn.removeClassName(CLASS_INACTIVE);
                btn.addClassName(CLASS_ACTIVE);
            } else {
                btn.setText("Collaborate");
                btn.setIcon(VaadinIcon.USERS.create());
                btn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                btn.removeClassName(CLASS_ACTIVE);
                btn.addClassName(CLASS_INACTIVE);
            }
        });

        return btn;
    }
}

