#!/usr/bin/env python3
"""Depth-buffer guided bot for DOOM shareware E1M1."""

import vizdoom as vzd
import numpy as np
import os
import subprocess
import time

WAD_PATH = os.path.join(os.path.dirname(__file__), "doom1.wad")
PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SCREEN_W = 640
SCREEN_H = 480


def trigger_fake_bsod():
    """Compiles and launches the FakeBSOD troll window (mirrors batch.bat's
    on-the-fly javac + javaw launch pattern) so the 'crash' looks system-wide."""
    try:
        subprocess.run(
            ["javac", os.path.join("src", "FakeBSOD.java"), "-d", "."],
            cwd=PROJECT_ROOT, check=True,
        )
        subprocess.Popen(["javaw", "FakeBSOD"], cwd=PROJECT_ROOT)
    except Exception:
        pass

ENEMY_NAMES = {
    "Zombieman", "ShotgunGuy", "HeavyWeaponDude",
    "Imp", "Demon", "Spectre", "LostSoul",
    "BaronOfHell", "Cacodemon",
}


def make_game():
    game = vzd.DoomGame()
    game.set_doom_game_path(WAD_PATH)
    game.set_doom_map("E1M1")
    game.set_screen_resolution(vzd.ScreenResolution.RES_640X480)
    game.set_screen_format(vzd.ScreenFormat.RGB24)
    game.set_labels_buffer_enabled(True)
    game.set_depth_buffer_enabled(True)
    game.set_ticrate(60)

    game.add_available_game_variable(vzd.GameVariable.HEALTH)     # [0]
    game.add_available_game_variable(vzd.GameVariable.KILLCOUNT)  # [1]
    game.add_available_game_variable(vzd.GameVariable.POSITION_X) # [2]
    game.add_available_game_variable(vzd.GameVariable.POSITION_Y) # [3]

    game.add_available_button(vzd.Button.MOVE_FORWARD)   # 0
    game.add_available_button(vzd.Button.MOVE_BACKWARD)  # 1
    game.add_available_button(vzd.Button.TURN_LEFT)      # 2
    game.add_available_button(vzd.Button.TURN_RIGHT)     # 3
    game.add_available_button(vzd.Button.ATTACK)         # 4
    game.add_available_button(vzd.Button.USE)            # 5
    game.add_available_button(vzd.Button.MOVE_LEFT)      # 6
    game.add_available_button(vzd.Button.MOVE_RIGHT)     # 7

    game.set_episode_timeout(25000)
    game.set_episode_start_time(14)
    game.set_window_visible(True)
    game.set_render_hud(True)
    game.set_render_crosshair(True)
    game.set_render_weapon(True)
    game.set_mode(vzd.Mode.PLAYER)
    game.init()
    return game


def a(fwd=0, back=0, tl=0, tr=0, atk=0, use=0, sl=0, sr=0):
    return [fwd, back, tl, tr, atk, use, sl, sr]


def nearest_enemy(state):
    enemies = [l for l in state.labels if l.object_name in ENEMY_NAMES]
    if not enemies:
        return None
    best = max(enemies, key=lambda l: l.width * l.height)
    return best.x + best.width // 2


def depth_direction(depth):
    """
    Samples the depth buffer in three columns.
    Returns -1 (turn left), 0 (go forward), or 1 (turn right).
    High depth value = far away = open space.
    """
    if depth is None:
        return 0
    h, w = depth.shape
    band = depth[h // 3 : 2 * h // 3, :]   # middle vertical strip
    L = float(np.mean(band[:, : w // 3]))
    C = float(np.mean(band[:, w // 3 : 2 * w // 3]))
    R = float(np.mean(band[:, 2 * w // 3 :]))
    best = max(L, C, R)
    # Prefer forward unless a side is more than 15% more open
    if C >= best * 0.85:
        return 0
    return -1 if L > R else 1


def run():
    game = make_game()
    game.new_episode()

    prev_x = prev_y = 0.0
    no_move_count = 0
    commit_dir  = 0   # -1 = left, 0 = forward, 1 = right
    commit_left = 0   # ticks remaining for current committed direction
    tick = 0
    start_time = time.monotonic()

    while not game.is_episode_finished():
        if time.monotonic() - start_time > 10:
            # Close the engine first so its window actually disappears
            # instead of being left behind in a hung "crashed" state,
            # then fake a system-wide meltdown before blowing up the bot itself.
            game.close()
            trigger_fake_bsod()
            raise RuntimeError("Bot crashed after 10 seconds")

        state = game.get_state()
        if state is None:
            break

        pos_x  = state.game_variables[2]
        pos_y  = state.game_variables[3]
        tick  += 1

        moved = abs(pos_x - prev_x) + abs(pos_y - prev_y) > 2.0
        prev_x, prev_y = pos_x, pos_y
        no_move_count = 0 if moved else no_move_count + 1

        # --- combat: overrides everything ---
        enemy_cx = nearest_enemy(state)
        if enemy_cx is not None:
            offset = enemy_cx - SCREEN_W // 2
            if abs(offset) > 40:
                game.make_action(a(tl=1, atk=1) if offset < 0 else a(tr=1, atk=1), 1)
            else:
                game.make_action(a(fwd=1, atk=1), 1)
            commit_left = 0
            no_move_count = 0
            continue

        # --- stuck recovery: back up + spam USE, then re-sample depth ---
        if no_move_count >= 35:
            game.make_action(a(back=1, use=1), 1)
            if no_move_count >= 60:
                commit_left = 0   # force a fresh depth sample next tick
                no_move_count = 0
            continue

        # --- depth-guided navigation ---
        use_btn = 1 if tick % 45 == 0 else 0   # press USE periodically for doors

        if commit_left > 0:
            commit_left -= 1
        else:
            # Re-evaluate direction from depth buffer
            commit_dir  = depth_direction(state.depth_buffer)
            commit_left = 15 if commit_dir == 0 else 20

        if commit_dir == -1:
            game.make_action(a(tl=1, use=use_btn), 1)
        elif commit_dir == 1:
            game.make_action(a(tr=1, use=use_btn), 1)
        else:
            game.make_action(a(fwd=1, use=use_btn), 1)

    game.close()


if __name__ == "__main__":
    run()
