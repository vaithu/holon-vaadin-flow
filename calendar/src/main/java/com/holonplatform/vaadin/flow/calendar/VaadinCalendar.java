package com.holonplatform.vaadin.flow.calendar;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code VaadinCalendar} – server-side Vaadin Flow component wrapping the
 * {@code <vaadin-calendar>} web element powered by FullCalendar 6 (MIT).
 *
 * <h3>Features</h3>
 * <ul>
 *   <li>Full CRUD API: {@link #addEvent}, {@link #addEvents}, {@link #updateEvent},
 *       {@link #deleteEvent}, {@link #setEvents}, {@link #getEvents}, {@link #getEvent}</li>
 *   <li>Navigation: {@link #today}, {@link #next}, {@link #previous}, {@link #navigateTo}</li>
 *   <li>View control: {@link #setView(CalendarView)}</li>
 *   <li>Google Calendar color palette: {@link EventColor}</li>
 *   <li>Calendar groups / category filtering: {@link CalendarGroup}</li>
 *   <li>Sidebar with mini-month, search, and group toggle</li>
 *   <li>Event popover (click-to-preview before edit)</li>
 *   <li>Rich content/edit dialog with repeat, location, URL, color picker</li>
 *   <li>Week numbers, business hours, timezone configuration</li>
 *   <li>Theming via CSS custom properties ({@code --vaadin-calendar-primary}, etc.)</li>
 * </ul>
 *
 * <h3>Minimal usage</h3>
 * <pre>{@code
 * VaadinCalendar cal = new VaadinCalendar();
 * cal.setSizeFull();
 *
 * cal.addCalendarReadyListener(e -> cal.setEvents(service.findAll()));
 * cal.addEventCreatedListener(e -> service.save(e.getEvent()));
 * cal.addEventUpdatedListener(e -> service.update(e.getEvent()));
 * cal.addEventDeletedListener(e -> service.delete(e.getDeletedId()));
 *
 * content(cal);
 * }</pre>
 */
@Tag("vaadin-calendar")
@NpmPackage(value = "@fullcalendar/core",        version = "6.1.15")
@NpmPackage(value = "@fullcalendar/daygrid",     version = "6.1.15")
@NpmPackage(value = "@fullcalendar/timegrid",    version = "6.1.15")
@NpmPackage(value = "@fullcalendar/list",        version = "6.1.15")
@NpmPackage(value = "@fullcalendar/interaction", version = "6.1.15")
@JsModule("./vaadin-calendar-element.js")
public class VaadinCalendar extends Component implements HasSize, Focusable<VaadinCalendar>, LocaleChangeObserver {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // =========================================================================
    // Enums
    // =========================================================================

    /** Calendar display views. */
    public enum CalendarView { MONTH, WEEK, DAY, AGENDA }

    /** Built-in themes. */
    public enum CalendarTheme { LIGHT, DARK }

    /**
     * Google Calendar–inspired event color palette.
     * Each constant carries the background hex and the recommended text hex.
     */
    public enum EventColor {
        TOMATO    ("#d50000", "#ffffff", "Tomato"),
        FLAMINGO  ("#e67c73", "#ffffff", "Flamingo"),
        TANGERINE ("#f4511e", "#ffffff", "Tangerine"),
        BANANA    ("#f6bf26", "#202124", "Banana"),
        SAGE      ("#33b679", "#ffffff", "Sage"),
        BASIL     ("#0b8043", "#ffffff", "Basil"),
        PEACOCK   ("#039be5", "#ffffff", "Peacock"),
        BLUEBERRY ("#3f51b5", "#ffffff", "Blueberry"),
        LAVENDER  ("#7986cb", "#ffffff", "Lavender"),
        GRAPE     ("#8e24aa", "#ffffff", "Grape"),
        GRAPHITE  ("#616161", "#ffffff", "Graphite");

        private final String hex;
        private final String textHex;
        private final String label;

        EventColor(String hex, String textHex, String label) {
            this.hex     = hex;
            this.textHex = textHex;
            this.label   = label;
        }

        /** Background colour hex, e.g. {@code "#d50000"}. */
        public String getHex()     { return hex; }
        /** Recommended foreground (text) colour hex. */
        public String getTextHex() { return textHex; }
        /** Human-readable label, e.g. {@code "Tomato"}. */
        public String getLabel()   { return label; }
    }

    /**
     * Repeat cadence for a calendar event.
     * Stored as a string in {@link CalendarEvent} for forward compatibility.
     */
    public enum RepeatUnit { NONE, DAILY, WEEKLY, MONTHLY, YEARLY }

    // =========================================================================
    // CalendarGroup – named calendar / category
    // =========================================================================

    /**
     * A named calendar group (like Google "My calendars").
     * Events reference a group via {@link CalendarEvent#getGroup()}.
     * Groups can be toggled visible/hidden; the JS side filters events accordingly.
     */
    public static final class CalendarGroup {

        private final String  id;
        private final String  name;
        private final String  color;
        private final boolean visible;

        public CalendarGroup(String id, String name, String color, boolean visible) {
            this.id      = Objects.requireNonNull(id,   "id must not be null");
            this.name    = Objects.requireNonNull(name, "name must not be null");
            this.color   = (color != null && !color.isBlank()) ? color : "#1a73e8";
            this.visible = visible;
        }

        /** Convenience constructor – visible by default. */
        public CalendarGroup(String id, String name, String color) {
            this(id, name, color, true);
        }

        public String  getId()      { return id; }
        public String  getName()    { return name; }
        public String  getColor()   { return color; }
        public boolean isVisible()  { return visible; }

        /** Returns a copy with the given visibility. */
        public CalendarGroup withVisible(boolean v) { return new CalendarGroup(id, name, color, v); }

        /** Serialise for JS interop. */
        public String toJson() {
            return "{\"id\":\""    + esc(id)    + "\",\"name\":\""  + esc(name) +
                   "\",\"color\":\"" + esc(color) + "\",\"visible\":" + visible + "}";
        }

        private static String esc(String s) {
            return s.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }

    // =========================================================================
    // CalendarEvent – immutable domain object
    // =========================================================================

    /**
     * Immutable representation of a calendar event used for Java ↔ JS interop.
     * Build via {@link Builder}; parse from a JSON string via {@link #fromJson(String)}.
     */
    public static final class CalendarEvent {

        private static final Logger log = LoggerFactory.getLogger(CalendarEvent.class);

        private static final ConcurrentHashMap<String, Pattern> STR_PATTERNS  = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, Pattern> BOOL_PATTERNS = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, Pattern> INT_PATTERNS  = new ConcurrentHashMap<>();
        private static final Pattern META_PATTERN =
                Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");

        private final String id;
        private final String title;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final String description;
        private final String location;
        private final String color;
        private final String colorText;
        private final boolean allDay;
        private final int repeatEvery;
        private final String repeatUnit;
        private final String organizerName;
        private final String organizerEmailAddress;
        private final String url;
        private final String group;
        private final Map<String, String> meta;

        private CalendarEvent(Builder b) {
            this.id                    = b.id != null ? b.id : UUID.randomUUID().toString();
            this.title                 = b.title != null ? b.title : "(no title)";
            this.start                 = b.start;
            this.end                   = b.end;
            this.description           = nvl(b.description);
            this.location              = nvl(b.location);
            this.color                 = (b.color     != null && !b.color.isEmpty())     ? b.color     : "#1a73e8";
            this.colorText             = (b.colorText != null && !b.colorText.isEmpty()) ? b.colorText : "#ffffff";
            this.allDay                = b.allDay;
            this.repeatEvery           = b.repeatEvery;
            this.repeatUnit            = (b.repeatUnit != null && !b.repeatUnit.isEmpty()) ? b.repeatUnit : "NONE";
            this.organizerName         = nvl(b.organizerName);
            this.organizerEmailAddress = nvl(b.organizerEmailAddress);
            this.url                   = nvl(b.url);
            this.group                 = nvl(b.group);
            this.meta                  = Collections.unmodifiableMap(new HashMap<>(b.meta));
        }

        private static String nvl(String s) { return s != null ? s : ""; }

        public String             getId()                    { return id; }
        public String             getTitle()                 { return title; }
        public LocalDateTime      getStart()                 { return start; }
        public LocalDateTime      getEnd()                   { return end; }
        public String             getDescription()           { return description; }
        public String             getLocation()              { return location; }
        public String             getColor()                 { return color; }
        public String             getColorText()             { return colorText; }
        public boolean            isAllDay()                 { return allDay; }
        public int                getRepeatEvery()           { return repeatEvery; }
        /** Repeat cadence – one of the {@link RepeatUnit} name strings, e.g. {@code "WEEKLY"}. */
        public String             getRepeatUnit()            { return repeatUnit; }
        public String             getOrganizerName()         { return organizerName; }
        public String             getOrganizerEmailAddress() { return organizerEmailAddress; }
        public String             getUrl()                   { return url; }
        /** ID of the {@link CalendarGroup} this event belongs to, or empty string. */
        public String             getGroup()                 { return group; }
        public Map<String,String> getMeta()                  { return meta; }

        /** Serialise to a JSON string for JS interop. */
        public String toJson() {
            StringBuilder sb = new StringBuilder("{");
            appendStr(sb, "id",          id);          sb.append(',');
            appendStr(sb, "title",       title);       sb.append(',');
            appendStr(sb, "start",       start != null ? start.format(ISO) : ""); sb.append(',');
            appendStr(sb, "end",         end   != null ? end.format(ISO)   : ""); sb.append(',');
            appendStr(sb, "description", description); sb.append(',');
            appendStr(sb, "location",    location);    sb.append(',');
            appendStr(sb, "color",       color);       sb.append(',');
            appendStr(sb, "colorText",   colorText);   sb.append(',');
            appendBool(sb,"allDay",      allDay);      sb.append(',');
            appendNum(sb, "repeatEvery", repeatEvery); sb.append(',');
            appendStr(sb, "repeatUnit",  repeatUnit);  sb.append(',');
            appendStr(sb, "organizerName",         organizerName);         sb.append(',');
            appendStr(sb, "organizerEmailAddress", organizerEmailAddress); sb.append(',');
            appendStr(sb, "url",         url);         sb.append(',');
            appendStr(sb, "group",       group);       sb.append(',');
            // _meta object
            sb.append('"').append("_meta").append("\":{");
            boolean first = true;
            for (Map.Entry<String,String> e : meta.entrySet()) {
                if (!first) sb.append(',');
                appendStr(sb, e.getKey(), e.getValue());
                first = false;
            }
            sb.append('}');
            sb.append('}');
            return sb.toString();
        }

        // ── JSON helpers ─────────────────────────────────────────────────

        private static void appendStr(StringBuilder sb, String key, String value) {
            sb.append('"').append(jsonEscape(key)).append("\":\"").append(jsonEscape(value)).append('"');
        }

        private static void appendBool(StringBuilder sb, String key, boolean value) {
            sb.append('"').append(jsonEscape(key)).append("\":").append(value);
        }

        private static void appendNum(StringBuilder sb, String key, int value) {
            sb.append('"').append(jsonEscape(key)).append("\":").append(value);
        }

        private static String jsonEscape(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
        }

        /** Parse a {@link CalendarEvent} from a JSON string received from the client. */
        public static CalendarEvent fromJson(String json) {
            if (json == null || json.isBlank()) return new Builder().start(LocalDateTime.now()).end(LocalDateTime.now().plusHours(1)).build();
            Builder b = new Builder()
                .id(          strField(json, "id"))
                .title(       strField(json, "title"))
                .description( strField(json, "description"))
                .location(    strField(json, "location"))
                .color(       strField(json, "color"))
                .colorText(   strField(json, "colorText"))
                .allDay(      boolField(json, "allDay"))
                .repeatEvery( intField(json, "repeatEvery"))
                .repeatUnit(  strField(json, "repeatUnit"))
                .organizerName(         strField(json, "organizerName"))
                .organizerEmailAddress( strField(json, "organizerEmailAddress"))
                .url(         strField(json, "url"))
                .group(       strField(json, "group"));

            String startStr = strField(json, "start");
            String endStr   = strField(json, "end");
            if (!startStr.isEmpty()) { try { b.start(LocalDateTime.parse(startStr, ISO)); } catch (Exception e) { log.debug("Could not parse start date '{}': {}", startStr, e.getMessage()); } }
            if (!endStr.isEmpty())   { try { b.end(LocalDateTime.parse(endStr, ISO));     } catch (Exception e) { log.debug("Could not parse end date '{}': {}",   endStr,   e.getMessage()); } }
            if (b.start == null) b.start(LocalDateTime.now());
            if (b.end   == null) b.end(b.start.plusHours(1));

            // Parse _meta entries
            int metaStart = json.indexOf("\"_meta\"");
            if (metaStart >= 0) {
                int braceOpen  = json.indexOf('{', metaStart + 7);
                int braceClose = json.indexOf('}', braceOpen + 1);
                if (braceOpen >= 0 && braceClose > braceOpen) {
                    String metaBody = json.substring(braceOpen + 1, braceClose);
                    Matcher m = META_PATTERN.matcher(metaBody);
                    while (m.find()) b.meta(m.group(1), m.group(2));
                }
            }
            return b.build();
        }

        // ── Minimal JSON field extractors ────────────────────────────────

        static String strField(String json, String key) {
            Matcher m = STR_PATTERNS
                .computeIfAbsent(key, k -> Pattern.compile("\"" + Pattern.quote(k) + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\""))
                .matcher(json);
            if (m.find()) {
                return m.group(1)
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\")
                    .replace("\\n",  "\n")
                    .replace("\\r",  "\r")
                    .replace("\\t",  "\t");
            }
            return "";
        }

        static boolean boolField(String json, String key) {
            Matcher m = BOOL_PATTERNS
                .computeIfAbsent(key, k -> Pattern.compile("\"" + Pattern.quote(k) + "\"\\s*:\\s*(true|false)"))
                .matcher(json);
            return m.find() && "true".equals(m.group(1));
        }

        static int intField(String json, String key) {
            Matcher m = INT_PATTERNS
                .computeIfAbsent(key, k -> Pattern.compile("\"" + Pattern.quote(k) + "\"\\s*:\\s*(\\d+)"))
                .matcher(json);
            if (m.find()) {
                try { return Integer.parseInt(m.group(1)); } catch (NumberFormatException e) { log.debug("Could not parse int field '{}': {}", key, e.getMessage()); }
            }
            return 0;
        }

        @Override
        public String toString() {
            return "CalendarEvent{id='" + id + "', title='" + title +
                   "', start=" + start + ", end=" + end + ", group='" + group + "'}";
        }

        // ── Builder ───────────────────────────────────────────────────────

        public static final class Builder {
            private String id;
            private String title = "";
            private LocalDateTime start;
            private LocalDateTime end;
            private String description = "";
            private String location    = "";
            private String color       = "#1a73e8";
            private String colorText   = "#ffffff";
            private boolean allDay     = false;
            private int repeatEvery    = 0;
            private String repeatUnit  = "NONE";
            private String organizerName         = "";
            private String organizerEmailAddress = "";
            private String url   = "";
            private String group = "";
            private final Map<String, String> meta = new HashMap<>();

            public Builder id(String v)                    { this.id = v;            return this; }
            public Builder title(String v)                 { this.title = v;         return this; }
            public Builder start(LocalDateTime v)          { this.start = v;         return this; }
            public Builder end(LocalDateTime v)            { this.end = v;           return this; }
            public Builder description(String v)           { this.description = v;   return this; }
            public Builder location(String v)              { this.location = v;      return this; }
            public Builder color(String v)                 { this.color = v;         return this; }
            public Builder colorText(String v)             { this.colorText = v;     return this; }
            public Builder allDay(boolean v)               { this.allDay = v;        return this; }
            public Builder repeatEvery(int v)              { this.repeatEvery = v;   return this; }
            /** Set the repeat cadence using the {@link RepeatUnit} name string. */
            public Builder repeatUnit(String v)            { this.repeatUnit = v;    return this; }
            /** Set the repeat cadence using the {@link RepeatUnit} enum. */
            public Builder repeatUnit(RepeatUnit v)        { this.repeatUnit = v != null ? v.name() : "NONE"; return this; }
            public Builder organizerName(String v)         { this.organizerName = v; return this; }
            public Builder organizerEmailAddress(String v) { this.organizerEmailAddress = v; return this; }
            public Builder url(String v)                   { this.url = v;           return this; }
            /** Associate this event with a {@link CalendarGroup} by its id. */
            public Builder group(String v)                 { this.group = v;         return this; }
            /** Associate this event with a {@link CalendarGroup}. */
            public Builder group(CalendarGroup g)          { this.group = g != null ? g.getId() : ""; return this; }
            /** Apply a Google Calendar {@link EventColor}. */
            public Builder eventColor(EventColor c)        { if (c != null) { this.color = c.getHex(); this.colorText = c.getTextHex(); } return this; }
            public Builder meta(String k, String v)        { this.meta.put(k, v);    return this; }

            public CalendarEvent build() {
                Objects.requireNonNull(start, "start is required");
                Objects.requireNonNull(end,   "end is required");
                if (title == null || title.isBlank()) title = "(no title)";
                return new CalendarEvent(this);
            }
        }
    }

    // =========================================================================
    // CalendarI18n – translatable UI labels pushed to the JS web component
    // =========================================================================

    /**
     * Holds all translatable labels for the {@code <vaadin-calendar>} web component.
     * <p>
     * Each label is a {@link Localizable}: it carries both a <em>message code</em> (looked up via
     * the Holon {@code LocalizationContext} / Vaadin {@code I18NProvider}) and a <em>default message</em>
     * used as the English fallback when no translation is found.
     *
     * <h4>Typical usage – message-code driven</h4>
     * <pre>{@code
     * // In messages_fr.properties:
     * //   vaadin.calendar.today = Aujourd'hui
     * //   vaadin.calendar.cancel = Annuler
     *
     * cal.setI18n(CalendarI18n.defaults());  // codes already point to your bundle
     * }</pre>
     *
     * <h4>Override individual labels</h4>
     * <pre>{@code
     * cal.setI18n(CalendarI18n.defaults()
     *     .today("my.key.today", "Today")
     *     .cancel(Localizable.builder().messageCode("my.key.cancel").message("Cancel").build()));
     * }</pre>
     */
    public static final class CalendarI18n implements Serializable {

        // ── FullCalendar toolbar ───────────────────────────────────────────
        private Localizable today       = lz("vaadin.calendar.today",                 "Today");
        private Localizable month       = lz("vaadin.calendar.month",                 "Month");
        private Localizable week        = lz("vaadin.calendar.week",                  "Week");
        private Localizable day         = lz("vaadin.calendar.day",                   "Day");
        private Localizable agenda      = lz("vaadin.calendar.agenda",                "Agenda");
        private Localizable addEvent    = lz("vaadin.calendar.content.event",             "+ Add Event");

        // ── Sidebar ────────────────────────────────────────────────────────
        private Localizable searchPlaceholder = lz("vaadin.calendar.search.placeholder", "Search events");
        private Localizable myCalendars       = lz("vaadin.calendar.my.calendars",       "My calendars");
        private Localizable ariaToggleSidebar = lz("vaadin.calendar.aria.toggle.sidebar","Toggle sidebar");

        // ── Add / Edit dialog ──────────────────────────────────────────────
        private Localizable newEvent              = lz("vaadin.calendar.new.event",              "New Event");
        private Localizable editEvent             = lz("vaadin.calendar.edit.event",             "Edit Event");
        private Localizable labelTitle            = lz("vaadin.calendar.label.title",            "Title");
        private Localizable placeholderTitle      = lz("vaadin.calendar.placeholder.title",      "Add title");
        private Localizable allDay                = lz("vaadin.calendar.all.day",                "All day");
        private Localizable labelStart            = lz("vaadin.calendar.label.start",            "Start");
        private Localizable labelEnd              = lz("vaadin.calendar.label.end",              "End");
        private Localizable labelDescription      = lz("vaadin.calendar.label.description",      "Description");
        private Localizable placeholderDescription= lz("vaadin.calendar.placeholder.description","Add description");
        private Localizable labelLocation         = lz("vaadin.calendar.label.location",         "Location");
        private Localizable placeholderLocation   = lz("vaadin.calendar.placeholder.location",   "Add location");
        private Localizable labelUrl              = lz("vaadin.calendar.label.url",              "URL");
        private Localizable labelCalendar         = lz("vaadin.calendar.label.calendar",         "Calendar");
        private Localizable noneCalendar          = lz("vaadin.calendar.none.calendar",          "— none —");
        private Localizable labelColor            = lz("vaadin.calendar.label.color",            "Color");
        private Localizable labelRepeat           = lz("vaadin.calendar.label.repeat",           "Repeat");
        private Localizable labelEvery            = lz("vaadin.calendar.label.every",            "Every");
        private Localizable labelUnit             = lz("vaadin.calendar.label.unit",             "Unit");
        private Localizable delete                = lz("vaadin.calendar.delete",                 "Delete");
        private Localizable cancel                = lz("vaadin.calendar.cancel",                 "Cancel");
        private Localizable saveChanges           = lz("vaadin.calendar.save.changes",           "Save changes");
        private Localizable addEventBtn           = lz("vaadin.calendar.content.event.btn",          "Add Event");

        // ── Repeat unit labels ─────────────────────────────────────────────
        private Localizable repeatNone    = lz("vaadin.calendar.repeat.none",    "None");
        private Localizable repeatDaily   = lz("vaadin.calendar.repeat.daily",   "Daily");
        private Localizable repeatWeekly  = lz("vaadin.calendar.repeat.weekly",  "Weekly");
        private Localizable repeatMonthly = lz("vaadin.calendar.repeat.monthly", "Monthly");
        private Localizable repeatYearly  = lz("vaadin.calendar.repeat.yearly",  "Yearly");

        // ── Popover tooltips ───────────────────────────────────────────────
        private Localizable tooltipEdit   = lz("vaadin.calendar.tooltip.edit",   "Edit");
        private Localizable tooltipDelete = lz("vaadin.calendar.tooltip.delete", "Delete");
        private Localizable tooltipClose  = lz("vaadin.calendar.tooltip.close",  "Close");

        private CalendarI18n() {}

        /** Returns a new {@link CalendarI18n} pre-filled with default English labels and standard message codes. */
        public static CalendarI18n defaults() { return new CalendarI18n(); }

        // ── Fluent setters – Localizable ────────────────────────────────────
        public CalendarI18n today(Localizable v)               { this.today = v;                return this; }
        public CalendarI18n month(Localizable v)               { this.month = v;                return this; }
        public CalendarI18n week(Localizable v)                { this.week = v;                 return this; }
        public CalendarI18n day(Localizable v)                 { this.day = v;                  return this; }
        public CalendarI18n agenda(Localizable v)              { this.agenda = v;               return this; }
        public CalendarI18n addEvent(Localizable v)            { this.addEvent = v;             return this; }
        public CalendarI18n searchPlaceholder(Localizable v)   { this.searchPlaceholder = v;    return this; }
        public CalendarI18n myCalendars(Localizable v)         { this.myCalendars = v;          return this; }
        public CalendarI18n ariaToggleSidebar(Localizable v)   { this.ariaToggleSidebar = v;    return this; }
        public CalendarI18n newEvent(Localizable v)            { this.newEvent = v;             return this; }
        public CalendarI18n editEvent(Localizable v)           { this.editEvent = v;            return this; }
        public CalendarI18n labelTitle(Localizable v)          { this.labelTitle = v;           return this; }
        public CalendarI18n placeholderTitle(Localizable v)    { this.placeholderTitle = v;     return this; }
        public CalendarI18n allDay(Localizable v)              { this.allDay = v;               return this; }
        public CalendarI18n labelStart(Localizable v)          { this.labelStart = v;           return this; }
        public CalendarI18n labelEnd(Localizable v)            { this.labelEnd = v;             return this; }
        public CalendarI18n labelDescription(Localizable v)    { this.labelDescription = v;     return this; }
        public CalendarI18n placeholderDescription(Localizable v){ this.placeholderDescription = v; return this; }
        public CalendarI18n labelLocation(Localizable v)       { this.labelLocation = v;        return this; }
        public CalendarI18n placeholderLocation(Localizable v) { this.placeholderLocation = v;  return this; }
        public CalendarI18n labelUrl(Localizable v)            { this.labelUrl = v;             return this; }
        public CalendarI18n labelCalendar(Localizable v)       { this.labelCalendar = v;        return this; }
        public CalendarI18n noneCalendar(Localizable v)        { this.noneCalendar = v;         return this; }
        public CalendarI18n labelColor(Localizable v)          { this.labelColor = v;           return this; }
        public CalendarI18n labelRepeat(Localizable v)         { this.labelRepeat = v;          return this; }
        public CalendarI18n labelEvery(Localizable v)          { this.labelEvery = v;           return this; }
        public CalendarI18n labelUnit(Localizable v)           { this.labelUnit = v;            return this; }
        public CalendarI18n delete(Localizable v)              { this.delete = v;               return this; }
        public CalendarI18n cancel(Localizable v)              { this.cancel = v;               return this; }
        public CalendarI18n saveChanges(Localizable v)         { this.saveChanges = v;          return this; }
        public CalendarI18n addEventBtn(Localizable v)         { this.addEventBtn = v;          return this; }
        public CalendarI18n repeatNone(Localizable v)          { this.repeatNone = v;           return this; }
        public CalendarI18n repeatDaily(Localizable v)         { this.repeatDaily = v;          return this; }
        public CalendarI18n repeatWeekly(Localizable v)        { this.repeatWeekly = v;         return this; }
        public CalendarI18n repeatMonthly(Localizable v)       { this.repeatMonthly = v;        return this; }
        public CalendarI18n repeatYearly(Localizable v)        { this.repeatYearly = v;         return this; }
        public CalendarI18n tooltipEdit(Localizable v)         { this.tooltipEdit = v;          return this; }
        public CalendarI18n tooltipDelete(Localizable v)       { this.tooltipDelete = v;        return this; }
        public CalendarI18n tooltipClose(Localizable v)        { this.tooltipClose = v;         return this; }

        // ── Convenience overloads – (messageCode, defaultMessage) ───────────
        public CalendarI18n today(String code, String msg)               { return today(lz(code, msg)); }
        public CalendarI18n month(String code, String msg)               { return month(lz(code, msg)); }
        public CalendarI18n week(String code, String msg)                { return week(lz(code, msg)); }
        public CalendarI18n day(String code, String msg)                 { return day(lz(code, msg)); }
        public CalendarI18n agenda(String code, String msg)              { return agenda(lz(code, msg)); }
        public CalendarI18n addEvent(String code, String msg)            { return addEvent(lz(code, msg)); }
        public CalendarI18n searchPlaceholder(String code, String msg)   { return searchPlaceholder(lz(code, msg)); }
        public CalendarI18n myCalendars(String code, String msg)         { return myCalendars(lz(code, msg)); }
        public CalendarI18n ariaToggleSidebar(String code, String msg)   { return ariaToggleSidebar(lz(code, msg)); }
        public CalendarI18n newEvent(String code, String msg)            { return newEvent(lz(code, msg)); }
        public CalendarI18n editEvent(String code, String msg)           { return editEvent(lz(code, msg)); }
        public CalendarI18n labelTitle(String code, String msg)          { return labelTitle(lz(code, msg)); }
        public CalendarI18n placeholderTitle(String code, String msg)    { return placeholderTitle(lz(code, msg)); }
        public CalendarI18n allDay(String code, String msg)              { return allDay(lz(code, msg)); }
        public CalendarI18n labelStart(String code, String msg)          { return labelStart(lz(code, msg)); }
        public CalendarI18n labelEnd(String code, String msg)            { return labelEnd(lz(code, msg)); }
        public CalendarI18n labelDescription(String code, String msg)    { return labelDescription(lz(code, msg)); }
        public CalendarI18n placeholderDescription(String code, String msg){ return placeholderDescription(lz(code, msg)); }
        public CalendarI18n labelLocation(String code, String msg)       { return labelLocation(lz(code, msg)); }
        public CalendarI18n placeholderLocation(String code, String msg) { return placeholderLocation(lz(code, msg)); }
        public CalendarI18n labelUrl(String code, String msg)            { return labelUrl(lz(code, msg)); }
        public CalendarI18n labelCalendar(String code, String msg)       { return labelCalendar(lz(code, msg)); }
        public CalendarI18n noneCalendar(String code, String msg)        { return noneCalendar(lz(code, msg)); }
        public CalendarI18n labelColor(String code, String msg)          { return labelColor(lz(code, msg)); }
        public CalendarI18n labelRepeat(String code, String msg)         { return labelRepeat(lz(code, msg)); }
        public CalendarI18n labelEvery(String code, String msg)          { return labelEvery(lz(code, msg)); }
        public CalendarI18n labelUnit(String code, String msg)           { return labelUnit(lz(code, msg)); }
        public CalendarI18n delete(String code, String msg)              { return delete(lz(code, msg)); }
        public CalendarI18n cancel(String code, String msg)              { return cancel(lz(code, msg)); }
        public CalendarI18n saveChanges(String code, String msg)         { return saveChanges(lz(code, msg)); }
        public CalendarI18n addEventBtn(String code, String msg)         { return addEventBtn(lz(code, msg)); }
        public CalendarI18n repeatNone(String code, String msg)          { return repeatNone(lz(code, msg)); }
        public CalendarI18n repeatDaily(String code, String msg)         { return repeatDaily(lz(code, msg)); }
        public CalendarI18n repeatWeekly(String code, String msg)        { return repeatWeekly(lz(code, msg)); }
        public CalendarI18n repeatMonthly(String code, String msg)       { return repeatMonthly(lz(code, msg)); }
        public CalendarI18n repeatYearly(String code, String msg)        { return repeatYearly(lz(code, msg)); }
        public CalendarI18n tooltipEdit(String code, String msg)         { return tooltipEdit(lz(code, msg)); }
        public CalendarI18n tooltipDelete(String code, String msg)       { return tooltipDelete(lz(code, msg)); }
        public CalendarI18n tooltipClose(String code, String msg)        { return tooltipClose(lz(code, msg)); }

        // ── JSON serialisation – resolves each Localizable via LocalizationProvider ──

        /**
         * Resolves all {@link Localizable} labels for the given {@code locale} using
         * the Holon {@code LocalizationContext} / Vaadin {@code I18NProvider}, then
         * serialises them to a flat JSON object for the JS web component.
         *
         * @param locale the locale to resolve against (not null)
         */
        String toJson(Locale locale) {
            StringBuilder sb = new StringBuilder("{");
            a(sb, "today",                resolve(locale, today));                sb.append(',');
            a(sb, "month",                resolve(locale, month));                sb.append(',');
            a(sb, "week",                 resolve(locale, week));                 sb.append(',');
            a(sb, "day",                  resolve(locale, day));                  sb.append(',');
            a(sb, "agenda",               resolve(locale, agenda));               sb.append(',');
            a(sb, "addEvent",             resolve(locale, addEvent));             sb.append(',');
            a(sb, "searchPlaceholder",    resolve(locale, searchPlaceholder));    sb.append(',');
            a(sb, "myCalendars",          resolve(locale, myCalendars));          sb.append(',');
            a(sb, "ariaToggleSidebar",    resolve(locale, ariaToggleSidebar));    sb.append(',');
            a(sb, "newEvent",             resolve(locale, newEvent));             sb.append(',');
            a(sb, "editEvent",            resolve(locale, editEvent));            sb.append(',');
            a(sb, "labelTitle",           resolve(locale, labelTitle));           sb.append(',');
            a(sb, "placeholderTitle",     resolve(locale, placeholderTitle));     sb.append(',');
            a(sb, "allDay",               resolve(locale, allDay));               sb.append(',');
            a(sb, "labelStart",           resolve(locale, labelStart));           sb.append(',');
            a(sb, "labelEnd",             resolve(locale, labelEnd));             sb.append(',');
            a(sb, "labelDescription",     resolve(locale, labelDescription));     sb.append(',');
            a(sb, "placeholderDescription",resolve(locale, placeholderDescription));sb.append(',');
            a(sb, "labelLocation",        resolve(locale, labelLocation));        sb.append(',');
            a(sb, "placeholderLocation",  resolve(locale, placeholderLocation));  sb.append(',');
            a(sb, "labelUrl",             resolve(locale, labelUrl));             sb.append(',');
            a(sb, "labelCalendar",        resolve(locale, labelCalendar));        sb.append(',');
            a(sb, "noneCalendar",         resolve(locale, noneCalendar));         sb.append(',');
            a(sb, "labelColor",           resolve(locale, labelColor));           sb.append(',');
            a(sb, "labelRepeat",          resolve(locale, labelRepeat));          sb.append(',');
            a(sb, "labelEvery",           resolve(locale, labelEvery));           sb.append(',');
            a(sb, "labelUnit",            resolve(locale, labelUnit));            sb.append(',');
            a(sb, "delete",               resolve(locale, delete));               sb.append(',');
            a(sb, "cancel",               resolve(locale, cancel));               sb.append(',');
            a(sb, "saveChanges",          resolve(locale, saveChanges));          sb.append(',');
            a(sb, "addEventBtn",          resolve(locale, addEventBtn));          sb.append(',');
            a(sb, "tooltipEdit",          resolve(locale, tooltipEdit));          sb.append(',');
            a(sb, "tooltipDelete",        resolve(locale, tooltipDelete));        sb.append(',');
            a(sb, "tooltipClose",         resolve(locale, tooltipClose));         sb.append(',');
            // Nested repeatLabels – keyed by RepeatUnit name
            sb.append("\"repeatLabels\":{");
            a(sb, "NONE",    resolve(locale, repeatNone));    sb.append(',');
            a(sb, "DAILY",   resolve(locale, repeatDaily));   sb.append(',');
            a(sb, "WEEKLY",  resolve(locale, repeatWeekly));  sb.append(',');
            a(sb, "MONTHLY", resolve(locale, repeatMonthly)); sb.append(',');
            a(sb, "YEARLY",  resolve(locale, repeatYearly));
            sb.append('}');
            sb.append('}');
            return sb.toString();
        }

        // ── Internal helpers ───────────────────────────────────────────────

        /** Resolve a Localizable to a string for the given locale. */
        private static String resolve(Locale locale, Localizable l) {
            return LocalizationProvider.getLocalization(locale, l)
                    .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
        }

        private static Localizable lz(String code, String defaultMessage) {
            return Localizable.builder().messageCode(code).message(defaultMessage).build();
        }

        private static void a(StringBuilder sb, String key, String value) {
            sb.append('"').append(esc(key)).append("\":\"").append(esc(value != null ? value : "")).append('"');
        }

        private static String esc(String s) {
            return s.replace("\\", "\\\\").replace("\"", "\\\"")
                    .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
        }
    }

    // =========================================================================
    // DOM Event wrappers
    // =========================================================================

    /** Base class for events that carry a {@link CalendarEvent}. */
    public static abstract class CalendarEventComponentEvent extends ComponentEvent<VaadinCalendar> {
        private final CalendarEvent event;
        protected CalendarEventComponentEvent(VaadinCalendar src, boolean fc, CalendarEvent ev) {
            super(src, fc);
            this.event = ev;
        }
        public CalendarEvent getEvent() { return event; }
    }

    @DomEvent("calendar-event-created")
    public static class EventCreatedEvent extends CalendarEventComponentEvent {
        public EventCreatedEvent(VaadinCalendar src, boolean fc,
                @EventData("JSON.stringify(event.detail.event)") String json) {
            super(src, fc, CalendarEvent.fromJson(json));
        }
    }

    @DomEvent("calendar-event-updated")
    public static class EventUpdatedEvent extends CalendarEventComponentEvent {
        public EventUpdatedEvent(VaadinCalendar src, boolean fc,
                @EventData("JSON.stringify(event.detail.event)") String json) {
            super(src, fc, CalendarEvent.fromJson(json));
        }
    }

    @DomEvent("calendar-event-deleted")
    public static class EventDeletedEvent extends CalendarEventComponentEvent {
        private final String deletedId;
        public EventDeletedEvent(VaadinCalendar src, boolean fc,
                @EventData("JSON.stringify(event.detail.event)") String json,
                @EventData("event.detail.id")                    String id) {
            super(src, fc, CalendarEvent.fromJson(json));
            this.deletedId = id;
        }
        public String getDeletedId() { return deletedId; }
    }

    @DomEvent("calendar-event-click")
    public static class EventClickEvent extends CalendarEventComponentEvent {
        public EventClickEvent(VaadinCalendar src, boolean fc,
                @EventData("JSON.stringify(event.detail.event)") String json) {
            super(src, fc, CalendarEvent.fromJson(json));
        }
    }

    @DomEvent("calendar-ready")
    public static class CalendarReadyEvent extends ComponentEvent<VaadinCalendar> {
        public CalendarReadyEvent(VaadinCalendar src, boolean fc) { super(src, fc); }
    }

    @DomEvent("calendar-date-changed")
    public static class DateChangedEvent extends ComponentEvent<VaadinCalendar> {
        private final String date;
        public DateChangedEvent(VaadinCalendar src, boolean fc,
                @EventData("event.detail.date") String date) {
            super(src, fc);
            this.date = date;
        }
        public String getDate() { return date; }
        public LocalDateTime toDateTime() { return LocalDateTime.parse(date, ISO); }
    }

    @DomEvent("calendar-error")
    public static class CalendarErrorEvent extends ComponentEvent<VaadinCalendar> {
        private final String code;
        private final String message;
        public CalendarErrorEvent(VaadinCalendar src, boolean fc,
                @EventData("event.detail.code")    String code,
                @EventData("event.detail.message") String message) {
            super(src, fc);
            this.code    = code;
            this.message = message;
        }
        public String getCode()    { return code; }
        public String getMessage() { return message; }
    }

    /**
     * Fired when the calendar needs events for a new date range (lazy-load mode).
     * <p>
     * Register with {@link #addFetchEventsListener} and respond by calling
     * {@link VaadinCalendar#setEvents(java.util.Collection)} with the events that fall
     * within [{@link #getStart()}, {@link #getEnd()}).
     *
     * <pre>{@code
     * cal.setLazyLoad(true);
     * cal.addFetchEventsListener(e ->
     *     cal.setEvents(service.findByRange(e.getStart(), e.getEnd())));
     * }</pre>
     */
    @DomEvent("calendar-fetch-events")
    public static class FetchEventsEvent extends ComponentEvent<VaadinCalendar> {
        private final LocalDateTime start;
        private final LocalDateTime end;

        public FetchEventsEvent(VaadinCalendar src, boolean fc,
                @EventData("event.detail.start") String start,
                @EventData("event.detail.end")   String end) {
            super(src, fc);
            this.start = parsedt(start);
            this.end   = parsedt(end);
        }

        /** Inclusive start of the visible date range. */
        public LocalDateTime getStart() { return start; }
        /** Exclusive end of the visible date range. */
        public LocalDateTime getEnd()   { return end; }

        private static LocalDateTime parsedt(String s) {
            try { return (s != null && !s.isBlank()) ? LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null; }
            catch (Exception ignored) { return null; }
        }
    }

    /**
     * Fired when the user toggles a {@link CalendarGroup}'s visibility in the sidebar.
     */
    @DomEvent("calendar-group-changed")
    public static class GroupVisibilityChangedEvent extends ComponentEvent<VaadinCalendar> {
        private final String  groupId;
        private final boolean visible;

        public GroupVisibilityChangedEvent(VaadinCalendar src, boolean fc,
                @EventData("event.detail.id")      String  groupId,
                @EventData("event.detail.visible") boolean visible) {
            super(src, fc);
            this.groupId = groupId;
            this.visible = visible;
        }

        public String  getGroupId() { return groupId; }
        public boolean isVisible()  { return visible; }
    }

    // =========================================================================
    // Constructor
    // =========================================================================

    public VaadinCalendar() {
        setWidth("100%");
        setHeight("650px");
    }

    /** Retains the last {@link CalendarI18n} so it can be re-resolved when the locale changes. */
    private CalendarI18n currentI18n;

    // =========================================================================
    // Locale – auto-detection and live sync
    // =========================================================================

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        if (!getElement().hasAttribute("locale")) {
            LocalizationProvider.getCurrentLocale()
                    .ifPresent(l -> getElement().setAttribute("locale", l.toLanguageTag()));
        }
        // Re-push i18n resolved for the current locale (handles first-attach case too)
        if (currentI18n != null) {
            Locale locale = resolveCurrentLocale();
            getElement().callJsFunction("setI18n", currentI18n.toJson(locale));
        }
    }

    /**
     * Called by Vaadin whenever the UI locale changes.
     * Re-applies the FullCalendar locale and re-resolves all {@link CalendarI18n} labels
     * through the Holon {@code LocalizationContext} / {@code I18NProvider} for the new locale.
     */
    @Override
    public void localeChange(LocaleChangeEvent event) {
        Locale locale = event.getLocale();
        getElement().setAttribute("locale", locale.toLanguageTag());
        if (currentI18n != null) {
            getElement().callJsFunction("setI18n", currentI18n.toJson(locale));
        }
    }

    // =========================================================================
    // CRUD API
    // =========================================================================

    /** Add a single event to the calendar. */
    public VaadinCalendar addEvent(CalendarEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        getElement().callJsFunction("addEvents", "[" + event.toJson() + "]");
        return this;
    }

    /** Add multiple events in a single round-trip. */
    public VaadinCalendar addEvents(Collection<CalendarEvent> events) {
        Objects.requireNonNull(events, "events must not be null");
        getElement().callJsFunction("addEvents", buildJsonArray(events));
        return this;
    }

    /** Replace ALL events currently displayed (bulk load). */
    public VaadinCalendar setEvents(Collection<CalendarEvent> events) {
        Objects.requireNonNull(events, "events must not be null");
        getElement().callJsFunction("setEvents", buildJsonArray(events));
        return this;
    }

    /** Update an existing event by id. */
    public VaadinCalendar updateEvent(String id, CalendarEvent patch) {
        Objects.requireNonNull(id,    "id must not be null");
        Objects.requireNonNull(patch, "patch must not be null");
        getElement().callJsFunction("updateEvent", id, patch.toJson());
        return this;
    }

    /** Delete an event by id. */
    public VaadinCalendar deleteEvent(String id) {
        Objects.requireNonNull(id, "id must not be null");
        getElement().callJsFunction("deleteEvent", id);
        return this;
    }

    /** Asynchronously fetch all events currently held by the client component. */
    public void getEvents(Consumer<List<CalendarEvent>> callback) {
        Objects.requireNonNull(callback, "callback must not be null");
        getElement().callJsFunction("getAllEventsJson")
            .then(String.class, json -> {
                List<CalendarEvent> list = parseJsonArray(json);
                callback.accept(Collections.unmodifiableList(list));
            });
    }

    /** Asynchronously fetch a single event by id; {@code null} is passed to the callback if not found. */
    public void getEvent(String id, Consumer<CalendarEvent> callback) {
        Objects.requireNonNull(id,       "id must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        getElement().callJsFunction("getEventJson", id)
            .then(String.class, json ->
                callback.accept("null".equals(json) || json == null ? null : CalendarEvent.fromJson(json))
            );
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    public VaadinCalendar today()    { getElement().callJsFunction("today");    return this; }
    public VaadinCalendar next()     { getElement().callJsFunction("next");     return this; }
    public VaadinCalendar previous() { getElement().callJsFunction("previous"); return this; }

    public VaadinCalendar navigateTo(LocalDateTime date) {
        Objects.requireNonNull(date, "date must not be null");
        getElement().callJsFunction("navigateTo", date.format(ISO));
        return this;
    }

    public VaadinCalendar openAddDialog(LocalDateTime start) {
        if (start != null) getElement().callJsFunction("openAddDialog", start.format(ISO));
        else               getElement().callJsFunction("openAddDialog");
        return this;
    }

    // =========================================================================
    // Configuration
    // =========================================================================

    public VaadinCalendar setView(CalendarView view) {
        Objects.requireNonNull(view, "view must not be null");
        getElement().setAttribute("view", view.name().toLowerCase());
        return this;
    }

    /** Raw-string locale setter (e.g. {@code "fr"}, {@code "de-AT"}). */
    public VaadinCalendar setLocale(String locale) {
        Objects.requireNonNull(locale, "locale must not be null");
        getElement().setAttribute("locale", locale);
        return this;
    }

    /**
     * Type-safe locale setter.
     * Converts the {@link Locale} to an IETF BCP 47 language tag (e.g. {@code "fr-FR"})
     * and forwards it to the underlying FullCalendar instance.
     *
     * @param locale the {@link Locale} to apply (not null)
     */
    public VaadinCalendar setLocale(Locale locale) {
        Objects.requireNonNull(locale, "locale must not be null");
        return setLocale(locale.toLanguageTag());
    }

    public VaadinCalendar setFirstDayOfWeek(int day) {
        getElement().setAttribute("first-day", String.valueOf(day));
        return this;
    }

    public VaadinCalendar setReadOnly(boolean readOnly) {
        if (readOnly) getElement().setAttribute("read-only", "");
        else          getElement().removeAttribute("read-only");
        return this;
    }

    public VaadinCalendar setTheme(CalendarTheme theme) {
        Objects.requireNonNull(theme, "theme must not be null");
        getElement().setAttribute("theme", theme.name().toLowerCase());
        return this;
    }

    /** Show or hide ISO week numbers in the grid. */
    public VaadinCalendar setWeekNumbers(boolean show) {
        if (show) getElement().setAttribute("week-numbers", "true");
        else      getElement().removeAttribute("week-numbers");
        return this;
    }

    /**
     * Highlight business hours in time-grid views.
     *
     * @param startHour hour of day the working day starts (0–23)
     * @param endHour   hour of day the working day ends   (0–23)
     */
    public VaadinCalendar setBusinessHours(int startHour, int endHour) {
        getElement().setAttribute("business-hours", startHour + ":" + endHour);
        return this;
    }

    /** Remove the business-hours highlight. */
    public VaadinCalendar clearBusinessHours() {
        getElement().removeAttribute("business-hours");
        return this;
    }

    /**
     * Set the time zone used for display.
     * Pass a valid IANA timezone string (e.g. {@code "America/New_York"}) or {@code "local"}.
     */
    public VaadinCalendar setTimeZone(String timezone) {
        Objects.requireNonNull(timezone, "timezone must not be null");
        getElement().setAttribute("timezone", timezone);
        return this;
    }

    /** Show or hide the left sidebar (mini-month, search, groups). Default: visible. */
    public VaadinCalendar setSidebarVisible(boolean visible) {
        if (visible) getElement().removeAttribute("sidebar-hidden");
        else         getElement().setAttribute("sidebar-hidden", "");
        return this;
    }

    /** Show or hide the search bar inside the sidebar. Default: visible. */
    public VaadinCalendar setSearchVisible(boolean visible) {
        if (visible) getElement().removeAttribute("search-hidden");
        else         getElement().setAttribute("search-hidden", "");
        return this;
    }

    /**
     * Enable or disable lazy-load mode.
     * <p>
     * When {@code true}, the component fires a {@link FetchEventsEvent} every time
     * the visible date range changes (navigation, view switch). The server must listen
     * with {@link #addFetchEventsListener} and reply by calling
     * {@link #setEvents(java.util.Collection)} with only the events for the requested range.
     * <p>
     * When {@code false} (default), events are loaded once via {@link #setEvents}.
     */
    public VaadinCalendar setLazyLoad(boolean lazy) {
        if (lazy) getElement().setAttribute("lazy-load", "");
        else      getElement().removeAttribute("lazy-load");
        return this;
    }

    /** Returns {@code true} if lazy-load mode is currently enabled. */
    public boolean isLazyLoad() {
        return getElement().hasAttribute("lazy-load");
    }

    /**
     * Push a set of localized UI labels to the calendar web component.
     * <p>
     * Each label in {@link CalendarI18n} is a {@link Localizable}: it carries a message code
     * (looked up via the Holon {@code LocalizationContext} or Vaadin {@code I18NProvider}) plus
     * an English default fallback. Labels are resolved for the <em>current locale</em> at call
     * time and again automatically whenever the UI locale changes.
     *
     * <h4>Zero-config – bundle-driven</h4>
     * <pre>{@code
     * // messages_fr.properties:
     * //   vaadin.calendar.today = Aujourd'hui
     * //   vaadin.calendar.cancel = Annuler
     *
     * cal.setI18n(CalendarI18n.defaults());  // codes already point to your bundle
     * }</pre>
     *
     * <h4>Custom message codes</h4>
     * <pre>{@code
     * cal.setI18n(CalendarI18n.defaults()
     *     .today("app.cal.today", "Today")
     *     .cancel(Localizable.builder()
     *         .messageCode("app.cal.cancel").message("Cancel").build()));
     * }</pre>
     *
     * @param i18n the {@link CalendarI18n} descriptor (not null)
     */
    public VaadinCalendar setI18n(CalendarI18n i18n) {
        Objects.requireNonNull(i18n, "i18n must not be null");
        this.currentI18n = i18n;
        getElement().callJsFunction("setI18n", i18n.toJson(resolveCurrentLocale()));
        return this;
    }

    // =========================================================================
    // Calendar groups API
    // =========================================================================

    /** Replace all calendar groups. */
    public VaadinCalendar setGroups(Collection<CalendarGroup> groups) {
        Objects.requireNonNull(groups, "groups must not be null");
        getElement().callJsFunction("setGroups", buildGroupsJsonArray(groups));
        return this;
    }

    /** Add a single calendar group. */
    public VaadinCalendar addGroup(CalendarGroup group) {
        Objects.requireNonNull(group, "group must not be null");
        getElement().callJsFunction("addGroup", group.toJson());
        return this;
    }

    /** Remove a calendar group by its id. */
    public VaadinCalendar removeGroup(String id) {
        Objects.requireNonNull(id, "id must not be null");
        getElement().callJsFunction("removeGroup", id);
        return this;
    }

    /** Change the visibility of a calendar group. */
    public VaadinCalendar setGroupVisible(String id, boolean visible) {
        Objects.requireNonNull(id, "id must not be null");
        getElement().callJsFunction("setGroupVisible", id, visible);
        return this;
    }

    // =========================================================================
    // Listener registration
    // =========================================================================

    public Registration addEventCreatedListener      (ComponentEventListener<EventCreatedEvent>           l) { return addListener(EventCreatedEvent.class,           l); }
    public Registration addEventUpdatedListener      (ComponentEventListener<EventUpdatedEvent>           l) { return addListener(EventUpdatedEvent.class,           l); }
    public Registration addEventDeletedListener      (ComponentEventListener<EventDeletedEvent>           l) { return addListener(EventDeletedEvent.class,           l); }
    public Registration addEventClickListener        (ComponentEventListener<EventClickEvent>             l) { return addListener(EventClickEvent.class,             l); }
    public Registration addCalendarReadyListener     (ComponentEventListener<CalendarReadyEvent>          l) { return addListener(CalendarReadyEvent.class,          l); }
    public Registration addDateChangedListener       (ComponentEventListener<DateChangedEvent>            l) { return addListener(DateChangedEvent.class,            l); }
    public Registration addErrorListener             (ComponentEventListener<CalendarErrorEvent>          l) { return addListener(CalendarErrorEvent.class,          l); }
    public Registration addGroupVisibilityChangedListener(ComponentEventListener<GroupVisibilityChangedEvent> l) { return addListener(GroupVisibilityChangedEvent.class, l); }
    /** Register a listener for lazy-load fetch requests. */
    public Registration addFetchEventsListener       (ComponentEventListener<FetchEventsEvent>            l) { return addListener(FetchEventsEvent.class,            l); }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private static String buildJsonArray(Collection<CalendarEvent> events) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (CalendarEvent e : events) {
            if (!first) sb.append(',');
            sb.append(e.toJson());
            first = false;
        }
        sb.append(']');
        return sb.toString();
    }

    private static String buildGroupsJsonArray(Collection<CalendarGroup> groups) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (CalendarGroup g : groups) {
            if (!first) sb.append(',');
            sb.append(g.toJson());
            first = false;
        }
        sb.append(']');
        return sb.toString();
    }

    /** Minimal JSON array parser – splits top-level {@code {...}} objects. */
    private static List<CalendarEvent> parseJsonArray(String json) {
        List<CalendarEvent> result = new ArrayList<>();
        if (json == null || json.isBlank() || "[]".equals(json.strip())) return result;
        int depth = 0, start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') { if (depth++ == 0) start = i; }
            else if (c == '}') { if (--depth == 0 && start >= 0) { result.add(CalendarEvent.fromJson(json.substring(start, i + 1))); start = -1; } }
        }
        return result;
    }

    /** Returns the best available locale: UI locale → LocalizationContext → ENGLISH. */
    private Locale resolveCurrentLocale() {
        return LocalizationProvider.getCurrentLocale().orElse(Locale.ENGLISH);
    }
}
