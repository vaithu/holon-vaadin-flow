# Week 2 Plan: Comprehensive Observability & Performance Baseline

**Duration:** September 2–8, 2026  
**Status:** 🟡 Planning Phase  
**Goal:** Instrument hot paths + establish baseline performance metrics

---

## Overview

Week 2 focuses on:
1. ✅ Instrument Datastore services with @Timed
2. ✅ Create k6 load test script
3. ✅ Enable virtual threads in Spring Boot
4. ✅ Seal additional component hierarchies
5. ✅ Run baseline load test at 1k concurrent users

---

## Daily Breakdown

### Day 1 (Mon): Instrument Hot Paths with @Timed

**Objective:** Add observability to top 5 Datastore service methods

**Services to Instrument:**
1. ProductService.findAll() → `datastore.product.findAll`
2. ProductService.findById() → `datastore.product.findById`
3. ProductService.save() → `datastore.product.save`
4. CustomerService.fetch() → `datastore.customer.fetch`
5. CustomerService.save() → `datastore.customer.save`

**Pattern Used:**
```java
@Timed(
    value = "datastore.product.findAll",
    description = "Time to fetch all products from Datastore",
    longTask = false
)
public List<Product> findAll() {
    return helper.getDatastore()
        .query(TARGET)
        .sort(NAME_PROP.asc())
        .stream(BeanProjection.of(Product.class))
        .toList();
}
```

**Expected Metrics:**
- `datastore_product_findAll_seconds` (histogram)
- `datastore_product_findById_seconds` (histogram)
- `datastore_product_save_seconds` (histogram)

---

### Day 2 (Tue): Enable Actuator & Baseline Metrics

**Objective:** Verify Prometheus is scraping metrics

**Tasks:**
1. ✅ Add `spring-boot-starter-actuator` to starter module
2. ✅ Configure `application.properties` with management endpoints
3. ✅ Verify `/actuator/prometheus` responds
4. ✅ Collect first metrics from instrumented services

**Configuration:**
```properties
management.endpoints.web.exposure.include=health,metrics,prometheus,env
management.metrics.export.prometheus.enabled=true
management.metrics.distribution.slo.http.server.requests=10ms,50ms,100ms,500ms,1s,5s
management.metrics.distribution.percentiles-histogram.http.server.requests=true
```

---

### Day 3 (Wed): Create & Run Load Test

**Objective:** Baseline performance at 1k concurrent users

**Load Test Script (k6):**
```javascript
// load-test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  scenarios: {
    ramping: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 100 },   // Ramp to 100
        { duration: '1m', target: 100 },    // Stay at 100
        { duration: '30s', target: 500 },   // Ramp to 500
        { duration: '1m', target: 500 },    // Stay at 500
        { duration: '30s', target: 1000 },  // Ramp to 1000
        { duration: '1m', target: 1000 },   // Stay at 1000
        { duration: '30s', target: 0 },     // Ramp down
      ],
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.05'],
  },
};

export default function () {
  // Test product listing
  let res = http.get('http://localhost:8080/products');
  check(res, {
    'products list status 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
  sleep(1);

  // Test single product fetch
  res = http.get('http://localhost:8080/products/1');
  check(res, {
    'product detail status 200': (r) => r.status === 200,
    'response time < 300ms': (r) => r.timings.duration < 300,
  });
  sleep(1);
}
```

**Run Command:**
```bash
k6 run load-test.js
```

**Capture Metrics:**
```bash
# In another terminal, while load test runs
curl http://localhost:8080/actuator/prometheus | grep datastore_product > baseline-metrics.txt
```

---

### Day 4 (Thu): Seal Additional Hierarchies

**Objective:** Extend type safety to more component types

**Candidates to Seal:**

1. **FilterInputBase** → 5 implementations
   ```java
   public sealed abstract class FilterInputBase 
       permits StringFilterInput, 
               NumericFilterInput, 
               TemporalFilterInput,
               BooleanFilterInput,
               EnumerationFilterInput { }
   ```

2. **PropertyRenderer** → 3 implementations
   ```java
   public sealed interface PropertyRenderer<T> 
       permits StringPropertyRenderer,
               NumericPropertyRenderer,
               TemporalPropertyRenderer { }
   ```

3. **QueryOperator** (if exists) → 8 implementations
   ```java
   public sealed interface QueryOperator 
       permits EQ, NE, GT, LT, GE, LE, 
               CONTAINS, BETWEEN { }
   ```

**Benefit:** Exhaustive switch statements catch missing cases at compile time

---

### Day 5 (Fri): Audit & Verify

**Objective:** Verify all instrumentation works, prepare report

**Tasks:**
1. ✅ Run baseline load test again (verify consistency)
2. ✅ Capture p50/p95/p99 latency metrics
3. ✅ Verify sealed classes compile
4. ✅ Generate Week 2 completion report

**Report Contents:**
- Baseline latency (p50, p95, p99) at 1k concurrent users
- Error rates and throughput
- Top 3 slowest endpoints
- Sealed class coverage (% of hierarchies sealed)
- Recommendations for Week 3

---

## Implementation Details

### Task 1: Add @Timed to Services

**File:** `starter/src/main/java/.../service/ProductService.java`

```java
@Service
@Transactional(readOnly = true)
public class ProductService {
    
    @Timed(
        value = "datastore.product.findAll",
        description = "Time to fetch all products from Datastore",
        longTask = false
    )
    public List<Product> findAll() { ... }
    
    @Timed(
        value = "datastore.product.findById",
        description = "Time to fetch a single product by ID"
    )
    public Optional<Product> findById(Long id) { ... }
    
    @Transactional
    @Timed(
        value = "datastore.product.save",
        description = "Time to save a product"
    )
    public Product save(Product product) { ... }
}
```

