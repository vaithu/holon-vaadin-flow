package com.holonplatform.vaadin.flow.vaadinplus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Converts JavaBeans / POJOs into a {@link LinkedHashMap} with human-readable, title-cased keys.
 *
 * <p>Uses {@link Class#getDeclaredFields()} to discover properties — this returns only
 * the fields you explicitly declared in the class (and its superclasses up to {@link Object}),
 * never recurses into field types, and excludes all Java reflection / Object machinery.
 *
 * <h3>Key ordering</h3>
 * <ol>
 *   <li>Exact {@code id} field first</li>
 *   <li>Other {@code *Id} / {@code *ID} fields next</li>
 *   <li>Remaining fields in case-insensitive alphabetical order</li>
 * </ol>
 *
 * <h3>Key format</h3>
 * camelCase / PascalCase / snake_case field names are humanized to Title Case.
 * Common acronyms (ID, URL, JSON, UUID, ZIP, …) are preserved in UPPER CASE.
 * <pre>
 *   firstName  →  First Name
 *   ZIPCode    →  ZIP Code
 *   userId     →  User ID
 * </pre>
 *
 * @since 10.0.1
 */
public final class BeanToMap {

    private BeanToMap() {}

    private static void requireNotClassObject(Object bean) {
        if (bean instanceof Class<?>) {
            throw new IllegalArgumentException(
                    "BeanToMap received a Class object (" + ((Class<?>) bean).getName() + ") instead of a bean instance. " +
                    "Pass an actual instance, e.g. KeyValueList.from(product) not KeyValueList.from(Product.class).");
        }
    }

    // ── Field cache ──────────────────────────────────────────────────────────

    /** Cache of (class → list of readable declared fields across the hierarchy). */
    private static final Map<Class<?>, List<FieldAccessor>> FIELD_CACHE = new ConcurrentHashMap<>();

    private record FieldAccessor(String fieldName, Method getter) {}

    private static List<FieldAccessor> accessors(Class<?> type) {
        return FIELD_CACHE.computeIfAbsent(type, BeanToMap::buildAccessors);
    }

    /**
     * Collects all non-static, non-synthetic declared fields from {@code type} and its
     * superclasses (stopping before {@link Object}), matched to their public getter methods.
     */
    private static List<FieldAccessor> buildAccessors(Class<?> type) {
        List<FieldAccessor> result = new ArrayList<>();
        Class<?> cursor = type;
        while (cursor != null && cursor != Object.class) {
            // Never introspect JDK internal classes — they contain no user bean fields
            String pkg = cursor.getPackageName();
            if (pkg.startsWith("java.") || pkg.startsWith("javax.")
                    || pkg.startsWith("jakarta.") || pkg.startsWith("sun.")
                    || pkg.startsWith("com.sun.") || pkg.startsWith("jdk.")) {
                cursor = cursor.getSuperclass();
                continue;
            }
            for (Field field : cursor.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (field.isSynthetic()) continue;

                Method getter = findGetter(cursor, field);
                if (getter != null) {
                    result.add(new FieldAccessor(field.getName(), getter));
                }
            }
            cursor = cursor.getSuperclass();
        }
        return Collections.unmodifiableList(result);
    }

    /** Finds the public getter for a field: {@code getXxx()} or {@code isXxx()} for booleans. */
    private static Method findGetter(Class<?> type, Field field) {
        String name = field.getName();
        String capitalized = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        // try getXxx first, then isXxx (boolean fields)
        for (String prefix : new String[]{"get", "is"}) {
            try {
                Method m = type.getMethod(prefix + capitalized);
                if (Modifier.isPublic(m.getModifiers()) && !m.isSynthetic()) return m;
            } catch (NoSuchMethodException ignored) {}
        }
        return null;
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Flat map of the bean's declared fields: title-case keys, ID-first, then A→Z.
     *
     * @param bean non-null bean instance
     * @param <T>  bean type
     * @return ordered title-case map
     */
    public static <T> Map<String, Object> toTitleCaseMap(T bean) {
        Objects.requireNonNull(bean, "bean must not be null");
        requireNotClassObject(bean);
        return buildMap(bean, Set.of());
    }

    /**
     * Flat map with field exclusion.
     *
     * @param bean        non-null bean instance
     * @param excludeKeys set of raw field names to exclude (e.g. {@code "password"})
     * @param <T>         bean type
     * @return ordered title-case map
     */
    public static <T> Map<String, Object> toTitleCaseMap(T bean, Set<String> excludeKeys) {
        Objects.requireNonNull(bean, "bean must not be null");
        requireNotClassObject(bean);
        return buildMap(bean, excludeKeys != null ? excludeKeys : Set.of());
    }

    /**
     * Pre-warm the field-accessor cache for a type (e.g. at application start-up).
     *
     * @param type bean class to inspect
     */
    public static void prewarmDescriptors(Class<?> type) {
        accessors(type);
    }

    // ── Title-case humanizer (public for standalone use) ─────────────────────

    /**
     * Converts a camelCase / PascalCase / snake_case identifier into human-readable Title Case.
     *
     * <pre>
     *   "firstName"  →  "First Name"
     *   "ZIPCode"    →  "ZIP Code"
     *   "userId"     →  "User ID"
     * </pre>
     *
     * @param name raw field name
     * @return humanized title-case string, or empty string when input is null/blank
     */
    public static String toTitleCase(String name) {
        if (name == null || name.isBlank()) return "";
        List<String> tokens = splitKeepingAcronyms(name);
        StringBuilder sb = new StringBuilder(tokens.size() * 6);
        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);
            String word = isAcronym(t)
                    ? t.toUpperCase(Locale.ROOT)
                    : capitalize(t.toLowerCase(Locale.ROOT));
            if (i > 0) sb.append(' ');
            sb.append(word);
        }
        return sb.toString();
    }

    // ── Internals ────────────────────────────────────────────────────────────

    private record Entry(String rawName, String titleKey, Object value) {}

    private static <T> Map<String, Object> buildMap(T bean, Set<String> excludeKeys) {
        List<Entry> entries = new ArrayList<>();
        for (FieldAccessor fa : accessors(bean.getClass())) {
            if (excludeKeys.contains(fa.fieldName())) continue;
            try {
                Object value = fa.getter().invoke(bean);
                entries.add(new Entry(fa.fieldName(), toTitleCase(fa.fieldName()), value));
            } catch (Exception ignored) {
                // skip any field whose getter throws
            }
        }

        entries.sort((a, b) -> {
            boolean aExact = isExactId(a.rawName()), bExact = isExactId(b.rawName());
            if (aExact && !bExact) return -1;
            if (!aExact && bExact) return  1;

            boolean aLike = isIdLike(a.rawName()), bLike = isIdLike(b.rawName());
            if (aLike && !bLike)   return -1;
            if (!aLike && bLike)   return  1;

            return String.CASE_INSENSITIVE_ORDER
                    .thenComparing(Comparator.naturalOrder())
                    .compare(a.titleKey(), b.titleKey());
        });

        Map<String, Object> out = new LinkedHashMap<>();
        for (Entry e : entries) out.put(e.titleKey(), e.value());
        return out;
    }

    private static boolean isExactId(String name) {
        return "id".equalsIgnoreCase(name);
    }

    private static boolean isIdLike(String name) {
        return isExactId(name) || name.toLowerCase(Locale.ROOT).endsWith("id");
    }

    // ── Title-case helpers ───────────────────────────────────────────────────

    private static List<String> splitKeepingAcronyms(String s) {
        if (s.isEmpty()) return List.of();
        s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (i > 0 && isBoundary(s.charAt(i - 1), ch)) {
                out.add(cur.toString());
                cur.setLength(0);
            }
            cur.append(ch);
        }
        if (!cur.isEmpty()) out.add(cur.toString());
        return out;
    }

    private static boolean isBoundary(char prev, char curr) {
        if (prev == '_' || prev == '-') return true;
        if (Character.isDigit(prev) != Character.isDigit(curr)) return true;
        return Character.isLowerCase(prev) && Character.isUpperCase(curr);
    }

    private static boolean isAcronym(String token) {
        String t = token.replaceAll("[_\\-]", "");
        if (t.length() <= 1) return false;
        Set<String> known = Set.of(
                "ID", "URL", "HTML", "XML", "JSON", "CSV", "UUID",
                "ZIP", "IP", "UI", "API", "GPU", "CPU", "SSN");
        return known.contains(t.toUpperCase(Locale.ROOT))
                || t.equals(t.toUpperCase(Locale.ROOT));
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}

