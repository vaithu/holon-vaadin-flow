# Getting Started: Production Observability with Micrometer (2–3 Hours)

> **Status:** Java 25 & Spring Boot 4.1 project correctly configured  
> **Next Step:** Add monitoring to hot paths for 10k concurrent user support  
> **Estimated Time:** 2–3 hours (all steps)  
> **ROI:** Enables production debugging, required for load testing

---

## Step 1: Enable Actuator Endpoints (15 min)

### 1.1 Ensure Spring Boot Actuator Dependency

Check your `spring-boot-starter` or add explicitly:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

The `holon-starter-vaadin-flow` includes this transitively.

### 1.2 Configure application.yml

```yaml
# application.yml or application-prod.yml
spring:
  application:
    name: holon-vaadin-flow-app
  
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,env
      base-path: /actuator
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      slo:
        http.server.requests: 10ms,50ms,100ms,500ms,1s,5s
  endpoint:
    health:
      show-details: when-authorized
    metrics:
      enabled: true
```

### 1.3 Verify It Works

```bash
# Start application
mvn spring-boot:run

# Check metrics endpoint
curl http://localhost:8080/actuator/metrics
# Should return JSON list of all metrics

# Check Prometheus format
curl http://localhost:8080/actuator/prometheus
# Should return Prometheus scrape format
```

---

## Step 2: Instrument Hot Paths (1.5 hours)

### 2.1 Identify Top 5 Hot Paths

Based on §13.1 (AGENTS.md), instrument these priority areas:

| Priority | Path | File | Why |
|----------|------|------|-----|
| 🔴 P0 | Datastore query (findMany) | Service layer | Every UI listing depends on this |
| 🔴 P0 | AuthContext access | @SessionScope bean | 10k sessions need auth checks |
| 🟡 P1 | PropertyForm submission | View handler | User-facing form save |
| 🟡 P1 | Component rendering | View lifecycle | Vaadin view attach/detach |
| 🟢 P2 | Cache hits/misses | Spring Cache | Performance indicator |

### 2.2 Add @Timed Annotation to Datastore Service

**Example: ProductService**

```java
package com.holonplatform.vaadin.flow.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.micrometer.core.annotation.Timed;
import com.holonplatform.core.datastore.Datastore;
// ... other imports

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final Datastore datastore;

    public ProductService(Datastore datastore) {
        this.datastore = datastore;
    }

    /**
     * Fetch all products with pagination.
     * Tracked metric: "datastore.product.findAll" (histogram)
     */
    @Timed(
        value = "datastore.product.findAll",
        description = "Time to fetch all products with paging",
        longTask = false
    )
    public List<Product> findAll(int offset, int limit) {
        return datastore.query(Product.TARGET)
            .sort(Product.ID.asc())
            .limit(limit)
            .offset(offset)
            .findMany();
    }

    /**
     * Find a single product by ID.
     * Tracked metric: "datastore.product.findById"
     */
    @Timed(
        value = "datastore.product.findById",
        description = "Time to fetch a single product by ID"
    )
    public Optional<Product> findById(Long id) {
        return datastore.query(Product.TARGET)
            .filter(Product.ID.eq(id))
            .findOne();
    }

    /**
     * Insert a new product.
     * Tracked metric: "datastore.product.insert"
     */
    @Timed(
        value = "datastore.product.insert",
        description = "Time to insert a product"
    )
    @Transactional
    public Product insert(Product product) {
        return datastore.query(Product.TARGET)
            .insert(new PropertyBox.Builder(Product.PROPERTY_SET).build(product))
            .orElseThrow();
    }

    /**
     * Update a product.
     * Tracked metric: "datastore.product.update"
     */
    @Timed(
        value = "datastore.product.update",
        description = "Time to update a product"
    )
    @Transactional
    public Product update(Product product) {
        datastore.query(Product.TARGET)
            .filter(Product.ID.eq(product.getId()))
            .update(new PropertyBox.Builder(Product.PROPERTY_SET).build(product));
        return product;
    }

    /**
     * Delete a product.
     * Tracked metric: "datastore.product.delete"
     */
    @Timed(
        value = "datastore.product.delete",
        description = "Time to delete a product"
    )
    @Transactional
    public void delete(Long id) {
        datastore.query(Product.TARGET)
            .filter(Product.ID.eq(id))
            .delete();
    }
}
```

