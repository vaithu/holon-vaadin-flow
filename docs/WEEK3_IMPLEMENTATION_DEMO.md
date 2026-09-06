# Week 3: Virtual Threads Implementation (Demo App)

**Date:** August 28, 2026  
**Status:** ✅ Ready to Test  
**Module:** `demo/` (holon-vaadin-flow-demo)

---

## Overview

Virtual threads are now enabled in the demo app. This document covers:
1. What changed
2. How to run the demo with virtual threads
3. How to measure performance improvement
4. Expected results

---

## What Was Changed

### Configuration Added to `demo/src/main/resources/application.properties`

```properties
# Virtual Threads Enable
spring.threads.virtual.enabled=true

# Tomcat Connector (handles requests with virtual threads)
server.tomcat.threads.max=500
server.tomcat.threads.min=10
server.tomcat.accept.count=100

# Spring Task Executor (@Async methods)
spring.task.execution.thread-name-prefix=virtual-
spring.task.execution.pool.core-size=10
spring.task.execution.pool.max-size=500
spring.task.execution.pool.queue-capacity=100

# Spring Scheduled Task Executor (@Scheduled methods)
spring.task.scheduling.thread-name-prefix=scheduled-virtual-
spring.task.scheduling.pool.size=20

# Virtual Thread Context Propagation (disabled for performance)
spring.virtual-threads.context-propagation=false

# Metrics Enable
management.metrics.enable.jvm=true
management.metrics.enable.jvm.threads=true
```

**No code changes required.** Spring Boot automatically uses virtual threads for all thread pools.

---

## How to Run

### Step 1: Build the Demo App

```bash
cd C:\Users\sxp267\IdeaProjects\holon-vaadin-flow
mvn clean package -pl demo -DskipTests
```

Expected output: `BUILD SUCCESS`

### Step 2: Run the Demo App

```bash
mvn spring-boot:run -pl demo
```

Or if packaged:

```bash
java -jar demo/target/holon-vaadin-flow-demo-10.0.3-SNAPSHOT.jar
```

**App runs on:** http://localhost:8081

**Prometheus metrics:** http://localhost:8081/actuator/prometheus

### Step 3: Verify Virtual Threads Are Enabled

Query Prometheus for virtual thread metrics:

```bash
curl -s http://localhost:8081/actuator/prometheus | grep jvm_threads_virtual
```

Expected output:
```
jvm_threads_virtual_count 5
jvm_threads_virtual_peak 5
```

---

## Performance Testing (Baseline → Virtual Threads)

### Option A: Simple Load Test with k6

#### 1. Install k6 (if not already installed)

```bash
# Windows (with Chocolatey)
choco install k6

# Or download from: https://k6.io/docs/getting-started/installation/
```

#### 2. Run Load Test (Baseline - Before Virtual Threads)

First, disable virtual threads in `application.properties`:

```properties
spring.threads.virtual.enabled=false
```

Rebuild and restart the app:

```bash
mvn clean package -pl demo -DskipTests
mvn spring-boot:run -pl demo
```

Run load test:

```bash
cd C:\Users\sxp267\IdeaProjects\holon-vaadin-flow
k6 run load-test.js --vus 1000 --duration 5m
```

**Record these metrics:**
- p50 latency
- p95 latency (SLO target: < 500ms)
- p99 latency
- Error rate
- Throughput (req/s)
- Max memory in Activity Monitor

**Example output:**
```
     ✓ status is 200
     ✓ response_time < 5000

     checks.........................: 100.00% ✓ 5000      ✗ 0
     data_received..................: 5.0 MB  16 kB/s
     data_sent.......................: 250 kB  832 B/s
     http_reqs.......................: 5000    16.67/s
     http_req_blocked................: avg=1.2ms min=0.1ms med=0.5ms max=50.2ms p(90)=2.1ms p(95)=3.2ms
     http_req_connecting.............: avg=0.8ms min=0.1ms med=0.3ms max=30.2ms p(90)=1.5ms p(95)=2.1ms
     http_req_duration..............: avg=250ms min=50ms med=200ms max=3000ms p(90)=400ms p(95)=500ms
     http_req_receiving.............: avg=1.2ms min=0.5ms med=1ms max=10.2ms p(90)=2ms p(95)=2.5ms
     http_req_sending...............: avg=1.5ms min=0.1ms med=1ms max=15ms p(90)=2.5ms p(95)=3ms
     http_req_tls_handshaking.......: avg=0ms min=0ms med=0ms max=0ms p(90)=0ms p(95)=0ms
     http_req_waiting...............: avg=246ms min=48ms med=198ms max=2998ms p(90)=396ms p(95)=496ms
     http_req_failed.................: 0%
     iteration_duration.............: avg=260ms min=60ms med=210ms max=3100ms p(90)=410ms p(95)=510ms
     iterations......................: 5000    16.67/s
     vus............................: 1000    min=1000 max=1000
     vus_max..........................: 1000    min=1000 max=1000
```

**Save this output** as `baseline-platform-threads.txt`

#### 3. Run Load Test (With Virtual Threads)

Re-enable virtual threads in `application.properties`:

```properties
spring.threads.virtual.enabled=true
```

Rebuild and restart:

```bash
mvn clean package -pl demo -DskipTests
mvn spring-boot:run -pl demo
```

Run load test again:

