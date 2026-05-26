package com.holonplatform.vaadin.flow.calendar;

import com.holonplatform.vaadin.flow.calendar.VaadinCalendar.CalendarEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link VaadinCalendar.CalendarEvent} – pure model tests,
 * no Vaadin UI context required.
 */
class CalendarEventTest {

    private static final LocalDateTime START = LocalDateTime.of(2025, 6, 15, 10, 0);
    private static final LocalDateTime END   = LocalDateTime.of(2025, 6, 15, 11, 30);

    // ── Builder ──────────────────────────────────────────────────────────────

    @Test
    void builderSetsAllFields() {
        CalendarEvent ev = new CalendarEvent.Builder()
            .id("evt-1")
            .title("Team Meeting")
            .start(START)
            .end(END)
            .description("Weekly sync")
            .location("Room A")
            .color("#34a853")
            .colorText("#ffffff")
            .allDay(false)
            .repeatEvery(0)
            .organizerName("Alice")
            .organizerEmailAddress("alice@example.com")
            .url("https://meet.example.com")
            .meta("project", "alpha")
            .build();

        assertThat(ev.getId()).isEqualTo("evt-1");
        assertThat(ev.getTitle()).isEqualTo("Team Meeting");
        assertThat(ev.getStart()).isEqualTo(START);
        assertThat(ev.getEnd()).isEqualTo(END);
        assertThat(ev.getDescription()).isEqualTo("Weekly sync");
        assertThat(ev.getLocation()).isEqualTo("Room A");
        assertThat(ev.getColor()).isEqualTo("#34a853");
        assertThat(ev.getColorText()).isEqualTo("#ffffff");
        assertThat(ev.isAllDay()).isFalse();
        assertThat(ev.getRepeatEvery()).isZero();
        assertThat(ev.getOrganizerName()).isEqualTo("Alice");
        assertThat(ev.getOrganizerEmailAddress()).isEqualTo("alice@example.com");
        assertThat(ev.getUrl()).isEqualTo("https://meet.example.com");
        assertThat(ev.getMeta()).containsEntry("project", "alpha");
    }

    @Test
    void builderGeneratesIdWhenMissing() {
        CalendarEvent ev = new CalendarEvent.Builder()
            .title("Auto ID")
            .start(START)
            .end(END)
            .build();
        assertThat(ev.getId()).isNotBlank();
    }

    @Test
    void builderDefaultsBlankTitleToPlaceholder() {
        CalendarEvent ev = new CalendarEvent.Builder()
            .start(START)
            .end(END)
            .build();
        assertThat(ev.getTitle()).isEqualTo("(no title)");
    }

    @Test
    void builderRequiresStart() {
        assertThatNullPointerException().isThrownBy(() ->
            new CalendarEvent.Builder().end(END).build()
        );
    }

    @Test
    void builderRequiresEnd() {
        assertThatNullPointerException().isThrownBy(() ->
            new CalendarEvent.Builder().start(START).build()
        );
    }

