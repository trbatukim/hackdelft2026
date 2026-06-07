// Pokemon Battle Bot - automatically plays the game using optimal move selection

const bot = (() => {
  function pickMove() {
    // VOLT TACKLE recoil = 25% of damage dealt; avoid if recoil could faint us
    const safeToUseVoltTackle = state.playerHp > 15;

    // IRON TAIL has 75% accuracy — skip it when a reliable move would finish the enemy
    const estKillThreshold = 22; // min damage from a 90-power move at worst roll
    const enemyAlmostDead = state.enemyHp <= estKillThreshold;

    if (safeToUseVoltTackle && !enemyAlmostDead) {
      // Volt Tackle has highest expected damage when we can afford the recoil
      return 3;
    }
    // Thunderbolt: 90 power, 100% acc, no recoil — safest high-damage option
    return 0;
  }

  function tick() {
    // Auto-reset if we lost
    const playAgainBtn = document.getElementById("play-again");
    if (playAgainBtn && !playAgainBtn.hasAttribute("hidden")) {
      log("🤖 Bot: resetting...");
      resetGame();
      return;
    }

    // Attack when buttons are available
    if (!state.busy) {
      const buttons = document.querySelectorAll(".move-btn");
      const allEnabled = [...buttons].every(b => !b.disabled);
      if (allEnabled) {
        const idx = pickMove();
        playerAttack(idx);
      }
    }
  }

  function start() {
    setTimeout(() => {
      setInterval(tick, 400);
      log("Bot started!");
    }, 5000);
  }

  return { start };
})();

bot.start();
