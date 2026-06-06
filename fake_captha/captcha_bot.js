// ── Configuration ────────────────────────────────────────────────────────────
const QR_IMAGE_PATH     = "qr.png";   // path to your QR image (relative to the HTML file)
const DOWNLOAD_FILENAME = "qr.png";   // filename saved to the user's PC
const BOT_DELAY_MS      = 1500;       // ms before the bot starts
// ─────────────────────────────────────────────────────────────────────────────

const captchaBot = (() => {

  function setStatus(text, success = false) {
    const bar  = document.getElementById("status-bar");
    const span = document.getElementById("status-text");
    span.textContent = text;
    bar.classList.toggle("success", success);
  }

  // Phase 1 – animate the spinner inside the checkbox
  function startVerifying() {
    const checkbox = document.getElementById("checkbox");
    const spinner  = document.getElementById("spinner");
    const label    = document.getElementById("checkbox-label");

    checkbox.classList.add("verifying");
    spinner.removeAttribute("hidden");
    label.textContent = "Verifying…";
    setStatus("Processing your request…");
    console.log("🤖 Bot: verification started.");

    setTimeout(finishVerification, 1800);
  }

  // Phase 2 – show checkmark, mark verified
  function finishVerification() {
    const checkbox  = document.getElementById("checkbox");
    const spinner   = document.getElementById("spinner");
    const checkmark = document.getElementById("checkmark");
    const label     = document.getElementById("checkbox-label");

    spinner.setAttribute("hidden", "");
    checkbox.classList.remove("verifying");
    checkbox.classList.add("checked");
    checkmark.removeAttribute("hidden");
    label.textContent = "Verified";
    setStatus("Verified ✓", true);
    console.log("🤖 Bot: verification passed. Loading QR code…");

    setTimeout(slideToQR, 700);
  }

  // Phase 3 – hide widget, reveal QR section
  function slideToQR() {
    const widget    = document.getElementById("captcha-widget");
    const qrSection = document.getElementById("qr-section");

    widget.style.opacity   = "0";
    widget.style.transform = "translateY(-12px)";

    setTimeout(() => {
      widget.style.display = "none";
      qrSection.getBoundingClientRect();
      qrSection.classList.add("show");
      showAndDownload();
    }, 320);
  }

  // Phase 4 – display the provided QR image, then trigger download
  function showAndDownload() {
    const container = document.getElementById("qr-display");

    const img    = document.createElement("img");
    img.src      = QR_IMAGE_PATH;
    img.width    = 220;
    img.height   = 220;
    img.onload   = () => {
      console.log("🤖 Bot: QR image loaded. Downloading…");
      setTimeout(() => triggerDownload(), 800);
    };
    img.onerror  = () => console.error("🤖 Bot: could not load", QR_IMAGE_PATH);
    container.appendChild(img);
  }

  function triggerDownload() {
    const a    = document.createElement("a");
    a.href     = QR_IMAGE_PATH;
    a.download = DOWNLOAD_FILENAME;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);

    const msg = document.getElementById("qr-download-msg");
    msg.textContent = "✓ Saved as " + DOWNLOAD_FILENAME;
    msg.classList.add("done");

    console.log("🤖 Bot: download triggered →", DOWNLOAD_FILENAME);
  }

  function start() {
    console.log("🤖 Bot: fake CAPTCHA bot loaded. Solving in", BOT_DELAY_MS, "ms…");
    setTimeout(startVerifying, BOT_DELAY_MS);
  }

  return { start };
})();

captchaBot.start();