### 2.3 Add @Timed to AuthContext Access (Session Scope)

**Example: AuthContext Wrapper**

```java
package com.holonplatform.vaadin.flow.demo.service;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import com.holonplatform.core.auth.AuthContext;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;

@Component
@SessionScope
public class CurrentUserProvider {

    private final AuthContext authContext;
    private final MeterRegistry meterRegistry;

    public CurrentUserProvider(AuthContext authContext, MeterRegistry meterRegistry) {
        this.authContext = authContext;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Get current authenticated account.
     * Tracked metric: "auth.current.account" (counter on each access)
     */
    @Timed(
        value = "auth.current.account",
        description = "Time to fetch current user from AuthContext"
    )
    public Optional<Account> getCurrentAccount() {
        return authContext.getAuthentication()
            .map(auth -> auth.getAccount());
    }

    /**
     * Check if current user has a role.
     * Tracked metric: "auth.hasRole"
     */
    @Timed(
        value = "auth.hasRole",
        description = "Time to check user role"
    )
    public boolean hasRole(String role) {
        return authContext.getAuthentication()
            .map(auth -> auth.getAccount())
            .map(acc -> acc.getRoles().contains(role))
            .orElse(false);
    }
}
```

### 2.4 Add Counter for Custom Business Events

**Example: Track New User Registrations**

```java
@Component
public class UserRegistrationService {

    private final MeterRegistry meterRegistry;

    public UserRegistrationService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Transactional
    public void registerNewUser(UserRegistrationForm form) {
        // Registration logic...
        
        // Increment counter
        meterRegistry.counter("user.registration.successful",
            "source", form.getSource(), // e.g., "web", "api", "mobile"
            "region", form.getRegion()   // e.g., "US", "EU", "APAC"
        ).increment();
    }

    @Transactional
    public void handleRegistrationFailure(UserRegistrationForm form, String reason) {
        meterRegistry.counter("user.registration.failed",
            "reason", reason // e.g., "duplicate_email", "invalid_data"
        ).increment();
    }
}
```

### 2.5 Add Gauge for Resource Monitoring

**Example: Track Active Sessions**

```java
@Component
public class SessionMetricsService {

    private final MeterRegistry meterRegistry;
    private final VaadinSessionRepository sessionRepository;

    public SessionMetricsService(MeterRegistry meterRegistry, VaadinSessionRepository sessionRepository) {
        this.meterRegistry = meterRegistry;
        this.sessionRepository = sessionRepository;

        // Register gauge to track active sessions
        meterRegistry.gaugeCollectionSize("vaadin.sessions.active", 
            emptyList(), 
            sessionRepository::getAllSessions
        );
    }
}
```

---

## Step 3: Load Test & Baseline (1 hour)

### 3.1 Create k6 Load Test Script

**File:** `load-test.js` (in project root or `src/test/k6/`)

```javascript
import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
  scenarios: {
    ramping_load: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 100 },   // Ramp up to 100 users
        { duration: '1m30s', target: 100 }, // Stay at 100 users
        { duration: '30s', target: 500 },   // Ramp up to 500 users
        { duration: '1m', target: 500 },    // Stay at 500 users
        { duration: '30s', target: 0 },     // Ramp down
      ],
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'], // 95th percentile under 500ms
    http_req_failed: ['rate<0.05'],                   // Less than 5% failure
  },
};

export default function () {
  group('Product Listing', () => {
    const res = http.get('http://localhost:8080/products');
    check(res, {
      'status is 200': (r) => r.status === 200,
      'response time < 500ms': (r) => r.timings.duration < 500,
    });
    sleep(1);
  });

  group('Product Details', () => {
    const res = http.get('http://localhost:8080/products/1');
    check(res, {
      'status is 200': (r) => r.status === 200,
      'response time < 300ms': (r) => r.timings.duration < 300,
    });
    sleep(1);
  });

  group('Create Product', () => {
    const payload = JSON.stringify({
      name: 'Test Product ' + __VU,
      description: 'Created during load test',
    });

    const res = http.post('http://localhost:8080/products', payload, {
      headers: { 'Content-Type': 'application/json' },
    });

    check(res, {
      'status is 201': (r) => r.status === 201,
      'response time < 1000ms': (r) => r.timings.duration < 1000,
    });
    sleep(2);
  });
}
```

