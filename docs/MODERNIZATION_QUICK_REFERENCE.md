# Java 25 & Spring Boot 4.1 — Quick Modernization Checklist

**Project Status:** ✅ Configured for Java 25 · SB 4.1.0 · Vaadin 25.2.1  
**Generated:** August 27, 2026

---

## What's Working ✅

| Feature | Impact | Status |
|---------|--------|--------|
| Records (Java 16+) | High | ✅ Active |
| Pattern Matching (Java 16+) | High | ✅ Active |
| Switch Expressions (Java 14+) | Medium | ✅ Active |
| Virtual Threads (Java 21+) | Medium | ⚠️ Demo only |
| Stream API + Collectors | Medium | ✅ Active |
| CompletableFuture (async) | High | ✅ Active |

---

## Critical Gaps (Do These First) 🔴

### 1. **NO OBSERVABILITY** ← BIGGEST GAP FOR 10K USERS
- ❌ No `@Timed` on hot paths (Datastore, Auth, components)
- ❌ No Micrometer metrics/instrumentation
- **Impact:** Can't debug 10k user load problems  
**Effort:** 2–3 days  
**ROI:** 10/10 — Essential for production

**Quick Win:**
```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: metrics, health, prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

---

### 2. **NO NATIVE IMAGE SUPPORT**
- ❌ No `@RegisterForReflection` on domain beans
- ❌ No GraalVM metadata
- **Impact:** Can't build fast, small native images  
**Effort:** 3–5 days  
**ROI:** 8/10 — Cloud-native deployment (50ms startup)

---

### 3. **SEALED CLASSES NOT USED**
- ❌ FilterInputBase, PropertyRenderer, DataProvider = open hierarchies
- ❌ Missing exhaustive pattern matching guarantees
- **Impact:** Type safety, JIT optimization  
**Effort:** 4–6 hours  
**ROI:** 7/10 — Prevent runtime errors, speed up JIT

---

### 4. **TEXT BLOCKS NOT USED**
- ❌ Long SQL/JSON strings still use escape sequences
- **Impact:** Code readability  
**Effort:** 1 hour  
**ROI:** 4/10 — Quality-of-life improvement

---

### 5. **VIRTUAL THREADS ONLY IN DEMO**
- ❌ Production code uses platform thread pools
- **Impact:** Lower throughput at 10k concurrent  
**Effort:** 2 hours config + testing  
**ROI:** 8/10 — Scales to 10k users per node

---

## Recommended Execution Plan

### **Week 1: Observability + Sealed Classes**
```
Mon: Add Micrometer @Timed to top 10 hot paths
Tue: Enable actuator/prometheus scraping
Wed: Baseline load test (1k users)
Thu: Seal FilterInputBase hierarchy
Fri: Pattern matching audit + test
```
**Benefit:** Production telemetry + type safety  
**Risk:** Low

### **Week 2: Native Image + Virtual Threads**
```
Mon: Annotate domain beans with @RegisterForReflection
Tue: Add GraalVM plugin, test native build
Wed: Enable virtual thread executor
Thu: Integration test at 500 concurrent
Fri: Load test at 1k, 5k users
```
**Benefit:** 50ms startup, 10x throughput  
**Risk:** Medium (GraalVM can be finicky)

### **Week 3: Async Datastore Adoption**
```
Mon: Audit CompletableFuture usages
Tue: Replace with holon-async-datastore
Wed: Test at 10k concurrent
Thu: Performance tuning (connection pool, cache)
Fri: Production readiness review
```
**Benefit:** Non-blocking I/O, Holon alignment  
**Risk:** Medium (paradigm change)

---

## Impact Matrix

| Feature | Effort | Impact | Priority | Do It |
|---------|--------|--------|----------|-------|
| Add Micrometer | 2d | 🔴 Critical | 1 | **NOW** |
| Sealed classes | 6h | 🟡 High | 2 | **Week 1** |
| GraalVM native | 3d | 🟡 High | 3 | **Week 2** |
| Virtual threads | 2h | 🟡 High | 4 | **Week 1** |
| Text blocks | 1h | 🟢 Low | 5 | **Week 2** |
| Async Datastore | 3d | 🟡 High | 6 | **Week 3** |
| Record patterns | 2d | 🟢 Low | 7 | **Later** |
| Spring Cloud Sleuth | 2d | 🟢 Low | 8 | **Later** |

---

## Feature Details

### Micrometer Setup (MUST DO FIRST)

```java
// Before: No visibility
CompletableFuture.supplyAsync(() -> {
    return datastore.query(Product.TARGET).findMany();
})

