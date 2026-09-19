# Hack Delft 2026 - Double Bs

> Theme: Hack on the Beach
>
> *This project won second place for the Hack Delft case.*

## What It Does

It prints "Hello World".

That's it. That is the whole thing. Everything else in this repo exists to make printing two words as difficult, dramatic, and unnecessary as humanly possible.

Hack Delft case was focused on building a digital Rube Goldberg machine to print "Hello World".

## The Process

Running `Main` kicks off this chain of events:

1. **Keyboard Trap:** A "quick verification" window asks you to hold ENTER, then CTRL, then ALT, then SHIFT, then P, Z, and X, all at once, without letting go, and then click a button with the mouse. Release anything early and you start over.
2. **Pokémon Battle:** To prove you are human, you must win a Pokémon battle in the browser. A local server sits on port 8765 listening for the victory sound so it knows you actually won. A bot plays this fight instead of you.
3. **Fake Captcha:** Since you won the battle using a bot, we're still not sure you're legitimate. This opens a captcha window which contains a QR code.
4. **QR Reader:** A scanner window with a moving green line reads the QR code (just a visual gag) and writes out a batch file contained in the QR code.
5. **The Batch File:** The batch file that was dumped from the QR code announces that it is "deleting Windows to make space for Hello World", fills a progress bar to 99 percent, and then pivots to running DOOM to purify your machine.
6. **BSOD:** `FakeBSOD` throws up a full-screen Windows crash with the stop code `VERY_BADLY_WRITTEN_CODE`, waits for the "restart", then puts on some relaxing music, shows the desktop, opens a terminal, and gently types `echo Hello World!` for you one key at a time.

## Layout

```
src/                  the Java sources for the whole contraption
  Main.java           the entry point
  Logic.java          wires the trap, the QR listener, and the battle together
  KeyboardTrap.java   the impossible key combo window
  BattleDetector.java listens for the Pokémon victory
  QRReader.java       scans the captcha QR and fires the batch file
  TrollTimer.java     the fake self-destruct countdown
  FakeBSOD.java       the full screen BSOD
  JavaFXCodeFactory.java  unused mock IDE that we didn't have time to clean up
  MainLauncher.java   launches the JavaFX side
batch/                the "deleting Windows" theatrics
pokemon_battle/       the browser battle
fake_captcha/         the captcha with the QR code
doom/                 shareware DOOM1.WAD and the ViZDoom bot
sfx/                  alarm, vine boom, and some relaxing music
lib/                  JavaFX 23 and ZXing jars
```

## Code Distribution

<img width="305" height="142" alt="image" src="https://github.com/user-attachments/assets/347d8245-64c6-45f8-8d1e-79846fd7521b" />

We wanted to use as many different languages as possible in spirit of a Rube Goldberg machine. The code distribution is like this:

- **58% Java:** Used for event listeners, JavaFX desktop windows, fake BSOD, QR reader, and in between logic.
- **12.7% JavaScript:** Used for web apps and bots for Pokémon battle, and fake captcha.
- **10.1% CSS:** Used to style web apps for Pokémon battle, and fake captcha.
- **7.1% Python:** Used to code a DOOM bot using VIZDoom.
- **4.9% HTML:** Used for Pokémon battle, and fake captcha skeletons.
- **4% C:** Went unused in the final project.
- **3.2% Batchfile:** Used to run a fake deleting Windows script and for the final `echo Hello World!` command.

## Running It

You will need a JDK, and Python with ViZDoom and NumPy. The JavaFX and ZXing jars live in `lib/`.

Running `Main` starts the chain.

This is a hackathon project built for laughs. It moves your mouse, takes over your screen, opens terminals, and pokes at your files. Run it on a machine you do not mind being trolled on and read the source before you do.

## The Team

Double Bs. Made in collaboration with [Lara Akkan](https://www.github.com/larakkan) and Bora Bekbay.
