# Week 3-4 Roadmap: Virtual Threads & GraalVM Native Image

**Phase:** 🟡 Planning (Ready to execute after Week 2)  
**Date:** August 28, 2026  
**Target:** Scale to 10,000 concurrent users + native image deployment

---

## Overview

Week 3-4 focuses on advanced modernization:
- Week 3: Virtual threads configuration + performance testing (5k users)
- Week 4: GraalVM native image + final verification (10k users)

---

## Week 3: Virtual Threads & Load Testing

### Day 1: Virtual Threads Setup

**Objective:** Enable Java 21 virtual threads for high concurrency

**Tasks:**
1. Add virtual threads configuration (copy from `docs/VIRTUAL_THREADS_CONFIG.properties`)
2. Reduce connection pool size from 50 to 20
3. Enable thread metrics in Prometheus

**Configuration Example:**
```properties
spring.threads.virtual.enabled=true
spring.datasource.hikari.maximum-pool-size=20
management.metrics.enable.jvm.threads=true
```

**Expected Result:** App runs with virtual threads, Prometheus shows `jvm.threads.virtual.count`

### Day 2-3: Load Testing (5k Users)

**Objective:** Verify performance scales with virtual threads

**Load Test Script:**
```bash
k6 run load-test.js --vus 5000 --duration 10m
```

**Expected Metrics:**
- p50 latency: < 100ms
- p95 latency: < 500ms ✅ (SLO target)
- p99 latency: < 1000ms
- Error rate: < 1%
- Throughput: > 500 req/s

**Monitoring:**
```bash
# Watch metrics during test
watch "curl -s http://localhost:8080/actuator/prometheus | grep jvm_threads_virtual_count"
```

### Day 4: Async Optimization (Optional)

**If load test shows p95 > 500ms:**
1. Identify slowest endpoints
2. Make them async with virtual threads
3. Re-test and compare

**Example:**
```java
// Before: Blocking
@Timed("service.slowOp")
public Result slowOperation() {
    Thread.sleep(500);  // Blocks platform thread
    return ...;
}

// After: Virtual thread (no change needed, just let it sleep!)
// Virtual thread yields, platform thread runs other tasks
@Timed("service.slowOp")
public Result slowOperation() {
    Thread.sleep(500);  // Virtual thread yields
    return ...;
}
```

### Day 5: Report & Analysis

**Deliverables:**
- Week 3 performance report
- Virtual threads impact metrics
- Recommendation for Week 4
- Load test results at 5k concurrent users

---

## Week 4: GraalVM Native Image

### Day 1-2: Native Image Preparation

**Objective:** Build native image with all sealed interfaces

**Preparation:**
1. Verify `reflection-config.json` is complete
2. Create `serialization-config.json` for sealed classes
3. Test compilation with `native-image` tool

**Build Command:**
```bash
mvn clean package -Pnative -DskipTests
```

**Expected Output:**
```
Building native image [holon-vaadin-flow-app-...].
[...]
Finished generating 'holon-vaadin-flow-app' in 45 seconds
```

**Result:** Single executable file (~80-120 MB)

### Day 2-3: Native Image Startup & Performance

**Startup Time Comparison:**
- JAR: 3-5 seconds
- Native Image: 50-100 ms ✅ (40-50× faster!)

**Memory Usage:**
- JAR (JVM): 200-300 MB
- Native Image: 50-80 MB ✅ (3-4× less!)

**Performance Testing:**
```bash
# Start native image
./target/holon-vaadin-flow-app

# In another terminal, run k6 at 10k users
k6 run load-test.js --vus 10000 --duration 10m
```

**Expected Metrics at 10k Users:**
- p95 latency: < 500ms ✅ (still meets SLO!)
- p99 latency: < 1000ms
- Error rate: < 1%
- Throughput: > 2000 req/s
- Memory: 50-80 MB (constant)

### Day 4-5: Production Deployment

**Deployment Checklist:**
- [ ] Native image builds successfully
- [ ] Startup time < 200ms
- [ ] Memory usage < 100 MB
- [ ] Load test passes at 10k users
- [ ] All endpoints respond correctly
- [ ] Metrics work in native image
- [ ] Error handling works
- [ ] Security works (no bypass)

**Docker Deployment:**
```dockerfile
FROM scratch
COPY target/holon-vaadin-flow-app /app
ENTRYPOINT ["/app"]
```

**Result:** Tiny, fast-starting container (< 150 MB)

---

## Materials Ready for Week 3-4

