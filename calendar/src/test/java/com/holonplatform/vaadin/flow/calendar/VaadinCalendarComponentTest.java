package com.holonplatform.vaadin.flow.calendar;

import com.holonplatform.vaadin.flow.calendar.VaadinCalendar.CalendarEvent;
import com.holonplatform.vaadin.flow.calendar.VaadinCalendar.CalendarTheme;
import com.holonplatform.vaadin.flow.calendar.VaadinCalendar.CalendarView;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Component-level tests for {@link VaadinCalendar}.
 *
 * <p>Tests exercise the Java API (attribute writes, null guards, JSON round-trips)
 * without requiring a live Vaadin UI or browser — following the pattern of the
 * core module's component tests (e.g. TestPagination).
 */
class VaadinCalendarComponentTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2025, 6, 15, 10, 0);

    // =========================================================================
    // @Tag / dimensions
    // =========================================================================

    @Test
    void component_hasCorrectTag() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThat(cal.getElement().getTag()).isEqualTo("vaadin-calendar");
    }

    @Test
    void constructor_setsDefaultWidth() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThat(cal.getWidth()).isEqualTo("100%");
    }

    @Test
    void constructor_setsDefaultHeight() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThat(cal.getHeight()).isEqualTo("650px");
    }

    // =========================================================================
    // setView — maps enum to lowercase attribute
    // =========================================================================

    @Test
    void setView_month_writesAttribute() {
        VaadinCalendar cal = new VaadinCalendar();
        cal.setView(CalendarView.MONTH);
        assertThat(cal.getElement().getAttribute("view")).isEqualTo("month");
    }

    @Test
    void setView_week_writesAttribute() {
        VaadinCalendar cal = new VaadinCalendar();
        cal.setView(CalendarView.WEEK);
        assertThat(cal.getElement().getAttribute("view")).isEqualTo("week");
    }

    @Test
    void setView_day_writesAttribute() {
        VaadinCalendar cal = new VaadinCalendar();
        cal.setView(CalendarView.DAY);
        assertThat(cal.getElement().getAttribute("view")).isEqualTo("day");
    }

    @Test
    void setView_agenda_writesAttribute() {
        VaadinCalendar cal = new VaadinCalendar();
        cal.setView(CalendarView.AGENDA);
        assertThat(cal.getElement().getAttribute("view")).isEqualTo("agenda");
    }

    @Test
    void setView_null_throwsNPE() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThatNullPointerException().isThrownBy(() -> cal.setView(null));
    }

    @Test
    void setView_returnsThis_forChaining() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThat(cal.setView(CalendarView.WEEK)).isSameAs(cal);
    }

    // =========================================================================
    // setTheme
    // =========================================================================

    @Test
    void setTheme_light_writesAttribute() {
        VaadinCalendar cal = new VaadinCalendar();
        cal.setTheme(CalendarTheme.LIGHT);
        assertThat(cal.getElement().getAttribute("theme")).isEqualTo("light");
    }

    @Test
    void setTheme_dark_writesAttribute() {
        VaadinCalendar cal = new VaadinCalendar();
        cal.setTheme(CalendarTheme.DARK);
        assertThat(cal.getElement().getAttribute("theme")).isEqualTo("dark");
    }

    @Test
    void setTheme_null_throwsNPE() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThatNullPointerException().isThrownBy(() -> cal.setTheme(null));
    }

    // =========================================================================
    // setReadOnly
    // =========================================================================

    @Test
    void setReadOnly_true_addsAttribute() {
        VaadinCalendar cal = new VaadinCalendar();
        cal.setReadOnly(true);
        assertThat(cal.getElement().hasAttribute("read-only")).isTrue();
    }

    @Test
    void setReadOnly_false_removesAttribute() {
        VaadinCalendar cal = new VaadinCalendar();
        cal.setReadOnly(true);
        cal.setReadOnly(false);
        assertThat(cal.getElement().hasAttribute("read-only")).isFalse();
    }

    // =========================================================================
    // setLocale
    // =========================================================================

    @Test
    void setLocale_writesAttribute() {
        VaadinCalendar cal = new VaadinCalendar();
        cal.setLocale("fr");
        assertThat(cal.getElement().getAttribute("locale")).isEqualTo("fr");
    }

    @Test
    void setLocale_null_throwsNPE() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThatNullPointerException().isThrownBy(() -> cal.setLocale((String) null));
    }

    // =========================================================================
    // setFirstDayOfWeek
    // =========================================================================

    @Test
    void setFirstDayOfWeek_writesAttribute() {
        VaadinCalendar cal = new VaadinCalendar();
        cal.setFirstDayOfWeek(0);
        assertThat(cal.getElement().getAttribute("first-day")).isEqualTo("0");
    }

    // =========================================================================
    // addEvent / setEvents null guards
    // =========================================================================

    @Test
    void addEvent_null_throwsNPE() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThatNullPointerException()
            .isThrownBy(() -> cal.addEvent(null))
            .withMessage("event must not be null");
    }

    @Test
    void addEvents_null_throwsNPE() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThatNullPointerException()
            .isThrownBy(() -> cal.addEvents(null))
            .withMessage("events must not be null");
    }

    @Test
    void setEvents_null_throwsNPE() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThatNullPointerException()
            .isThrownBy(() -> cal.setEvents(null))
            .withMessage("events must not be null");
    }

    @Test
    void updateEvent_nullId_throwsNPE() {
        VaadinCalendar cal = new VaadinCalendar();
        CalendarEvent ev = new CalendarEvent.Builder().start(NOW).end(NOW.plusHours(1)).build();
        assertThatNullPointerException().isThrownBy(() -> cal.updateEvent(null, ev));
    }

    @Test
    void deleteEvent_null_throwsNPE() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThatNullPointerException().isThrownBy(() -> cal.deleteEvent(null));
    }

    // =========================================================================
    // navigateTo null guard
    // =========================================================================

    @Test
    void navigateTo_null_throwsNPE() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThatNullPointerException().isThrownBy(() -> cal.navigateTo(null));
    }

    // =========================================================================
    // buildJsonArray — used internally by setEvents / addEvents
    // =========================================================================

    @Test
    void setEvents_emptyList_doesNotThrow() {
        VaadinCalendar cal = new VaadinCalendar();
        assertThatNoException().isThrownBy(() -> cal.setEvents(List.of()));
    }

    @Test
    void setEvents_multipleEvents_doesNotThrow() {
        VaadinCalendar cal = new VaadinCalendar();
        List<CalendarEvent> events = List.of(
            new CalendarEvent.Builder().title("A").start(NOW).end(NOW.plusHours(1)).build(),
            new CalendarEvent.Builder().title("B").start(NOW.plusDays(1)).end(NOW.plusDays(1).plusHours(2)).build()
        );
        assertThatNoException().isThrownBy(() -> cal.setEvents(events));
    }
}

