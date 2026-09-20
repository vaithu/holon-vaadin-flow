package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.FormSection;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Guards the fixes made for high-concurrency, replicated deployments: generated ids must stay
 * unique when components are built in parallel, and component state that Vaadin keeps in the
 * {@code VaadinSession} must survive Java serialization.
 */
class TestConcurrencyAndSerialization {

    private static final int THREADS = 16;
    private static final int SECTIONS_PER_THREAD = 250;

    @Test
    @DisplayName("FormSection generates unique title ids when built concurrently")
    void formSectionIdsAreUniqueUnderConcurrency() throws Exception {
        final Set<String> ids = ConcurrentHashMap.newKeySet();
        final CountDownLatch startGate = new CountDownLatch(1);
        final ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        try {
            final List<Future<Integer>> futures = new java.util.ArrayList<>();
            for (int t = 0; t < THREADS; t++) {
                futures.add(pool.submit((Callable<Integer>) () -> {
                    startGate.await();
                    int produced = 0;
                    for (int i = 0; i < SECTIONS_PER_THREAD; i++) {
                        FormSection section = FormSection.of("Section", new TextField());
                        ids.add(titleIdOf(section));
                        produced++;
                    }
                    return produced;
                }));
            }
            // Release every thread at once to maximise the chance of interleaving the id increment.
            startGate.countDown();

            int produced = 0;
            for (Future<Integer> f : futures) {
                produced += f.get(60, TimeUnit.SECONDS);
            }
            assertEquals(THREADS * SECTIONS_PER_THREAD, produced, "all sections should be built");
            assertEquals(produced, ids.size(),
                    "every concurrently built FormSection must get a distinct title id");
        } finally {
            pool.shutdownNow();
        }
    }

    /** Reads the id the section put on its title element and wired into {@code aria-labelledby}. */
    private static String titleIdOf(FormSection section) {
        String ariaLabelledBy = section.getChildren()
                .map(c -> c.getElement().getAttribute("aria-labelledby"))
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new AssertionError("form layout should reference the title id"));
        assertTrue(ariaLabelledBy.startsWith("form-section-"), "unexpected id: " + ariaLabelledBy);
        return ariaLabelledBy;
    }

    @Test
    @DisplayName("A ResponsiveDiv with lazy slots survives session serialization")
    void responsiveDivWithSlotsIsSerializable() throws Exception {
        ResponsiveDiv div = ResponsiveDiv.flex()
                .slotOnce(ViewMode.MOBILE, () -> new Span("mobile"))
                .slotOnce(ViewMode.DESKTOP, () -> new Span("desktop"))
                .build();

        ResponsiveDiv restored = roundTrip(div);
        assertNotNull(restored, "the div must round-trip through serialization");
    }

    @Test
    @DisplayName("A MasterDetailLayout with handlers survives session serialization")
    void masterDetailLayoutWithHandlersIsSerializable() throws Exception {
        MasterDetailLayout<String> layout = new MasterDetailLayout<>();
        layout.addSyncDispatcher(item -> {
            /* no-op: only its serializability matters here */ });
        layout.setAccentColorProvider(item -> "mdl-accent--blue");
        layout.setInitialItemSupplier(() -> Optional.of("first"));

        MasterDetailLayout<String> restored = roundTrip(layout);
        assertNotNull(restored, "the layout must round-trip through serialization");
    }

    @SuppressWarnings("unchecked")
    private static <T> T roundTrip(T value) throws Exception {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(value);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (T) in.readObject();
        }
    }
}
