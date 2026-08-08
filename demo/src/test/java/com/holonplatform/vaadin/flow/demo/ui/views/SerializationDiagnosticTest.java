package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.test.AbstractViewSessionTest;
import com.vaadin.flow.component.Component;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Diagnostic test: finds which component/field causes "Non-serializable lambda"
 * by walking the Vaadin component tree and tracing OOS output.
 */
class SerializationDiagnosticTest extends AbstractViewSessionTest {

    @Test
    void diagnoseProductCrudDemoView() throws Exception {
        ProductService productService = Mockito.mock(ProductService.class, Mockito.withSettings().serializable());
        when(productService.fetch(anyInt(), anyInt(), anyString())).thenReturn(Stream.empty());
        when(productService.fetch(anyInt(), anyInt(), anyString(), any(), any())).thenReturn(Stream.empty());
        when(productService.fetch(anyInt(), anyInt(), anyString(), any(), any(), anyList()))
                .thenReturn(Stream.empty());
        ProductCrudDemoView view = new ProductCrudDemoView(productService);
        System.out.println("\n=== Diagnosing ProductCrudDemoView (reflection hidden-class scan) ===");
        findHiddenNonSerializable(view, "view", new IdentityHashMap<>(), 0);
        System.out.println("=== Done ===\n");
    }


    @Test
    void diagnoseListingBundleDemoView() throws Exception {
        ProductService productService = Mockito.mock(ProductService.class, Mockito.withSettings().serializable());
        when(productService.fetch(anyInt(), anyInt(), anyString())).thenReturn(Stream.empty());
        when(productService.fetch(anyInt(), anyInt(), anyString(), any(), any())).thenReturn(Stream.empty());
        when(productService.fetch(anyInt(), anyInt(), anyString(), any(), any(), anyList()))
                .thenReturn(Stream.empty());
        ListingBundleDemoView view = new ListingBundleDemoView(productService);
        System.out.println("\n=== Diagnosing ListingBundleDemoView (reflection hidden-class scan) ===");
        findHiddenNonSerializable(view, "view", new IdentityHashMap<>(), 0);
        System.out.println("=== Done ===\n");
    }

    // -----------------------------------------------------------------------
    // Reflection-based hidden (lambda) class scanner — no OOS needed
    // -----------------------------------------------------------------------

    /**
     * Walk every non-static, non-transient field reachable from {@code root}
     * and report any hidden (lambda) class instance that does NOT implement Serializable.
     * This approach bypasses OOS internals and works even when ObjectStreamClass.lookup()
     * throws before writeClassDescriptor is called.
     */
    private static void findHiddenNonSerializable(Object obj, String path,
            IdentityHashMap<Object, Boolean> visited, int depth) {
        if (obj == null || depth > 12) return;
        if (visited.containsKey(obj)) return;
        visited.put(obj, Boolean.TRUE);

        Class<?> cls = obj.getClass();

        // Report non-serializable hidden classes (lambdas)
        if (cls.isHidden() && !(obj instanceof Serializable)) {
            System.out.println("[BAD LAMBDA] " + path + "  class=" + cls.getName());
            // Print captured fields
            for (Field f : cls.getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    Object cap = f.get(obj);
                    System.out.println("  capture " + f.getName() + " = "
                            + (cap == null ? "null" : cap.getClass().getName())
                            + (cap instanceof Serializable ? " (Serializable)" : " <<< NOT Serializable"));
                } catch (Exception ignore) {}
            }
            return;  // don't recurse further into the lambda itself
        }

