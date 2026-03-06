const { chromium, devices } = require('playwright');
const fs = require('fs');
const path = require('path');
const { PNG } = require('pngjs');

const OUTPUT_ROOT = path.resolve(__dirname, 'reports');
const IGNORED_CONSOLE_ERRORS = [
  /Pools: Please manually define a Pool for class com\.badlogic\.gdx\.math\.Vector2/i,
];

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

async function getCanvasBox(page) {
  const canvas = page.locator('canvas');
  await canvas.waitFor({ timeout: 90000 });
  const box = await canvas.boundingBox();
  if (!box) {
    throw new Error('Canvas bounding box unavailable');
  }
  return box;
}

async function clickNorm(page, nx, ny, touch = false) {
  const box = await getCanvasBox(page);
  const x = box.x + nx * box.width;
  const y = box.y + ny * box.height;
  if (touch) {
    await page.touchscreen.tap(x, y);
  } else {
    await page.mouse.click(x, y);
  }
}

function imageBrightness(filePath) {
  const raw = fs.readFileSync(filePath);
  const png = PNG.sync.read(raw);
  const data = png.data;
  const step = Math.max(4, Math.floor((png.width * png.height * 4) / 50000));
  let sum = 0;
  let count = 0;
  for (let i = 0; i < data.length; i += step) {
    sum += data[i] + data[i + 1] + data[i + 2];
    count++;
  }
  return count ? sum / (count * 3) : 0;
}

async function shot(page, outDir, name) {
  const file = path.join(outDir, `${name}.png`);
  await page.screenshot({ path: file, fullPage: true });
  return file;
}

async function waitForNonBlack(page, outDir, label, timeoutMs = 90000) {
  const probeFile = path.join(outDir, `${label}_probe.png`);
  const start = Date.now();
  while (Date.now() - start < timeoutMs) {
    await page.screenshot({ path: probeFile, fullPage: true });
    const b = imageBrightness(probeFile);
    if (b > 4) return b;
    await sleep(500);
  }
  throw new Error('Canvas stayed black after timeout');
}

