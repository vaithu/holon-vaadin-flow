# Observability Patterns for Holon Vaadin Flow Applications

> This guide shows how to add Micrometer/Prometheus observability to your Holon Vaadin Flow application.
> Works with Spring Boot 4.1.0 and Vaadin 25.2.1.

---

## Quick Start: Enable Prometheus Metrics (5 minutes)

### Step 1: Add Spring Boot Actuator (Maven)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Step 2: Configure application.properties

```properties
# Spring Boot Actuator + Prometheus
management.endpoints.web.exposure.include=health,metrics,prometheus,env
management.endpoints.web.base-path=/actuator
management.metrics.export.prometheus.enabled=true

# Optional: SLO-based histogram buckets (latency targets)
management.metrics.distribution.slo.http.server.requests=10ms,50ms,100ms,500ms,1s,5s
management.metrics.distribution.percentiles-histogram.http.server.requests=true
```

### Step 3: Start Application & Check Metrics

```bash
mvn spring-boot:run

# In another terminal:
curl http://localhost:8080/actuator/prometheus | head -20
```

Expected output:
```prometheus
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap"} 1.23456789E8

# HELP http_server_requests_seconds 
# TYPE http_server_requests_seconds histogram
http_server_requests_seconds_bucket{method="GET",status="200",uri="/products",le="0.01"} 45
http_server_requests_seconds_bucket{method="GET",status="200",uri="/products",le="0.05"} 187
http_server_requests_seconds_bucket{method="GET",status="200",uri="/products",le="0.5"} 1543
http_server_requests_seconds_bucket{method="GET",status="200",uri="/products",le="+Inf"} 1634
http_server_requests_seconds_count{method="GET",status="200",uri="/products"} 1634
http_server_requests_seconds_sum{method="GET",status="200",uri="/products"} 389.23
```

Done! You now have production metrics on every HTTP request.

---

## Pattern 1: @Timed on Service Methods

The simplest way to add metrics to your business logic.

### Service Layer Example

```java
@Service
@Transactional(readOnly = true)
public class ProductService {
    
    private final Datastore datastore;
    
    public ProductService(Datastore datastore) {
        this.datastore = datastore;
    }
    
    /**
     * Find all products.
     * 
     * Metric: "product.service.findAll" (histogram)
     *   - Count: number of calls
     *   - Sum: total time
     *   - p50/p95/p99: latency percentiles
     */
    @Timed(
        value = "product.service.findAll",
        description = "Time to find all products",
        longTask = false
    )
    public List<Product> findAll() {
        return datastore.query(Product.TARGET)
            .sort(Product.NAME.asc())
            .findMany();
    }
    
    /**
     * Find a single product by ID.
     */
    @Timed(
        value = "product.service.findById",
        description = "Time to find product by ID"
    )
    public Optional<Product> findById(Long id) {
        return datastore.query(Product.TARGET)
            .filter(Product.ID.eq(id))
            .findOne();
    }
    
    /**
     * Save a product.
     */
    @Transactional
    @Timed(
        value = "product.service.save",
        description = "Time to save a product"
    )
    public Product save(Product product) {
        return datastore.save(product);
    }
}
```

**Metrics exported:**
- `product_service_findAll_seconds` — histogram (call time)
- `product_service_findAll_seconds_max` — gauge (max time since last reset)
- `product_service_findByI d_seconds` — histogram
- `product_service_save_seconds` — histogram

### View It in Prometheus

```bash
curl http://localhost:8080/actuator/prometheus | grep product_service
```

Output:
```prometheus
# HELP product_service_findAll_seconds Time to find all products
# TYPE product_service_findAll_seconds histogram
product_service_findAll_seconds_bucket{class="ProductService",method="findAll",le="0.005"} 0
product_service_findAll_seconds_bucket{class="ProductService",method="findAll",le="0.01"} 12
product_service_findAll_seconds_bucket{class="ProductService",method="findAll",le="0.05"} 145
product_service_findAll_seconds_bucket{class="ProductService",method="findAll",le="0.1"} 198
product_service_findAll_seconds_bucket{class="ProductService",method="findAll",le="+Inf"} 200
product_service_findAll_seconds_count{class="ProductService",method="findAll"} 200
product_service_findAll_seconds_sum{class="ProductService",method="findAll"} 8.234
```