        // Walk fields of this object
        for (Field f : getAllFields(cls)) {
            if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
            if (java.lang.reflect.Modifier.isTransient(f.getModifiers())) continue;
            f.setAccessible(true);
            try {
                Object val = f.get(obj);
                if (val == null) continue;
                String fpath = path + "." + f.getDeclaringClass().getSimpleName() + "." + f.getName()
                        + "<" + val.getClass().getSimpleName() + ">";
                findHiddenNonSerializable(val, fpath, visited, depth + 1);
                // Collections / arrays
                if (val instanceof Iterable<?> iter) {
                    int idx = 0;
                    for (Object elem : iter) {
                        if (elem != null && !visited.containsKey(elem)) {
                            findHiddenNonSerializable(elem, fpath + "[" + idx + "]", visited, depth + 1);
                        }
                        if (++idx > 50) break;
                    }
                } else if (val instanceof Map<?,?> map) {
                    for (Map.Entry<?,?> entry : map.entrySet()) {
                        if (entry.getValue() != null && !visited.containsKey(entry.getValue())) {
                            findHiddenNonSerializable(entry.getValue(), fpath + "{" + entry.getKey() + "}", visited, depth + 1);
                        }
                        if (entry.getKey() != null && !visited.containsKey(entry.getKey())) {
                            findHiddenNonSerializable(entry.getKey(), fpath + "{key:" + entry.getKey() + "}", visited, depth + 1);
                        }
                    }
                } else if (val.getClass().isArray() && !val.getClass().getComponentType().isPrimitive()) {
                    int len = java.lang.reflect.Array.getLength(val);
                    for (int i = 0; i < Math.min(len, 30); i++) {
                        Object elem = java.lang.reflect.Array.get(val, i);
                        if (elem != null && !visited.containsKey(elem)) {
                            findHiddenNonSerializable(elem, fpath + "[" + i + "]", visited, depth + 1);
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        // Also walk Vaadin component children (stored in element tree, not in Java fields)
        if (obj instanceof Component comp) {
            comp.getChildren().forEach(child -> {
                if (!visited.containsKey(child)) {
                    findHiddenNonSerializable(child, path + "->" + child.getClass().getSimpleName(), visited, depth + 1);
                }
            });
        }
    }



    private static void findFailingFields(Object obj, String path, IdentityHashMap<Object, Boolean> visited, int depth) {
        if (obj == null || depth > 15) return;
        if (visited.containsKey(obj)) return;
        visited.put(obj, Boolean.TRUE);

        Class<?> cls = obj.getClass();

        // Try to serialize the object; if it succeeds, nothing to report here
        Exception ex = trySerialize(obj);
        if (ex == null) return;

        // It fails — is it a lambda?
        if (cls.isSynthetic() && cls.getName().contains("$$Lambda")) {
            if (!(obj instanceof Serializable)) {
                System.out.println("[NON-SERIALIZABLE LAMBDA] path=" + path + " class=" + cls.getName());
            } else {
                System.out.println("[SERIALIZABLE LAMBDA CAPTURING BAD OBJECT] path=" + path + " class=" + cls.getName());
                // Try to find which captured field is bad via reflection
                inspectLambdaCaptures(obj, path, visited, depth);
            }
            return;
        }

        // Not a lambda — recurse into all declared fields (no transient skip for diagnostics)
        for (Field f : getAllFields(cls)) {
            if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
            f.setAccessible(true);
            try {
                Object val = f.get(obj);
                if (val == null) continue;
                String fpath = path + "." + f.getDeclaringClass().getSimpleName() + "." + f.getName()
                        + " [" + val.getClass().getSimpleName() + "]";
                if (!visited.containsKey(val)) {
                    findFailingFields(val, fpath, visited, depth + 1);
                }
                // Also recurse into collections / maps / arrays
                if (!visited.containsKey(val)) {
                    traverseContainer(val, fpath, visited, depth);
                }
            } catch (Exception ignored) {}
        }

        // Vaadin component children (not in fields)
        if (obj instanceof Component comp) {
            comp.getChildren().forEach(child -> {
                String cpath = path + " -> " + child.getClass().getSimpleName();
                findFailingFields(child, cpath, visited, depth + 1);
            });
        }
    }

    private static void traverseContainer(Object val, String path, IdentityHashMap<Object, Boolean> visited, int depth) {
        if (val instanceof Iterable<?> iter) {
            int idx = 0;
            for (Object elem : iter) {
                if (elem != null && !visited.containsKey(elem)) {
                    findFailingFields(elem, path + "[" + idx + "]", visited, depth + 1);
                }
                if (++idx > 30) break;
            }
        } else if (val instanceof Map<?,?> map) {
            for (Map.Entry<?,?> entry : map.entrySet()) {
                if (entry.getValue() != null && !visited.containsKey(entry.getValue())) {
                    findFailingFields(entry.getValue(), path + "{" + entry.getKey() + "}", visited, depth + 1);
                }
            }
        } else if (val.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(val);
            for (int i = 0; i < Math.min(len, 20); i++) {
                Object elem = java.lang.reflect.Array.get(val, i);
                if (elem != null && !visited.containsKey(elem)) {
                    findFailingFields(elem, path + "[" + i + "]", visited, depth + 1);
                }
            }
        }
    }

    private static void inspectLambdaCaptures(Object lambda, String path, IdentityHashMap<Object, Boolean> visited, int depth) {
        for (Field f : lambda.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                Object val = f.get(lambda);
                if (val == null) continue;
                System.out.println("  lambda-capture [" + f.getName() + "=" + val.getClass().getName() + "]");
                findFailingFields(val, path + ".capture(" + f.getName() + ")", visited, depth + 1);
            } catch (Exception ignored) {}
        }
    }

    // -----------------------------------------------------------------------
    // Component tree drill-down with tracing OOS
    // -----------------------------------------------------------------------

    private static void diagnoseComponent(Component root, String label) {
        Exception ex = tracedSerialize(root);
        if (ex == null) {
            System.out.println("OK: " + label);
            return;
        }
        System.out.println("FAIL(" + ex.getMessage() + "): " + label);
        root.getChildren().forEach(child ->
                diagnoseComponent(child, label + " -> " + child.getClass().getSimpleName()));
    }

    private static Exception tracedSerialize(Object obj) {
        TrailingTracer tracer = new TrailingTracer(50);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TracingOOS oos = new TracingOOS(baos, tracer);
            System.out.println("  [TracingOOS created, starting writeObject]");
            oos.writeObject(obj);
            oos.close();
            System.out.println("  [TracingOOS: writeObject succeeded, " + tracer.trail.size() + " class descriptors]");
            return null;
        } catch (Exception e) {
            System.out.println("  !! " + e.getMessage() + " – last " + tracer.trail.size() + " classes serialized:");
            tracer.trail.forEach(s -> System.out.println("       " + s));
            return e;
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static Exception trySerialize(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(obj);
            }
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    private static List<Field> getAllFields(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = cls;
        while (current != null && current != Object.class) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        return fields;
    }

    // -----------------------------------------------------------------------
    // Tracing ObjectOutputStream
    // -----------------------------------------------------------------------

    static class TrailingTracer {
        final Deque<String> trail;
        final int capacity;
        TrailingTracer(int capacity) { this.capacity = capacity; trail = new ArrayDeque<>(capacity); }
        void add(String s) {
            if (trail.size() >= capacity) trail.pollFirst();
            trail.addLast(s);
        }
    }

    static class TracingOOS extends ObjectOutputStream {
        private final TrailingTracer tracer;
        TracingOOS(OutputStream out, TrailingTracer tracer) throws IOException {
            super(out);
            this.tracer = tracer;
            enableReplaceObject(true);  // replaceObject() is called for EVERY object, BEFORE hidden-class check
        }
        @Override
        protected Object replaceObject(Object obj) throws IOException {
            if (obj != null) {
                tracer.add(obj.getClass().getName());
            }
            return obj;  // no actual replacement
        }
    }
}