| Item | Location | Status |
|------|----------|--------|
| Virtual threads config | `docs/VIRTUAL_THREADS_CONFIG.properties` | ✅ Ready |
| Sealed interface guide | `docs/SEALED_INTERFACES_GUIDE.md` | ✅ Ready |
| Reflection config | `META-INF/.../reflection-config.json` | ✅ Ready |
| Native image config | `src/main/resources/native-image.properties` | ✅ Ready |
| Load test script | `load-test.js` | ✅ Ready (updated for 10k users) |

---

## Performance Targets

### Week 3 (Virtual Threads)
| Metric | Current | Target |
|--------|---------|--------|
| p95 latency @ 1k users | < 500ms | < 400ms |
| Thread count | 100-200 platform | 5000+ virtual |
| Throughput @ 1k | 100-200 req/s | 300+ req/s |

### Week 4 (Native Image)
| Metric | JAR (JVM) | Native Image |
|--------|-----------|--------------|
| Startup time | 3-5s | 50-100ms |
| Memory @ idle | 250-300 MB | 50-80 MB |
| Memory @ 10k load | 400-500 MB | 80-100 MB |
| p95 latency @ 10k | < 500ms | < 500ms |

---

## Risk Mitigation

**Virtual Thread Risks:**
- ❓ Blocker: Pinned virtual threads (rare in Vaadin)
- ✅ Mitigation: Monitor jvm.threads.virtual.count

**Native Image Risks:**
- ❓ Blocker: Dynamic class loading (reflection)
- ✅ Mitigation: reflection-config.json already prepared
- ✅ Mitigation: Test all sealed interfaces

---

## Success Criteria

### Week 3 Success
- ✅ Virtual threads enabled and metrics visible
- ✅ Load test passes at 5k concurrent users
- ✅ p95 latency ≤ 500ms
- ✅ Throughput > 500 req/s

### Week 4 Success
- ✅ Native image builds in < 2 minutes
- ✅ Startup time < 150ms
- ✅ Memory usage < 100 MB
- ✅ Load test passes at 10k concurrent users
- ✅ All SLOs met (p95 < 500ms, error rate < 1%)

---

## Scaling Path

```
Week 1-2: Foundation (Sealed classes + Observability)
    ↓
Week 3: Virtual Threads (1k → 5k users)
    p95 latency: < 500ms ✅
    Throughput: > 500 req/s
    ↓
Week 4: Native Image (5k → 10k users)
    p95 latency: < 500ms ✅
    Startup: 50-100ms
    Memory: 50-100 MB
    ↓
Production: Kubernetes / Docker Swarm / EC2
    Replicas: 10-20 (at 1k users each)
    Throughput: 10k-20k req/s
    SLO: p95 < 500ms, error < 1%
```

---

## Dependencies

**Virtual Threads:**
- Java 21+ ✅ (using Java 25)
- Spring Boot 3.2+ ✅ (using 4.1.0)

**Native Image:**
- GraalVM 24.0+ (install before Week 4)
- Maven native profile (already in pom.xml)

---

## Testing Checklist

### Before Week 3 Cutover
- [ ] Run load test at 1k users (baseline)
- [ ] Record p50/p95/p99 latencies
- [ ] Record memory usage
- [ ] Record throughput

### After Virtual Threads (Week 3)
- [ ] Run load test at 5k users
- [ ] Verify p95 < 500ms still holds
- [ ] Verify memory doesn't grow linearly
- [ ] Compare vs. Week 2 baseline

### After Native Image (Week 4)
- [ ] Run load test at 10k users
- [ ] Verify all SLOs met
- [ ] Verify startup time < 150ms
- [ ] Verify memory < 100 MB

---

## Documentation Template (Copy for Week 3-4)

**WEEK3_IMPLEMENTATION_STATUS.md**
- Virtual threads enabled date
- Load test results (5k users)
- Performance improvement %
- Any issues encountered

**WEEK4_IMPLEMENTATION_STATUS.md**
- Native image build time
- Startup time (before/after)
- Memory usage (before/after)
- Load test results (10k users)
- Production readiness checklist

---

## Next Actions (After Week 2)

1. **Day 1 of Week 3:** Enable virtual threads
2. **Day 3 of Week 3:** Run load test at 5k users
3. **Day 1 of Week 4:** Build native image
4. **Day 3 of Week 4:** Run load test at 10k users
5. **End of Week 4:** Production deployment

---

## Support & Resources

- **Virtual Threads:** https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html
- **GraalVM Native Image:** https://www.graalvm.org/latest/reference-manual/native-image/
- **Spring Boot Native:** https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html
- **k6 Documentation:** https://k6.io/docs/

---

**Status:** 🟡 Planning Complete, Ready to Execute  
**Start Date:** September 9, 2026 (after Week 2)  
**End Date:** September 22, 2026  
**Owner:** Architecture Team

🚀 **Road to 10,000 concurrent users is clear. We're ready.**

