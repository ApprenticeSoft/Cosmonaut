const { chromium, devices } = require('playwright');
const fs = require('fs');
const path = require('path');
const { PNG } = require('pngjs');

const OUTPUT_ROOT = path.resolve(__dirname, 'reports');
const MENU_BUTTON_X = 0.84;
const MENU_PLAY_Y = 0.35;
const MENU_OPTIONS_Y = 0.47;
const MENU_REGION = [0.70, 0.28, 0.96, 0.66];
const OPTIONS_LEFT_REGION = [0.02, 0.28, 0.40, 0.70];
const MENU_REGION_BRIGHT_MIN = 3.0;
const OPTIONS_LEFT_REGION_BRIGHT_MIN = 5.0;

const IGNORED_CONSOLE_ERRORS = [
  /Pools: Please manually define a Pool for class com\.badlogic\.gdx\.math\.Vector2/i,
  /Pools: Please manually define a Pool for class com\.badlogic\.gdx\.math\.Vector3/i,
  /Pools: Please manually define a Pool for class com\.badlogic\.gdx\.graphics\.Color/i,
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

function imageDifferenceRatio(fileA, fileB) {
  const a = PNG.sync.read(fs.readFileSync(fileA));
  const b = PNG.sync.read(fs.readFileSync(fileB));
  if (a.width !== b.width || a.height !== b.height) {
    return 1;
  }

  const dataA = a.data;
  const dataB = b.data;
  const step = Math.max(4, Math.floor((a.width * a.height * 4) / 60000));
  let delta = 0;
  let count = 0;
  for (let i = 0; i < dataA.length; i += step) {
    delta += Math.abs(dataA[i] - dataB[i]);
    delta += Math.abs(dataA[i + 1] - dataB[i + 1]);
    delta += Math.abs(dataA[i + 2] - dataB[i + 2]);
    count += 3;
  }
  return count ? delta / (count * 255) : 0;
}

function regionBrightness(filePath, nx1, ny1, nx2, ny2) {
  const png = PNG.sync.read(fs.readFileSync(filePath));
  const x1 = Math.max(0, Math.floor(nx1 * png.width));
  const y1 = Math.max(0, Math.floor(ny1 * png.height));
  const x2 = Math.min(png.width, Math.ceil(nx2 * png.width));
  const y2 = Math.min(png.height, Math.ceil(ny2 * png.height));

  let sum = 0;
  let count = 0;
  for (let y = y1; y < y2; y++) {
    for (let x = x1; x < x2; x++) {
      const i = (y * png.width + x) * 4;
      sum += png.data[i] + png.data[i + 1] + png.data[i + 2];
      count += 3;
    }
  }
  return count ? sum / count : 0;
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

async function enterMainMenu(page, outDir, useTouch, loadedShot) {
  for (let attempt = 1; attempt <= 4; attempt++) {
    await clickNorm(page, 0.50, 0.55, useTouch);
    await sleep(1800);
    await waitForNonBlack(page, outDir, `home_to_menu_attempt_${attempt}`, 20000);
    const candidate = await shot(page, outDir, `02_main_menu_attempt_${attempt}`);
    const diff = imageDifferenceRatio(loadedShot, candidate);
    const menuRegionBright = regionBrightness(candidate, ...MENU_REGION);
    if (diff >= 0.002 && menuRegionBright >= MENU_REGION_BRIGHT_MIN) {
      return { shotPath: candidate, attempts: attempt };
    }
  }
  throw new Error('Home->menu transition did not occur after repeated taps/clicks.');
}

async function runScenario(target, mode) {
  const testId = `${target.name}-${mode}`;
  const outDir = path.join(OUTPUT_ROOT, testId);
  fs.mkdirSync(outDir, { recursive: true });

  const browser = await chromium.launch({ headless: true });
  let context;
  if (mode === 'mobile') {
    const mobileDescriptor = devices['Galaxy S9+'];
    context = await browser.newContext({
      ...mobileDescriptor,
      viewport: {
        width: mobileDescriptor.viewport.height,
        height: mobileDescriptor.viewport.width,
      },
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
    if (mode === 'mobile') {
      const box = await getCanvasBox(page);
      if (box.width <= box.height) {
        throw new Error(`Mobile canvas is not in landscape (w=${box.width}, h=${box.height}).`);
      }
      result.steps.push('mobile_landscape_canvas_confirmed');
    }

    await waitForNonBlack(page, outDir, 'load', 90000);
    result.steps.push('canvas_not_black_after_load');
    const loadedShot = await shot(page, outDir, '01_loaded');

    const useTouch = mode === 'mobile';

    // Home -> Main Menu
    const menuTransition = await enterMainMenu(page, outDir, useTouch, loadedShot);
    const mainMenuShot = menuTransition.shotPath;
    result.steps.push('entered_main_menu');
    result.steps.push(`menu_transition_attempt_${menuTransition.attempts}`);
    const menuCenterBrightness = regionBrightness(mainMenuShot, 0.28, 0.30, 0.60, 0.72);
    if (menuCenterBrightness < 1.2) {
      throw new Error(`Main menu center appears too dark (brightness=${menuCenterBrightness.toFixed(2)}), likely missing background image.`);
    }
    result.steps.push('main_menu_background_visible');

    // Options and back
    await clickNorm(page, MENU_BUTTON_X, MENU_OPTIONS_Y, useTouch);
    await sleep(1800);
    await waitForNonBlack(page, outDir, 'options_open', 15000);
    result.steps.push('opened_options');
    const optionsShot = await shot(page, outDir, '03_options');
    const optionsLeftBright = regionBrightness(optionsShot, ...OPTIONS_LEFT_REGION);
    if (optionsLeftBright < OPTIONS_LEFT_REGION_BRIGHT_MIN) {
      throw new Error(`Main menu -> options transition was not detected (left-region brightness=${optionsLeftBright.toFixed(2)}).`);
    }

    await clickNorm(page, 0.05, 0.08, useTouch);
    await sleep(1800);
    await waitForNonBlack(page, outDir, 'options_back', 15000);
    result.steps.push('back_from_options');
    const backShot = await shot(page, outDir, '04_after_options_back');
    const backMenuBright = regionBrightness(backShot, ...MENU_REGION);
    if (backMenuBright < MENU_REGION_BRIGHT_MIN) {
      throw new Error(`Options -> main menu back transition was not detected (menu-region brightness=${backMenuBright.toFixed(2)}).`);
    }

    // Play -> Intro or level flow
    await clickNorm(page, MENU_BUTTON_X, MENU_PLAY_Y, useTouch);
    await sleep(2400);
    await waitForNonBlack(page, outDir, 'play_open', 30000);
    const playShot = await shot(page, outDir, '05_after_play');
    const playMenuBright = regionBrightness(playShot, ...MENU_REGION);
    if (playMenuBright >= MENU_REGION_BRIGHT_MIN) {
      throw new Error(`Main menu -> play transition was not detected (menu-region brightness=${playMenuBright.toFixed(2)}).`);
    }
    result.steps.push('opened_play_flow');

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
    await shot(page, outDir, '06_after_input');

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
