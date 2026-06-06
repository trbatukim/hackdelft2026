const WIN_PASSWORD = "SECRET_PASSWORD";

const state = {
  playerMaxHp: 100,
  enemyMaxHp: 120,
  playerHp: 100,
  enemyHp: 120,
  busy: false,
};

const moves = [
  { name: "THUNDERBOLT", power: 90,  acc: 1.00, recoil: 0,    special: null },
  { name: "QUICK ATTACK", power: 40, acc: 1.00, recoil: 0,    special: null },
  { name: "IRON TAIL",    power: 100, acc: 0.75, recoil: 0,   special: null },
  { name: "VOLT TACKLE",  power: 120, acc: 1.00, recoil: 0.25, special: null },
];

const enemyMoves = [
  { name: "SHADOW BALL",   minDmg: 18, maxDmg: 30 },
  { name: "LICK",          minDmg: 8,  maxDmg: 14 },
  { name: "NIGHT SHADE",   minDmg: 20, maxDmg: 20 },
  { name: "HYPNOSIS",      minDmg: 5,  maxDmg: 10 },
];

function randInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function calcDamage(power) {
  const roll = 0.85 + Math.random() * 0.15;
  return Math.max(1, Math.round(power * roll * 0.35));
}

function updateHpBar(who) {
  const isPlayer = who === "player";
  const hp    = isPlayer ? state.playerHp    : state.enemyHp;
  const maxHp = isPlayer ? state.playerMaxHp : state.enemyMaxHp;
  const fill  = document.getElementById(who + "-hp-fill");
  const cur   = document.getElementById(who + "-hp-cur");
  const pct   = Math.max(0, hp / maxHp * 100);

  fill.style.width = pct + "%";
  fill.className = "hp-fill " + (pct > 50 ? "high" : pct > 20 ? "medium" : "low");
  cur.textContent = Math.max(0, hp);
}

function log(msg, cls = "") {
  const div = document.getElementById("log");
  const p = document.createElement("p");
  if (cls) p.className = cls;
  p.textContent = msg;
  div.appendChild(p);
  div.scrollTop = div.scrollHeight;
}

function setButtonsDisabled(val) {
  document.querySelectorAll(".move-btn").forEach(b => b.disabled = val);
}

function shakeSprite(id) {
  const el = document.getElementById(id);
  el.style.transition = "transform 0.1s";
  const dir = id.includes("enemy") ? -1 : 1;
  el.style.transform = `translateX(${dir * 18}px)`;
  setTimeout(() => el.style.transform = "", 200);
}

function playerAttack(idx) {
  if (state.busy) return;
  state.busy = true;
  setButtonsDisabled(true);

  const move = moves[idx];
  const hit  = Math.random() < move.acc;

  if (!hit) {
    log(`PIKACHU used ${move.name}... but it missed!`);
    setTimeout(enemyTurn, 900);
    return;
  }

  const dmg = calcDamage(move.power);
  state.enemyHp -= dmg;
  shakeSprite("enemy-sprite");
  log(`PIKACHU used ${move.name}! Dealt ${dmg} damage.`, "highlight");
  updateHpBar("enemy");

  if (move.recoil > 0) {
    const recoilDmg = Math.max(1, Math.round(dmg * move.recoil));
    state.playerHp -= recoilDmg;
    updateHpBar("player");
    log(`PIKACHU took ${recoilDmg} recoil damage!`);
  }

  if (state.playerHp <= 0) { 
    setTimeout(showLose, 600); 
    return; 
  }
  if (state.enemyHp <= 0)  { 
    // setTimeout(showWin,  600); 
    showWinText();
    return; 
  }

  setTimeout(enemyTurn, 900);
}

function enemyTurn() {
  const em = enemyMoves[randInt(0, enemyMoves.length - 1)];
  const dmg = randInt(em.minDmg, em.maxDmg);
  state.playerHp -= dmg;
  shakeSprite("player-sprite");
  log(`PALOSSAND used ${em.name}! Dealt ${dmg} damage.`, "enemy-log");
  updateHpBar("player");

  if (state.playerHp <= 0) {
    showLoseText();
    // setTimeout(showLose, 600);
    return;
  }

  state.busy = false;
  setButtonsDisabled(false);
}

function resetGame() {
  state.playerHp = state.playerMaxHp;
  state.enemyHp  = state.enemyMaxHp;
  state.busy     = false;

  updateHpBar("player");
  updateHpBar("enemy");

  document.getElementById("log").innerHTML = '<p class="highlight">A wild PALOSSAND appeared! Choose your move!</p>';
  document.getElementById("enemy-sprite").style.transform = "";
  document.getElementById("player-sprite").style.transform = "";

  const resultEl = document.getElementById("result-text");
  resultEl.innerText = "";
  resultEl.className = "";
  document.getElementById("play-again").setAttribute("hidden", "hidden");

  setButtonsDisabled(false);
}

function showLoseText() {
  const el = document.getElementById("result-text");
  el.innerText = "You Lost! Try Again...";
  el.classList.add("lose-text");
  document.getElementById("play-again").removeAttribute("hidden");
}

function showWinText() {
  document.getElementById("result-text").innerText = "You Win! Here's The First Password: SECRET";
  document.getElementById("result-text").classList.add("win-text");
}

updateHpBar("player");
updateHpBar("enemy");
