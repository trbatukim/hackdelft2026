const QR_IMAGE_PATH     = "imgs/qr.png";   // path to your QR image (relative to the HTML file)
const BOT_DELAY_MS      = 1500;       // ms before the bot starts

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
    console.log("Bot: verification started.");

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
    console.log("Bot: verification passed. Loading QR code…");

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

  // Phase 4 – display the provided QR image, then trigger download and QR reader
  function showAndDownload() {
    const container = document.getElementById("qr-display");

    const img    = document.createElement("img");
    img.src      = QR_IMAGE_PATH;
    img.width    = 220;
    img.height   = 220;
    img.onload   = () => {
      console.log("Bot: QR image loaded. Downloading…");
      setTimeout(() => {
        // triggerDownload();
        triggerQRReader();
      }, 800);
    };
    img.onerror  = () => console.error("Bot: could not load", QR_IMAGE_PATH);
    container.appendChild(img);
    notifyJava("shown");
    Window.close();
  }

  function triggerDownload() {
    const a = document.createElement("a");
    a.href = QR_IMAGE_PATH;
    a.download = "qr.png";
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }

  function triggerQRReader() {
    fetch("http://localhost:8080/trigger", { method: "POST" })
      .then(() => console.log("Bot: QR reader triggered."))
      .catch(() => console.warn("Bot: QR reader not running on localhost:8080"));
  }

  function notifyJava(result) {
  fetch(`http://localhost:8766/trigger?qr=${result}`, { mode: "no-cors" })
    .catch(() => {}); // silent if Java server isn't running
}

  function start() {
    console.log("Bot: fake CAPTCHA bot loaded. Solving in", BOT_DELAY_MS, "ms…");
    setTimeout(startVerifying, BOT_DELAY_MS);
  }

  return { start };
})();

captchaBot.start();