    @Test
    void metaIsUnmodifiable() {
        CalendarEvent ev = minimal();
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> ev.getMeta().put("k", "v"));
    }

    // ── JSON round-trip ───────────────────────────────────────────────────

    @Test
    void toJsonProducesAllFields() {
        CalendarEvent ev = new CalendarEvent.Builder()
            .id("evt-2")
            .title("Demo")
            .start(START)
            .end(END)
            .color("#fbbc04")
            .allDay(true)
            .meta("ticketRef", "JIRA-42")
            .build();

        String json = ev.toJson();

        assertThat(json).contains("\"id\":\"evt-2\"");
        assertThat(json).contains("\"title\":\"Demo\"");
        assertThat(json).contains("\"allDay\":true");
        assertThat(json).contains("\"color\":\"#fbbc04\"");
        assertThat(json).contains("\"ticketRef\":\"JIRA-42\"");
    }

    @Test
    void fromJsonRoundTrip() {
        CalendarEvent original = new CalendarEvent.Builder()
            .id("round-1")
            .title("Round Trip")
            .start(START)
            .end(END)
            .description("test desc")
            .color("#ea4335")
            .meta("key", "value")
            .build();

        String json           = original.toJson();
        CalendarEvent copy    = CalendarEvent.fromJson(json);

        assertThat(copy.getId()).isEqualTo(original.getId());
        assertThat(copy.getTitle()).isEqualTo(original.getTitle());
        assertThat(copy.getStart()).isEqualTo(original.getStart());
        assertThat(copy.getEnd()).isEqualTo(original.getEnd());
        assertThat(copy.getDescription()).isEqualTo(original.getDescription());
        assertThat(copy.getColor()).isEqualTo(original.getColor());
        assertThat(copy.getMeta()).containsEntry("key", "value");
    }

    @Test
    void fromJsonHandlesMissingOptionalFields() {
        String json = "{\"id\":\"minimal\",\"title\":\"Minimal\"," +
                      "\"start\":\"2025-06-15T10:00:00\",\"end\":\"2025-06-15T11:00:00\"}";

        CalendarEvent ev = CalendarEvent.fromJson(json);

        assertThat(ev.getId()).isEqualTo("minimal");
        assertThat(ev.getDescription()).isEmpty();
        assertThat(ev.getLocation()).isEmpty();
        assertThat(ev.getColor()).isEqualTo("#1a73e8");
        assertThat(ev.getMeta()).isEmpty();
    }

    @Test
    void fromJsonHandlesAllDayFlag() {
        String json = "{\"id\":\"allday-1\",\"title\":\"All Day\"," +
                      "\"start\":\"2025-06-15T00:00:00\",\"end\":\"2025-06-15T23:59:59\"," +
                      "\"allDay\":true}";

        CalendarEvent ev = CalendarEvent.fromJson(json);
        assertThat(ev.isAllDay()).isTrue();
    }

    @Test
    void fromJsonHandlesAllDayFalse() {
        String json = "{\"id\":\"ev-1\",\"title\":\"Normal\"," +
                      "\"start\":\"2025-06-15T09:00:00\",\"end\":\"2025-06-15T10:00:00\"," +
                      "\"allDay\":false}";

        CalendarEvent ev = CalendarEvent.fromJson(json);
        assertThat(ev.isAllDay()).isFalse();
    }

    // ── Defaults ─────────────────────────────────────────────────────────

    @Test
    void defaultColorIsGoogleBlue() {
        CalendarEvent ev = minimal();
        assertThat(ev.getColor()).isEqualTo("#1a73e8");
        assertThat(ev.getColorText()).isEqualTo("#ffffff");
    }

    @Test
    void toStringContainsIdAndTitle() {
        CalendarEvent ev = new CalendarEvent.Builder()
            .id("x")
            .title("My Event")
            .start(START)
            .end(END)
            .build();
        assertThat(ev.toString()).contains("x").contains("My Event");
    }

    // ── JSON escaping ─────────────────────────────────────────────────────

    @Test
    void toJsonEscapesSpecialCharacters() {
        CalendarEvent ev = new CalendarEvent.Builder()
            .title("Say \"Hello\"")
            .description("Line1\nLine2")
            .start(START)
            .end(END)
            .build();

        String json = ev.toJson();
        // title must have escaped quotes
        assertThat(json).contains("\\\"Hello\\\"");
        // description must have escaped newline
        assertThat(json).contains("\\n");

        // round-trip must restore the original values
        CalendarEvent copy = CalendarEvent.fromJson(json);
        assertThat(copy.getTitle()).isEqualTo("Say \"Hello\"");
        assertThat(copy.getDescription()).isEqualTo("Line1\nLine2");
    }

    @Test
    void fromJsonNullOrBlankReturnsDefault() {
        assertThat(CalendarEvent.fromJson(null)).isNotNull();
        assertThat(CalendarEvent.fromJson("")).isNotNull();
        assertThat(CalendarEvent.fromJson("   ")).isNotNull();
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private CalendarEvent minimal() {
        return new CalendarEvent.Builder()
            .title("Minimal")
            .start(START)
            .end(END)
            .build();
    }
}

