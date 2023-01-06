package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.hardware.Gamepad;

/*
 * Abstract gamepad controller base class.
 * 
 * Makes storing the current and previous states of the gamepad more convent.
 */
public abstract class GamepadControllerBase {
    protected Gamepad gamepad;
    protected Gamepad previous;

    protected GamepadControllerBase() {
        this.gamepad  = new Gamepad();
        this.previous = new Gamepad();
    }

    // Must be called at the beginning of each while opModeIsActive() loop.
    protected void update(Gamepad gamepad) {
        // NOTE: Requires the most up to date SDK Version
        this.previous.copy(this.gamepad);
        this.gamepad.copy(gamepad);
    }
}