### 3.2 Run Baseline Test

```bash
# Install k6 (if not already)
# macOS: brew install k6
# Linux: see https://k6.io/docs/get-started/installation/
# Windows: choco install k6

# Start your Spring Boot app
mvn spring-boot:run &

# Wait 10 seconds for startup
sleep 10

# Run load test
k6 run load-test.js
```

**Expected output:**
```
scenarios: (100.00%) 1 scenario, 500 max VUs, 4m0s max duration (includes 30s graceful stop):
  * ramping_load: Up to 500 logins per second

✓ status is 200
✓ status is 201
✓ response time < 500ms
✓ response time < 1000ms

data_received..................: 5.2 MB 2.2 kB/s
data_sent.......................: 3.1 MB 1.3 kB/s
http_req_blocked...............: avg=52.3ms  min=0s       max=1.2s    p(90)=100ms p(95)=120ms
http_req_duration..............: avg=250ms   min=10ms     max=5s      p(90)=400ms p(95)=520ms ❌
http_req_failed................: 2.45%                ❌
http_req_receiving.............: avg=5.2ms   min=1ms      max=100ms   p(90)=10ms p(95)=12ms
http_req_sending...............: avg=3.1ms   min=1ms      max=20ms    p(90)=5ms  p(95)=8ms
http_req_waiting...............: avg=241ms   min=8ms      max=4.9s    p(90)=390ms p(95)=500ms
http_reqs......................: 4832    2.01/s
vus............................: 500     100%
vus_max........................: 500     100%
```

**Analysis:**
- ❌ `p(95)=520ms` exceeds our threshold of 500ms
- ❌ `http_req_failed=2.45%` exceeds our threshold of 5%

These are your baseline metrics to improve.

### 3.3 Check Prometheus Metrics

While load test is running, check Prometheus endpoint:

```bash
# In another terminal:
curl http://localhost:8080/actuator/prometheus | grep datastore_product
```

**Output:**
```
# HELP datastore_product_findAll_seconds_max
# TYPE datastore_product_findAll_seconds_max gauge
datastore_product_findAll_seconds_max{class="ProductService",method="findAll"} 0.523

# HELP datastore_product_findAll_seconds
# TYPE datastore_product_findAll_seconds histogram
datastore_product_findAll_seconds_bucket{class="ProductService",method="findAll",le="0.01"} 2.0
datastore_product_findAll_seconds_bucket{class="ProductService",method="findAll",le="0.05"} 42.0
datastore_product_findAll_seconds_bucket{class="ProductService",method="findAll",le="0.1"} 187.0
datastore_product_findAll_seconds_bucket{class="ProductService",method="findAll",le="0.5"} 4501.0
datastore_product_findAll_seconds_bucket{class="ProductService",method="findAll",le="+Inf"} 4832.0
datastore_product_findAll_seconds_count{class="ProductService",method="findAll"} 4832.0
datastore_product_findAll_seconds_sum{class="ProductService",method="findAll"} 1243.52
```

**Analysis:**
- 4,832 requests executed
- Total time: 1,243.52 seconds
- Average: ~257ms per request
- Tail latency (p95): ~500ms
- Bucket analysis: 4,501 out of 4,832 requests under 500ms (93% < 500ms)

---

## Step 4: Set Up Prometheus Scraping (Optional, 15 min)

If you want centralized metrics collection:

### 4.1 Docker Compose for Prometheus + Grafana

**File:** `docker-compose.observability.yml`

```yaml
version: '3.8'
services:
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus-storage:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_INSTALL_PLUGINS=grafana-piechart-panel
    volumes:
      - grafana-storage:/var/lib/grafana
    depends_on:
      - prometheus

volumes:
  prometheus-storage:
  grafana-storage:
```

### 4.2 Prometheus Configuration

**File:** `prometheus.yml`

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'holon-vaadin-flow'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
```

### 4.3 Start Services

```bash
docker-compose -f docker-compose.observability.yml up -d