### Task 2: Create Load Test

**File:** `load-test.js` (in project root)

```javascript
// Copy script from Day 3 section above
```

### Task 3: Seal FilterInputBase

**File:** `core/src/main/java/.../components/FilterInputBase.java`

Before:
```java
public abstract class FilterInputBase { }
```

After:
```java
public sealed abstract class FilterInputBase 
    permits StringFilterInput, 
            NumericFilterInput, 
            TemporalFilterInput,
            BooleanFilterInput,
            EnumerationFilterInput {
}
```

### Task 4: Update Implementations

Mark all subclasses as `final`:
```java
public final class StringFilterInput extends FilterInputBase { }
public final class NumericFilterInput extends FilterInputBase { }
```

---

## Metrics to Collect

### Datastore Query Metrics
- `datastore_product_findAll_seconds` (all percentiles)
- `datastore_product_findById_seconds`
- `datastore_product_save_seconds`
- `datastore_customer_fetch_seconds`
- `datastore_customer_save_seconds`

### HTTP Request Metrics (automatic from Spring Boot)
- `http_server_requests_seconds` (by endpoint)
- `http_server_requests_seconds_count` (throughput)
- `http_server_requests_seconds_sum` (total time)

### JVM Metrics
- `jvm_memory_used_bytes`
- `jvm_threads_live` (should stay constant at 1k virtual threads)
- `jvm_gc_pause_seconds`

---

## Success Criteria

| Criterion | Target | How to Verify |
|-----------|--------|---------------|
| **Datastore services instrumented** | 5/5 methods | Grep @Timed in code |
| **Actuator enabled** | /actuator/prometheus responds | curl endpoint |
| **Load test successful** | p95 < 500ms | k6 output |
| **No errors under load** | error rate < 1% | k6 thresholds |
| **Sealed classes** | 3+ hierarchies sealed | grep sealed in code |
| **Build passes** | BUILD SUCCESS | mvn clean package |

---

## Load Test Interpretation

**Expected Results at 1k Concurrent Users:**

| Metric | Target | Interpretation |
|--------|--------|-----------------|
| **p50 latency** | < 100ms | Median response time |
| **p95 latency** | < 500ms | 95% of requests fast |
| **p99 latency** | < 1000ms | Only 1% of requests slow |
| **Error rate** | < 1% | System stable |
| **Throughput** | > 100 req/s | Adequate capacity |

**If metrics miss targets:**
- Slow p95: Check for database query issues, add indexes
- High error rate: Check connection pool size, increase from 20 to 50
- Low throughput: Enable virtual threads, check CPU usage

---

## Week 2 Verification Checklist

### Monday (Day 1)
- [ ] Add @Timed to ProductService methods
- [ ] Add @Timed to CustomerService methods
- [ ] Compile and verify no errors

### Tuesday (Day 2)
- [ ] Add `spring-boot-starter-actuator` to starter pom.xml
- [ ] Configure management endpoints in application.properties
- [ ] Verify /actuator/prometheus responds with metrics

### Wednesday (Day 3)
- [ ] Create k6 load-test.js script
- [ ] Run 5-minute load test at 1k concurrent
- [ ] Capture baseline metrics

### Thursday (Day 4)
- [ ] Seal FilterInputBase hierarchy
- [ ] Mark all subclasses as final
- [ ] Verify sealed class pattern matching works
- [ ] Run unit tests

### Friday (Day 5)
- [ ] Generate performance report
- [ ] Document baseline metrics
- [ ] Plan Week 3 optimizations

---

## Dependencies to Add

### starter/pom.xml

```xml
<!-- Spring Boot Actuator (if not already present) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Micrometer Prometheus (usually comes with actuator) -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### Tools Needed

```bash
# Install k6 for load testing
brew install k6              # macOS
choco install k6             # Windows
apt-get install k6           # Linux
```

---

## Example: Sealed Class Benefit

**Before (Open hierarchy):**
```java
// Unknown subclass added by accident
public class UnknownFilterInput extends FilterInputBase { }

// Switch statement doesn't catch new type
switch (filter) {
    case StringFilterInput _ -> ...;
    case NumericFilterInput _ -> ...;
    // RUNTIME ERROR: UnknownFilterInput handled as default case
}
```

**After (Sealed hierarchy):**
```java
// Only 5 known implementations allowed
public sealed abstract class FilterInputBase 
    permits StringFilterInput, NumericFilterInput, ... { }

// Switch statement must handle all 5 cases
switch (filter) {
    case StringFilterInput _ -> ...;
    case NumericFilterInput _ -> ...;
    case TemporalFilterInput _ -> ...;
    case BooleanFilterInput _ -> ...;
    case EnumerationFilterInput _ -> ...;
    // COMPILE-TIME ERROR if any case is missing!
}
```

---

## Next Steps (Week 3)

If Week 2 metrics show:
- ✅ p95 < 500ms → Proceed to Week 3 (native image, async datastore)
- ⚠️ p95 > 500ms → Optimize database queries, add indexes, increase pool size
- 🔴 Error rate > 5% → Debug connection issues, increase virtual thread count

---

## Resources

- **Micrometer Docs:** https://micrometer.io/docs
- **k6 Docs:** https://k6.io/docs/
- **Spring Boot Actuator:** https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html
- **Sealed Classes Guide:** https://openjdk.org/jeps/409

---

**Week 2 Status:** 🟡 Ready to Start  
**Date:** August 27, 2026 (Planning)  
**Next Update:** September 2, 2026 (Execution)

**Owner:** Architecture Team  
**Next Review:** September 8, 2026 (Completion)