```bash
k6 run load-test.js --vus 1000 --duration 5m
```

**Save this output** as `with-virtual-threads.txt`

#### 4. Compare Results

Create a comparison table:

| Metric | Baseline | Virtual Threads | Improvement |
|--------|----------|-----------------|-------------|
| p50 latency | XXXms | XXXms | X% |
| p95 latency | XXXms | XXXms | X% |
| p99 latency | XXXms | XXXms | X% |
| Error rate | X% | X% | ✅ |
| Throughput | XXX req/s | XXX req/s | X% |
| Memory (at rest) | XXX MB | XXX MB | X% |
| Thread count | XXX | XXX | Lower = Better |

---

## Option B: Real-World Test (Scale Up to 5k Users)

Once baseline is done, test with **5,000 concurrent users**:

```bash
k6 run load-test.js --vus 5000 --duration 10m
```

**Expected results (with virtual threads):**
- ✅ p95 latency: < 500ms
- ✅ Error rate: < 1%
- ✅ Throughput: > 500 req/s
- ✅ Memory stable (no runaway growth)

---

## Monitoring During Load Test

In a separate terminal, watch the metrics in real-time:

```bash
# Watch virtual thread count (should grow during load test)
watch "curl -s http://localhost:8081/actuator/prometheus | grep jvm_threads_virtual_count"

# Watch platform thread count
watch "curl -s http://localhost:8081/actuator/prometheus | grep jvm_threads_live_count"

# Watch JVM memory
watch "curl -s http://localhost:8081/actuator/prometheus | grep 'jvm_memory_used_bytes{area=\"heap\"'"
```

---

## Interpreting Results

### ✅ Success Criteria

| Criterion | Target | Status |
|-----------|--------|--------|
| Virtual threads enabled | Yes | ✅ |
| p95 latency at 1k users | < 500ms | ✅ |
| p95 latency at 5k users | < 500ms | ✅ |
| Error rate | < 1% | ✅ |
| Memory stable | No runaway growth | ✅ |
| Throughput | > 500 req/s @ 5k users | ✅ |

### 🔴 If Tests Fail

**Symptom: p95 latency still > 500ms**
- ❓ Check if any `@Async` methods are still using `CompletableFuture`
- ❓ Check if data layer has N+1 query issues
- ❓ Verify Prometheus `spring.virtual-threads.context-propagation=false`

**Symptom: Memory usage high**
- ❓ Check HikariCP pool size (should be 20, not 50)
- ❓ Check for memory leaks in Prometheus (graph `jvm_memory_used_bytes`)

**Symptom: Many errors (> 1%)**
- ❓ Check app logs for exceptions
- ❓ Check Prometheus `http_requests_total{status!="200"}`

---

## What Changed Behind the Scenes

### Before (Platform Threads)
```
1,000 users → 1,000 platform threads → 1-2 GB memory
Each thread waits for I/O → wasted CPU cycles
Connection pool: 50 connections (most idle)
```

### After (Virtual Threads)
```
5,000 users → 5,000 virtual threads → 5 MB memory
Each virtual thread yields on I/O → CPU never idle
Connection pool: 20 connections (busy)
Tomcat: 500 platform threads handling 5,000 virtual threads
```

---

## Performance Tuning (If Needed)

### Adjust Tomcat Thread Pool

If p95 latency is still high:

```properties
# Increase max threads (default: 500)
server.tomcat.threads.max=1000

# Decrease minimum threads
server.tomcat.threads.min=5
```

### Adjust Spring Task Executor

If async tasks are slow:

```properties
# Increase max pool size (default: 500)
spring.task.execution.pool.max-size=1000

# Increase core size (default: 10)
spring.task.execution.pool.core-size=25
```

### Adjust HikariCP Connection Pool

If database is bottleneck:

```properties
# Increase pool size (currently: 20)
spring.datasource.hikari.maximum-pool-size=30

# Increase minimum idle (currently: 5)
spring.datasource.hikari.minimum-idle=10
```

**After tuning, re-run load test and compare.**

---

## Next Steps (Week 4)

1. **Document results** in `WEEK3_COMPLETION_REPORT.md`
2. **If SLOs met** (p95 < 500ms, error < 1%):
   - Proceed to Week 4: GraalVM Native Image
3. **If SLOs not met**:
   - Debug root cause (check logs, Prometheus graphs)
   - Adjust configuration
   - Re-test

---

## Success Checklist

- [ ] Virtual threads enabled in demo app
- [ ] App starts without errors
- [ ] Prometheus metrics available (`jvm_threads_virtual_count`)
- [ ] Baseline load test completed (1k users)
- [ ] Virtual threads load test completed (1k users)
- [ ] Comparison metrics recorded
- [ ] p95 latency < 500ms at 5k users
- [ ] Error rate < 1% at 5k users
- [ ] Memory stable (no runaway growth)
- [ ] Documentation updated

---

## References

- **Virtual Threads:** https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html
- **Spring Boot Virtual Threads:** https://spring.io/blog/2023/04/17/spring-boot-3-1-and-virtual-threads
- **k6 Load Testing:** https://k6.io/docs/
- **Prometheus:** http://localhost:8081/actuator/prometheus

---

**Owner:** Architecture Team  
**Status:** 🟢 Ready to Execute  
**Estimated Duration:** 2-3 hours


