import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright config for the demo application's E2E/visual tests.
 * Assumes the Spring Boot demo app is already running (see `demo` module,
 * `mvn spring-boot:run`) and reachable at baseURL below.
 */
export default defineConfig({
  testDir: './e2e',
  timeout: 60_000,
  expect: {
    timeout: 15_000,
  },
  fullyParallel: false,
  workers: 1,
  retries: 0,
  reporter: [['list']],
  use: {
    baseURL: process.env.PLAYWRIGHT_BASE_URL || 'http://localhost:8080',
    viewport: { width: 1280, height: 800 },
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
