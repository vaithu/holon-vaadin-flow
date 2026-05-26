package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.util.OperationResult;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TestOperationResult {

    @Test
    void testSuccessStatus() {
        assertEquals(OperationResult.Status.SUCCESS, OperationResult.success().getStatus());
    }

    @Test
    void testFailStatus() {
        assertEquals(OperationResult.Status.FAIL, OperationResult.fail().getStatus());
    }

    @Test
    void testSuccessThen() {
        var ran = new AtomicBoolean(false);
        OperationResult.success().then(() -> ran.set(true));
        assertTrue(ran.get());
    }

    @Test
    void testSuccessOtherwise() {
        var ran = new AtomicBoolean(false);
        OperationResult.success().otherwise(() -> ran.set(true));
        assertFalse(ran.get(), "otherwise should not run on success");
    }

    @Test
    void testFailThen() {
        var ran = new AtomicBoolean(false);
        OperationResult.fail().then(() -> ran.set(true));
        assertFalse(ran.get(), "then should not run on fail");
    }

    @Test
    void testFailOtherwise() {
        var ran = new AtomicBoolean(false);
        OperationResult.fail().otherwise(() -> ran.set(true));
        assertTrue(ran.get());
    }

    @Test
    void testSuccessCompose() {
        var counter = new AtomicInteger(0);
        OperationResult result = OperationResult.success()
                .compose(() -> {
                    counter.incrementAndGet();
                    return OperationResult.success();
                });
        assertEquals(1, counter.get());
        assertEquals(OperationResult.Status.SUCCESS, result.getStatus());
    }

    @Test
    void testFailCompose() {
        var counter = new AtomicInteger(0);
        OperationResult result = OperationResult.fail()
                .compose(() -> {
                    counter.incrementAndGet();
                    return OperationResult.success();
                });
        assertEquals(0, counter.get(), "compose should not execute on fail");
        assertEquals(OperationResult.Status.FAIL, result.getStatus());
    }

    @Test
    void testSuccessToString() {
        assertNotNull(OperationResult.success().toString());
    }

    @Test
    void testFailToString() {
        assertNotNull(OperationResult.fail().toString());
    }

    @Test
    void testChaining() {
        var thenRan = new AtomicBoolean(false);
        var otherwiseRan = new AtomicBoolean(false);
        OperationResult.success()
                .then(() -> thenRan.set(true))
                .otherwise(() -> otherwiseRan.set(true));
        assertTrue(thenRan.get());
        assertFalse(otherwiseRan.get());
    }
}
