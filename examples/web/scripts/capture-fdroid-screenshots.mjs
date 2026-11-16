/**
 * Capture real QRaft PWA frames for F-Droid Fastlane phoneScreenshots.
 * Run via scripts/capture-fdroid-screenshots.sh (builds + Playwright Chromium).
 */
import { spawn } from "node:child_process";
import { copyFileSync, existsSync, mkdirSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { chromium } from "@playwright/test";

const webRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const repoRoot = path.resolve(webRoot, "../..");
const images = path.join(repoRoot, "fastlane/metadata/android/en-US/images");
const shots = path.join(images, "phoneScreenshots");
const iconSrc = path.join(repoRoot, "examples/android/metadata/en-US/images/icon.png");
const heroSrc = path.join(repoRoot, "branding/assets/social-preview.jpg");
const port = 4173;
const baseURL = `http://127.0.0.1:${port}`;

async function waitForServer(child) {
  child.stderr.on("data", (buf) => process.stderr.write(buf));
  child.stdout.on("data", (buf) => process.stdout.write(buf));
  const dead = new Promise((_, reject) => {
    child.on("exit", (code) => reject(new Error(`preview exited ${code}`)));
  });
  const ready = (async () => {
    for (let i = 0; i < 60; i += 1) {
      try {
        const res = await fetch(baseURL);
        if (res.ok) return;
      } catch {
        /* preview not listening yet */
      }
      await new Promise((r) => setTimeout(r, 500));
    }
    throw new Error("preview timeout");
  })();
  await Promise.race([ready, dead]);
}

async function main() {
  mkdirSync(shots, { recursive: true });
  if (existsSync(iconSrc)) {
    copyFileSync(iconSrc, path.join(images, "icon.png"));
  }

  const preview = spawn(
    "npm",
    ["run", "preview", "--", "--port", String(port), "--host", "127.0.0.1"],
    { cwd: webRoot, stdio: ["ignore", "pipe", "pipe"] },
  );
  try {
    await waitForServer(preview);
    console.error("preview up, launching chromium");
    const browser = await chromium.launch({
      headless: true,
      args: ["--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu"],
    });
    console.error("chromium launched");
    const pixel = {
      viewport: { width: 412, height: 915 },
      deviceScaleFactor: 2,
      isMobile: true,
      hasTouch: true,
    };
    const context = await browser.newContext(pixel);
    const page = await context.newPage();

    await page.goto(baseURL, { waitUntil: "load", timeout: 30_000 });
    console.error("home loaded");
    await page.getByTestId("qr-preview").waitFor({ state: "visible", timeout: 30_000 });
    console.error("qr preview visible");
    await page.locator("[data-field='primary']").fill("https://github.com/edwardlthompson/QRaft");
    await page.waitForFunction(() => {
      const canvas = document.querySelector("[data-testid='qr-preview']");
      return canvas instanceof HTMLCanvasElement && canvas.width > 0;
    });
    await page.screenshot({
      path: path.join(shots, "01-home-editor.png"),
      type: "png",
    });
    console.error("wrote 01-home-editor.png");

    await page.getByRole("button", { name: "Profiles" }).click();
    await page.getByRole("heading", { name: "Library" }).waitFor({ state: "visible", timeout: 15_000 });
    await page.screenshot({
      path: path.join(shots, "02-profiles.png"),
      type: "png",
    });
    console.error("wrote 02-profiles.png");

    await page.getByRole("button", { name: "Wallpaper" }).click();
    await page.getByRole("button", { name: "Phone preset" }).click();
    await page.getByTestId("qr-preview").waitFor({ state: "visible", timeout: 15_000 });
    await page.screenshot({
      path: path.join(shots, "03-wallpaper.png"),
      type: "png",
    });
    console.error("wrote 03-wallpaper.png");

    await page.getByRole("button", { name: "Home" }).click();
    await page.getByRole("button", { name: "Settings" }).click();
    await page.getByTestId("settings-panel").waitFor({ state: "visible", timeout: 15_000 });
    await page.screenshot({
      path: path.join(shots, "04-settings.png"),
      type: "png",
    });
    console.error("wrote 04-settings.png");

    if (existsSync(heroSrc)) {
      copyFileSync(heroSrc, path.join(images, "featureGraphic.jpg"));
      console.error("wrote featureGraphic.jpg from branding");
    }

    await browser.close();
    console.error("browser closed");
  } finally {
    preview.kill("SIGKILL");
  }
}

main()
  .then(() => process.exit(0))
  .catch((err) => {
    console.error(err);
    process.exit(1);
  });