---

## Pattern 2: Manual Counter for Business Events

Track custom events that don't fit the @Timed pattern.

```java
@Service
public class OrderService {
    
    private final MeterRegistry meterRegistry;
    
    public OrderService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    @Transactional
    public Order placeOrder(OrderRequest request) {
        // ... validation + creation logic ...
        
        // Record successful order
        Counter.builder("order.created")
            .description("Number of orders created")
            .tag("source", request.getSource())      // e.g., "web", "mobile", "api"
            .tag("currency", request.getCurrency())  // e.g., "USD", "EUR"
            .tag("region", request.getRegion())      // e.g., "US", "EU", "APAC"
            .register(meterRegistry)
            .increment();
        
        return order;
    }
    
    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        // ... cancellation logic ...
        
        // Record cancellation with reason
        Counter.builder("order.cancelled")
            .description("Number of orders cancelled")
            .tag("reason", reason)  // e.g., "user_request", "payment_failed", "timeout"
            .register(meterRegistry)
            .increment();
    }
}
```

**Metrics exported:**
- `order_created_total{source="web",currency="USD",region="US"}` — counter
- `order_cancelled_total{reason="user_request"}` — counter

---

## Pattern 3: Explicit Timer for Complex Operations

When you need full control over timing logic.

```java
@Service
public class ReportService {
    
    private final MeterRegistry meterRegistry;
    
    public ReportService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    public void generateQuarterlyReport(int year, int quarter) {
        // Start timer
        Timer.Sample sample = Timer.start();
        
        try {
            // Complex multi-step operation
            fetchData(year, quarter);
            processData();
            generatePDF();
            
            // Record success
            sample.stop(Timer.builder("report.generate.time")
                .description("Time to generate quarterly report")
                .tag("year", String.valueOf(year))
                .tag("quarter", "Q" + quarter)
                .tag("status", "success")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry));
            
        } catch (Exception e) {
            // Record failure
            sample.stop(Timer.builder("report.generate.time")
                .description("Time to generate quarterly report")
                .tag("year", String.valueOf(year))
                .tag("quarter", "Q" + quarter)
                .tag("status", "error")
                .tag("error_type", e.getClass().getSimpleName())
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry));
            throw e;
        }
    }
    
    private void fetchData(int year, int quarter) { /* ... */ }
    private void processData() { /* ... */ }
    private void generatePDF() { /* ... */ }
}
```

---

## Pattern 4: Gauge for Resource Monitoring

Track resource counts and states in real-time.

```java
@Component
public class SessionMetricsTracker {
    
    private final MeterRegistry meterRegistry;
    private final VaadinSessionRepository sessionRepository;
    
    public SessionMetricsTracker(MeterRegistry meterRegistry, VaadinSessionRepository repo) {
        this.meterRegistry = meterRegistry;
        this.sessionRepository = repo;
        
        // Register gauge that reports active session count
        meterRegistry.gauge("vaadin.sessions.active", 
            "Active Vaadin sessions",
            this::getActiveSessions);
        
        // Register gauge for memory usage
        meterRegistry.gauge("vaadin.sessions.memory.bytes",
            "Total memory used by sessions",
            this::getTotalSessionMemory);
    }
    
    private int getActiveSessions() {
        return sessionRepository.count();
    }
    
    private long getTotalSessionMemory() {
        return sessionRepository.getTotalMemoryUsage();
    }
}
```

**Metrics exported:**
```prometheus
# HELP vaadin_sessions_active Active Vaadin sessions
# TYPE vaadin_sessions_active gauge
vaadin_sessions_active 245

# HELP vaadin_sessions_memory_bytes Total memory used by sessions
# TYPE vaadin_sessions_memory_bytes gauge
vaadin_sessions_memory_bytes 6.4765432E7
```

---

## Pattern 5: View Lifecycle Instrumentation

Track when views are loaded and rendered.

