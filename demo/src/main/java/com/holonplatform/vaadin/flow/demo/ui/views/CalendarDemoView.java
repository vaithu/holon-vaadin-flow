package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.calendar.VaadinCalendar;
import com.holonplatform.vaadin.flow.calendar.VaadinCalendar.CalendarEvent;
import com.holonplatform.vaadin.flow.calendar.VaadinCalendar.CalendarTheme;
import com.holonplatform.vaadin.flow.calendar.VaadinCalendar.CalendarView;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.util.List;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link VaadinCalendar} component.
 *
 * <p>Covers four key scenarios:
 * <ol>
 *   <li>Basic calendar – default month view, read-write</li>
 *   <li>Pre-loaded events – bulk {@code setEvents()} on ready</li>
 *   <li>Read-only – no add/edit/delete, agenda view</li>
 *   <li>Dark theme – dark variant with custom primary color</li>
 * </ol>
 */
@PageTitle("Calendar – Holon Demo")
@Route(value = "calendar", layout = DemoMainLayout.class)
public class CalendarDemoView extends Div {

    public CalendarDemoView() {
        addClassName("app-view");

        var heading = new H1("VaadinCalendar");

        var description = new Paragraph(
            "A full-featured calendar component powered by Calendar.js. " +
            "Supports month / week / day / agenda views, drag-and-drop event editing, " +
            "dark theme, and first-class Java event listeners for every CRUD action."
        );

        add(heading, description,
            example1_basic(),
            example2_preloaded(),
            example3_readOnly(),
            example4_darkTheme()
        );
    }

    // ── Example 1: basic calendar ─────────────────────────────────────────

    private DemoExample example1_basic() {
        var cal = new VaadinCalendar();

        cal.addCalendarReadyListener(e ->
            cal.addEvent(new CalendarEvent.Builder()
                .title("Welcome Event")
                .start(LocalDateTime.now().withHour(10).withMinute(0))
                .end(LocalDateTime.now().withHour(11).withMinute(0))
                .color("#1a73e8")
                .description("Auto-created when the calendar is ready")
                .build())
        );

        return new DemoExample("1 – Basic (month view, read-write)", cal, """
                var cal = new VaadinCalendar();

                cal.addCalendarReadyListener(e ->
                    cal.addEvent(new CalendarEvent.Builder()
                        .title("Welcome Event")
                        .start(LocalDateTime.now().withHour(10).withMinute(0))
                        .end(LocalDateTime.now().withHour(11).withMinute(0))
                        .color("#1a73e8")
                        .description("Auto-created when the calendar is ready")
                        .build())
                );

                add(cal);
                """);
    }

    // ── Example 2: pre-loaded events ──────────────────────────────────────

    private DemoExample example2_preloaded() {
        var cal = new VaadinCalendar();

        var now = LocalDateTime.now();
        var events = List.of(
            new CalendarEvent.Builder()
                .title("Sprint Planning")
                .start(now.withDayOfMonth(1).withHour(9).withMinute(0))
                .end(now.withDayOfMonth(1).withHour(10).withMinute(30))
                .color("#34a853")
                .build(),
            new CalendarEvent.Builder()
                .title("Team Lunch")
                .start(now.withDayOfMonth(5).withHour(12).withMinute(0))
                .end(now.withDayOfMonth(5).withHour(13).withMinute(0))
                .color("#fbbc04")
                .build(),
            new CalendarEvent.Builder()
                .title("Production Release")
                .start(now.withDayOfMonth(15).withHour(14).withMinute(0))
                .end(now.withDayOfMonth(15).withHour(15).withMinute(0))
                .color("#ea4335")
                .description("v2.1.0 goes live")
                .build(),
            new CalendarEvent.Builder()
                .title("All-Hands Meeting")
                .start(now.withDayOfMonth(20).withHour(10).withMinute(0))
                .end(now.withDayOfMonth(20).withHour(11).withMinute(0))
                .color("#a142f4")
                .allDay(false)
                .build()
        );

        cal.addCalendarReadyListener(e -> cal.setEvents(events));

        return new DemoExample("2 – Pre-loaded events (bulk setEvents)", cal, """
                var cal = new VaadinCalendar();

                var events = List.of(
                    new CalendarEvent.Builder()
                        .title("Sprint Planning")
                        .start(now.withDayOfMonth(1).withHour(9).withMinute(0))
                        .end(now.withDayOfMonth(1).withHour(10).withMinute(30))
                        .color("#34a853").build(),
                    new CalendarEvent.Builder()
                        .title("Team Lunch")
                        .start(now.withDayOfMonth(5).withHour(12).withMinute(0))
                        .end(now.withDayOfMonth(5).withHour(13).withMinute(0))
                        .color("#fbbc04").build()
                );

                cal.addCalendarReadyListener(e -> cal.setEvents(events));
                """);
    }

    // ── Example 3: read-only, agenda view ────────────────────────────────

    private DemoExample example3_readOnly() {
        var cal = new VaadinCalendar();
        cal.setReadOnly(true);
        cal.setView(CalendarView.AGENDA);
        cal.setHeight("400px");

        var now = LocalDateTime.now();
        cal.addCalendarReadyListener(e -> cal.setEvents(List.of(
            new CalendarEvent.Builder()
                .title("Board Meeting")
                .start(now.plusDays(1).withHour(9).withMinute(0))
                .end(now.plusDays(1).withHour(10).withMinute(0))
                .color("#1a73e8").build(),
            new CalendarEvent.Builder()
                .title("Code Review")
                .start(now.plusDays(2).withHour(14).withMinute(0))
                .end(now.plusDays(2).withHour(15).withMinute(30))
                .color("#34a853").build(),
            new CalendarEvent.Builder()
                .title("Offsite Workshop")
                .start(now.plusDays(5).withHour(8).withMinute(0))
                .end(now.plusDays(5).withHour(17).withMinute(0))
                .color("#a142f4")
                .allDay(false)
                .location("Conference Center, Building B")
                .build()
        )));

        return new DemoExample("3 – Read-only, agenda view", cal, """
                var cal = new VaadinCalendar();
                cal.setReadOnly(true);
                cal.setView(CalendarView.AGENDA);
                cal.setHeight("400px");

                // No add/edit/delete – users can only view events.
                cal.addCalendarReadyListener(e -> cal.setEvents(upcomingEvents));
                """);
    }

    // ── Example 4: dark theme ─────────────────────────────────────────────

    private DemoExample example4_darkTheme() {
        var cal = new VaadinCalendar();
        cal.setTheme(CalendarTheme.DARK);

        var now = LocalDateTime.now();
        cal.addCalendarReadyListener(e -> cal.setEvents(List.of(
            new CalendarEvent.Builder()
                .title("Midnight Deploy")
                .start(now.withDayOfMonth(10).withHour(0).withMinute(0))
                .end(now.withDayOfMonth(10).withHour(2).withMinute(0))
                .color("#8ab4f8").build(),
            new CalendarEvent.Builder()
                .title("Night Shift Stand-up")
                .start(now.withDayOfMonth(12).withHour(22).withMinute(0))
                .end(now.withDayOfMonth(12).withHour(22).withMinute(30))
                .color("#f28b82").build()
        )));

        return new DemoExample("4 – Dark theme", cal, """
                var cal = new VaadinCalendar();
                cal.setTheme(CalendarTheme.DARK);

                // CSS custom properties control all colours in dark mode:
                //   --vaadin-calendar-primary
                //   --vaadin-calendar-surface
                //   --vaadin-calendar-border
                //   --vaadin-calendar-text
                """);
    }
}

