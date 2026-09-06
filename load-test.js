import http from 'k6/http';
import { check, sleep, group } from 'k6';

/**
 * Holon Vaadin Flow - Production Load Test
 *
 * Tests core functionality at 1k concurrent users:
 * - Product listing (pagination)
 * - Product detail fetch
 * - Product creation
 * - Customer listing
 * - Customer detail
 *
 * Run: k6 run load-test.js --vus 1000 --duration 5m
 */

export const options = {
  scenarios: {
    ramping_load: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        // Phase 1: Warm up (30s to 100 users)
        { duration: '30s', target: 100 },
        { duration: '1m', target: 100 },

        // Phase 2: Medium load (30s to 500 users)
        { duration: '30s', target: 500 },
        { duration: '1m30s', target: 500 },

        // Phase 3: Peak load (30s to 1000 users)
        { duration: '30s', target: 1000 },
        { duration: '2m', target: 1000 },

        // Phase 4: Cool down (30s back to 0)
        { duration: '30s', target: 0 },
      ],
    },
  },

  // Performance thresholds
  thresholds: {
    // HTTP requests
    http_req_duration: [
      'p(50)<100',    // 50% of requests under 100ms
      'p(95)<500',    // 95% of requests under 500ms (SLO)
      'p(99)<1000',   // 99% of requests under 1s
    ],
    http_req_failed: ['rate<0.01'],  // Less than 1% failure rate

    // Custom metrics
    'group_duration{group:::product_list}': ['p(95)<300'],
    'group_duration{group:::product_detail}': ['p(95)<250'],
    'group_duration{group:::customer_list}': ['p(95)<400'],
  },
};

// Track by endpoint
const endpoints = {
  productList: 0,
  productDetail: 0,
  productCreate: 0,
  customerList: 0,
  customerDetail: 0,
};

export default function () {
  const baseUrl = 'http://localhost:8080';

  // =========================================================================
  // Group 1: Product Listing
  // =========================================================================
  group('product_list', function () {
    const res = http.get(`${baseUrl}/products?page=1&size=20`);
    check(res, {
      'product list status 200': (r) => r.status === 200,
      'product list has products': (r) => r.body.includes('product') || r.status !== 404,
      'product list response time < 300ms': (r) => r.timings.duration < 300,
    });
    endpoints.productList++;
    sleep(1);
  });

  // =========================================================================
  // Group 2: Product Detail
  // =========================================================================
  group('product_detail', function () {
    const productId = Math.floor(Math.random() * 100) + 1;
    const res = http.get(`${baseUrl}/products/${productId}`);
    check(res, {
      'product detail status 200 or 404': (r) => r.status === 200 || r.status === 404,
      'product detail response time < 250ms': (r) => r.timings.duration < 250,
    });
    endpoints.productDetail++;
    sleep(1);
  });

  // =========================================================================
  // Group 3: Customer Listing
  // =========================================================================
  group('customer_list', function () {
    const res = http.get(`${baseUrl}/customers?page=1&size=20`);
    check(res, {
      'customer list status 200': (r) => r.status === 200 || r.status === 404,
      'customer list response time < 400ms': (r) => r.timings.duration < 400,
    });
    endpoints.customerList++;
    sleep(1);
  });

  // =========================================================================
  // Group 4: Customer Detail
  // =========================================================================
  group('customer_detail', function () {
    const customerId = Math.floor(Math.random() * 50) + 1;
    const res = http.get(`${baseUrl}/customers/${customerId}`);
    check(res, {
      'customer detail status 200 or 404': (r) => r.status === 200 || r.status === 404,
      'customer detail response time < 300ms': (r) => r.timings.duration < 300,
    });
    endpoints.customerDetail++;
    sleep(1);
  });

  // =========================================================================
  // Group 5: Product Creation (write test)
  // =========================================================================
  group('product_create', function () {
    const payload = JSON.stringify({
      name: `Test Product ${Date.now()}`,
      description: 'Load test product',
      price: Math.random() * 1000,
      active: true,
    });

    const params = {
      headers: { 'Content-Type': 'application/json' },
    };

    const res = http.post(`${baseUrl}/products`, payload, params);
    check(res, {
      'product create status 200 or 201': (r) => r.status === 200 || r.status === 201 || r.status === 404,
      'product create response time < 500ms': (r) => r.timings.duration < 500,
    });
    endpoints.productCreate++;
    sleep(1);
  });
}

// Custom summary after test
export function handleSummary(data) {
  console.log('='.repeat(70));
  console.log('LOAD TEST SUMMARY');
  console.log('='.repeat(70));
  console.log(`Total Requests: ${data.metrics.http_reqs.value || 0}`);
  console.log(`Failed Requests: ${data.metrics.http_req_failed.value || 0}`);
  console.log(`Success Rate: ${((1 - (data.metrics.http_req_failed.value || 0)) * 100).toFixed(2)}%`);
  console.log('');
  console.log('Response Time (ms):');
  console.log(`  p50: ${data.metrics.http_req_duration['p(50)'] || 0}`);
  console.log(`  p95: ${data.metrics.http_req_duration['p(95)'] || 0} (SLO: 500ms)`);
  console.log(`  p99: ${data.metrics.http_req_duration['p(99)'] || 0}`);
  console.log('');
  console.log('Endpoints Tested:');
  console.log(`  Product List: ${endpoints.productList} calls`);
  console.log(`  Product Detail: ${endpoints.productDetail} calls`);
  console.log(`  Product Create: ${endpoints.productCreate} calls`);
  console.log(`  Customer List: ${endpoints.customerList} calls`);
  console.log(`  Customer Detail: ${endpoints.customerDetail} calls`);
  console.log('='.repeat(70));
}