```java
@View("products")
public class ProductListView extends Composite<VerticalLayout> {
    
    private final MeterRegistry meterRegistry;
    private final ProductService productService;
    
    public ProductListView(MeterRegistry meterRegistry, ProductService productService) {
        this.meterRegistry = meterRegistry;
        this.productService = productService;
    }
    
    @Override
    protected VerticalLayout initContent() {
        var layout = new VerticalLayout();
        layout.add(createGrid());
        return layout;
    }
    
    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        
        // Start timer for view rendering
        Timer.Sample sample = Timer.start();
        
        try {
            // Load data
            var products = productService.findAll();
            
            // Record successful view load
            sample.stop(Timer.builder("view.attach.time")
                .description("Time to attach and render view")
                .tag("view", "ProductListView")
                .tag("status", "success")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry));
            
            // Increment page view counter
            Counter.builder("view.pageview")
                .description("Number of view pageviews")
                .tag("view", "ProductListView")
                .register(meterRegistry)
                .increment();
                
        } catch (Exception e) {
            sample.stop(Timer.builder("view.attach.time")
                .description("Time to attach and render view")
                .tag("view", "ProductListView")
                .tag("status", "error")
                .tag("error", e.getClass().getSimpleName())
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry));
            throw e;
        }
    }
    
    private Grid<Product> createGrid() { /* ... */ }
}
```

---

## Prometheus Query Examples

Once metrics are collected, query them in Prometheus:

### Query 1: Average Request Latency Over Last 5 Minutes

```promql
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])
```

### Query 2: 95th Percentile Latency

```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

### Query 3: Error Rate (5xx responses)

```promql
rate(http_server_requests_seconds_count{status=~"5.."}[1m]) / 
rate(http_server_requests_seconds_count[1m])
```

### Query 4: Product Service Throughput

```promql
rate(product_service_findAll_seconds_count[1m])
```

### Query 5: Memory Usage Over Time

```promql
jvm_memory_used_bytes{area="heap"}
```

---

## Grafana Dashboard Setup (Optional)

### Step 1: Add Prometheus Data Source

```
URL: http://localhost:9090
```

### Step 2: Create Dashboard Panel

Query:
```promql
histogram_quantile(0.95, rate(product_service_findAll_seconds_bucket[5m]))
```

Panel: Graph  
Title: "Product Service p95 Latency"  
Legend: Show

---

## Best Practices

1. **Always include `longTask = false`** in @Timed for short operations (<10s)
   ```java
   @Timed(value = "...", longTask = false)  // For fast operations (correct)
   @Timed(value = "...")                      // For long operations
   ```

2. **Use tags for cardinality** — but don't create unbounded tag values
   ```java
   // Good: Limited, known values
   .tag("status", status)  // e.g., "active", "inactive", "pending"
   
   // Bad: Unbounded cardinality (will exhaust memory)
   .tag("userId", userId)  // Don't do this! Can be 10k different values
   ```

3. **Publish percentiles for SLO tracking**
   ```java
   .publishPercentiles(0.5, 0.95, 0.99)  // Measure p50, p95, p99
   ```

4. **Set meaningful descriptions**
   ```java
   .description("Time to fetch all products from Datastore")
   ```

5. **Monitor the monitors** — add metrics to your metrics infrastructure itself
   ```java
   Counter.builder("metrics.exported")
       .description("Number of metrics exported")
       .register(meterRegistry)
       .increment();
   ```

---

## Troubleshooting

### Metrics Not Appearing?

```bash
# 1. Check actuator is enabled
curl http://localhost:8080/actuator

# 2. Check specific metrics endpoint
curl http://localhost:8080/actuator/metrics/product_service_findAll_seconds

# 3. Restart app (new @Timed annotations require app restart)
mvn spring-boot:run

# 4. Check logs for errors
grep -i metric *.log | grep -i error
```

### Too Many Metrics?

Set a limit in application.properties:
```properties
management.metrics.tags.application=myapp
management.metrics.enable.jvm=false
management.metrics.enable.logback=false
```

### Prometheus Not Scraping?

Verify data:
```bash
curl http://localhost:8080/actuator/prometheus \
  | grep "^[^#]" \
  | head -20
```

Should see metric lines like:
```
product_service_findAll_seconds_count 200
product_service_findAll_seconds_sum 8.234
```

---

## References

- **Micrometer Docs:** https://micrometer.io/docs
- **Spring Boot Actuator:** https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html
- **Prometheus Docs:** https://prometheus.io/docs/
- **Java @Timed Annotation:** https://micrometer.io/docs/concepts#_timed

---

**Last Updated:** August 27, 2026