// After: Full visibility
@Component
public class ProductService {
    
    private final MeterRegistry meterRegistry;
    
    @Timed(value = "products.fetch", description = "Time to fetch all products")
    public List<Product> getAllProducts() {
        return datastore.query(Product.TARGET).findMany();
    }
}
```

**Metrics available at:** `http://localhost:8080/actuator/prometheus`

---

### Sealed Classes (QUICK WIN)

**Before:**
```java
public abstract class FilterInputBase { }
public class StringFilterInput extends FilterInputBase { }
// Someone adds NumericFilterInput; did you update all switch statements?
```

**After:**
```java
public sealed abstract class FilterInputBase 
    permits StringFilterInput, NumericFilterInput, TemporalFilterInput { }

// Compiler ERROR if you forget a case!
return switch (filter) {
    case StringFilterInput sf -> ...;
    case NumericFilterInput nf -> ...;
    case TemporalFilterInput tf -> ...;
};
```

---

### Virtual Threads (EASY CONFIG)

```yaml
# application.yml
spring:
  threads:
    virtual:
      enabled: true

# That's it! All @Async, scheduled tasks, and async servlet requests now use virtual threads.
```

**Benefit:** Same code, 10x throughput.

---

### GraalVM Native (MEDIUM EFFORT)

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
    <version>0.10.2</version>
</plugin>
```

```bash
# Build native image
mvn -Pnative native:compile

# Run it
./target/holon-vaadin-flow-app

# Check startup time
time ./target/holon-vaadin-flow-app --help
# Expected: ~50ms (vs 2–3 seconds JVM)
```

**Requires:**
- `@RegisterForReflection` on all domain beans + Vaadin components
- GraalVM JDK 21+ installed
- Test thoroughly (native compilation can expose subtle bugs)

---

## Load Testing Baseline

**Before optimizations:**
```bash
k6 run --vus 1000 --duration 5m load-test.js
# Expected: 200–400ms p95 latency, 5–10% error rate
```

**After Week 3 (Observability + Virtual Threads + Async Datastore):**
```bash
k6 run --vus 10000 --duration 10m load-test.js
# Target: <100ms p95 latency, <1% error rate, 10k concurrent sustainable
```

---

## Files to Read Next

1. **`docs/MODERNIZATION_GAP_ANALYSIS.md`** — Full details on all gaps
2. **`pom.xml`** — Already configured correctly for Java 25 + SB 4.1.0
3. **`.claude/skills/logging-patterns`** — Structured logging (complements Micrometer)
4. **`.claude/skills/spring-boot/SKILL.md`** — Spring Boot patterns (virtual threads, etc.)

---

## One-Liner: The Single Most Important Thing

> **Add Micrometer observability to hot paths.** Without it, you can't debug 10k user load problems. Everything else is optimization.

```java
@Timed(value = "datastore.query", description = "Datastore query duration")
@Transactional(readOnly = true)
public List<Product> findAll() {
    return datastore.query(Product.TARGET).findMany();
}
```

Then enable:
```yaml
management.endpoints.web.exposure.include: metrics,health,prometheus
```

**Time investment: 30 minutes. ROI: Infinite (can't operate without it).**

---

## Resources

- 📖 Full analysis: `docs/MODERNIZATION_GAP_ANALYSIS.md`
- 🔗 Java 25 features: https://openjdk.org/jeps/
- 🔗 Spring Boot 4.1: https://spring.io/projects/spring-boot
- 🔗 Micrometer: https://micrometer.io/
- 🔗 GraalVM Native: https://www.graalvm.org/
- 🔗 Sealed Classes: https://openjdk.org/jeps/409