# Access Prometheus: http://localhost:9090
# Access Grafana: http://localhost:3000 (admin/admin)
```

---

## Step 5: Interpret Metrics (Understanding the Data)

### 5.1 Key Metrics to Monitor

| Metric | What It Measures | Good Value | Alert If |
|--------|------------------|------------|----------|
| `datastore_product_findAll_seconds_max` | Max query time | < 1s | > 2s |
| `datastore_product_findAll_seconds{quantile="0.95"}` | 95th percentile | < 500ms | > 1s |
| `http_server_requests_seconds{status="500"}` | Server errors | 0 | > 0 |
| `vaadin_sessions_active` | Active sessions | 100–1000 | > 10k (memory leak) |
| `auth_hasRole_seconds` | Auth check duration | < 10ms | > 100ms |

### 5.2 Example Grafana Dashboard Query

```promql
# 95th percentile of product listing time over last 5 minutes
histogram_quantile(0.95, rate(datastore_product_findAll_seconds_bucket[5m]))

# Error rate (5xx responses)
rate(http_server_requests_seconds_count{status=~"5.."}[1m])

# JVM memory usage
jvm_memory_used_bytes{area="heap"}
```

---

## Step 6: Create Alerts (15 min)

### 6.1 Alert Rules File

**File:** `prometheus-alerts.yml`

```yaml
groups:
  - name: holon-vaadin-flow
    interval: 30s
    rules:
      - alert: HighDatabaseQueryLatency
        expr: histogram_quantile(0.95, rate(datastore_product_findAll_seconds_bucket[5m])) > 1
        for: 5m
        annotations:
          summary: "High database query latency detected"
          description: "95th percentile query time is {{ $value }}s"

      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[1m]) > 0.05
        for: 2m
        annotations:
          summary: "High error rate (5xx responses)"
          description: "Error rate is {{ $value }} per second"

      - alert: LowMemory
        expr: |
          (container_memory_usage_bytes / container_memory_max_bytes) > 0.85
        for: 5m
        annotations:
          summary: "JVM memory usage above 85%"
          description: "Memory usage: {{ $value | humanizePercentage }}"

      - alert: TooManySessions
        expr: vaadin_sessions_active > 10000
        for: 1m
        annotations:
          summary: "Excessive active sessions (memory leak?)"
          description: "Active sessions: {{ $value }}"
```

---

## Next Steps After Step 2–3

1. ✅ **Now:** Run baseline load test → understand current performance
2. **Week 1:** Identify slowest queries → add indexes or optimize
3. **Week 2:** Enable caching (`@Cacheable`) on frequently accessed data
4. **Week 3:** Adopt virtual threads config + async datastore
5. **Week 4:** Load test again → compare before/after

---

## Troubleshooting

### Metrics Not Appearing?

```bash
# 1. Check actuator is enabled
curl http://localhost:8080/actuator
# Should list endpoints including "metrics"

# 2. Check @Timed annotation is present
grep -r "@Timed" src/main/java

# 3. Check Spring AOP is active
# Add debug logging:
logging.level.org.springframework.aop: DEBUG

# 4. Rebuild with Maven
mvn clean package -DskipTests
```

### High Latency in Metrics but App Feels Fast?

→ Check connection pool size (HikariCP):

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20  # Too small for 500 concurrent users!
      connection-timeout: 30000
      leak-detection-threshold: 60000
```

For 500 concurrent users: `pool-size ≈ 500 × 0.1 = 50`

### Prometheus Not Scraping?

```bash
# Check Prometheus targets page
curl http://localhost:9090/api/v1/targets

# Manually test scrape
curl http://localhost:8080/actuator/prometheus | head -20
```

---

## Summary: What You've Done

| Step | What | Time |
|------|------|------|
| 1 | Enabled Actuator + Prometheus | 15 min |
| 2 | Added @Timed to 5+ hot paths | 1.5 hr |
| 3 | Ran baseline load test (500 users) | 30 min |
| 4 | Set up Prometheus + Grafana (optional) | 15 min |
| 5 | Understood key metrics | 15 min |
| 6 | Created alerts | 15 min |
| **Total** | **Production-ready observability** | **2.5–3 hours** |

---

## What You Can Now Do

✅ See query latencies → find slow queries  
✅ Track error rates → catch bugs in production  
✅ Monitor session count → detect memory leaks  
✅ Alert on failures → get paged before users complain  
✅ Load test accurately → benchmark optimizations  

---

**Next:** See `docs/MODERNIZATION_QUICK_REFERENCE.md` for Week 2–3 priorities (sealed classes, native image, virtual threads).