async function runScenario(target, mode) {
  const testId = `${target.name}-${mode}`;
  const outDir = path.join(OUTPUT_ROOT, testId);
  fs.mkdirSync(outDir, { recursive: true });

  const browser = await chromium.launch({ headless: true });
  let context;
  if (mode === 'mobile') {
    context = await browser.newContext({
      ...devices['Galaxy S9+'],
      ignoreHTTPSErrors: true,
      locale: 'en-US',
    });
  } else {
    context = await browser.newContext({
      viewport: { width: 1440, height: 1024 },
      ignoreHTTPSErrors: true,
      locale: 'en-US',
    });
  }

  const page = await context.newPage();
  const consoleErrors = [];
  const pageErrors = [];

  page.on('console', (msg) => {
    if (msg.type() === 'error') {
      consoleErrors.push(msg.text());
    }
  });

  page.on('pageerror', (err) => {
    pageErrors.push(String(err));
  });

  const result = {
    testId,
    url: target.url,
    mode,
    passed: false,
    steps: [],
    consoleErrors,
    pageErrors,
  };

  const significantConsoleErrors = () =>
    consoleErrors.filter((msg) => !IGNORED_CONSOLE_ERRORS.some((re) => re.test(msg)));

  try {
    await page.goto(target.url, { waitUntil: 'domcontentloaded', timeout: 120000 });
    result.steps.push('page_loaded');

    await getCanvasBox(page);
    result.steps.push('canvas_ready');

    await waitForNonBlack(page, outDir, 'load', 90000);
    result.steps.push('canvas_not_black_after_load');
    await shot(page, outDir, '01_loaded');

    const useTouch = mode === 'mobile';

    // Home -> Main Menu
    await clickNorm(page, 0.50, 0.55, useTouch);
    await sleep(2000);
    await waitForNonBlack(page, outDir, 'home_to_menu', 20000);
    result.steps.push('entered_main_menu');
    await shot(page, outDir, '02_main_menu');

    // Options and back
    await clickNorm(page, 0.83, 0.34, useTouch);
    await sleep(1800);
    await waitForNonBlack(page, outDir, 'options_open', 15000);
    result.steps.push('opened_options');
    await shot(page, outDir, '03_options');

    await clickNorm(page, 0.05, 0.08, useTouch);
    await sleep(1800);
    await waitForNonBlack(page, outDir, 'options_back', 15000);
    result.steps.push('back_from_options');

    // Upgrades and back
    await clickNorm(page, 0.83, 0.47, useTouch);
    await sleep(1800);
    await waitForNonBlack(page, outDir, 'upgrades_open', 15000);
    result.steps.push('opened_upgrades');
    await shot(page, outDir, '04_upgrades');

    await clickNorm(page, 0.05, 0.08, useTouch);
    await sleep(1800);
    await waitForNonBlack(page, outDir, 'upgrades_back', 15000);
    result.steps.push('back_from_upgrades');

    // Play -> Level select
    await clickNorm(page, 0.83, 0.60, useTouch);
    await sleep(2000);
    await waitForNonBlack(page, outDir, 'play_to_levels', 20000);
    result.steps.push('opened_level_selection');
    await shot(page, outDir, '05_level_selection');

    // Select level 1
    await clickNorm(page, 0.17, 0.64, useTouch);
    await sleep(4000);
    await waitForNonBlack(page, outDir, 'start_level', 30000);
    result.steps.push('started_level_or_intro');
    await shot(page, outDir, '06_level_or_intro');

    // Input sanity
    if (useTouch) {
      await clickNorm(page, 0.35, 0.78, true);
      await sleep(800);
      await clickNorm(page, 0.65, 0.78, true);
      await sleep(800);
      result.steps.push('mobile_touch_input_sent');
    } else {
      await clickNorm(page, 0.50, 0.80, false);
      await sleep(800);
      result.steps.push('desktop_mouse_input_sent');
    }

    await waitForNonBlack(page, outDir, 'post_input', 15000);
    await shot(page, outDir, '07_after_input');

    if (significantConsoleErrors().length > 0 || pageErrors.length > 0) {
      throw new Error('Browser runtime errors detected.');
    }

    result.passed = true;
  } catch (err) {
    result.error = String(err && err.stack ? err.stack : err);
    await shot(page, outDir, 'zz_failure_state').catch(() => {});
  } finally {
    await context.close();
    await browser.close();
  }

  result.significantConsoleErrors = significantConsoleErrors();
  result.consoleErrors = significantConsoleErrors();

  fs.writeFileSync(path.join(outDir, 'result.json'), JSON.stringify(result, null, 2));
  return result;
}

async function main() {
  const targets = [
    { name: 'local', url: 'http://127.0.0.1:8090' },
    { name: 'rpi', url: 'https://cosmonaut.marcvidal.ca' },
  ];

  const results = [];
  for (const target of targets) {
    for (const mode of ['desktop', 'mobile']) {
      // eslint-disable-next-line no-console
      console.log(`Running ${target.name} ${mode} ...`);
      const r = await runScenario(target, mode);
      results.push(r);
      // eslint-disable-next-line no-console
      console.log(`${r.testId}: ${r.passed ? 'PASS' : 'FAIL'}`);
    }
  }

  const summary = {
    generatedAt: new Date().toISOString(),
    results,
  };

  fs.mkdirSync(OUTPUT_ROOT, { recursive: true });
  fs.writeFileSync(path.join(OUTPUT_ROOT, 'summary.json'), JSON.stringify(summary, null, 2));

  const failed = results.filter((r) => !r.passed);
  if (failed.length > 0) {
    // eslint-disable-next-line no-console
    console.error('Some smoke scenarios failed:', failed.map((f) => f.testId).join(', '));
    process.exit(1);
  }

  // eslint-disable-next-line no-console
  console.log('All smoke scenarios passed.');
}

main().catch((err) => {
  // eslint-disable-next-line no-console
  console.error(err);
  process.exit(1);
});
