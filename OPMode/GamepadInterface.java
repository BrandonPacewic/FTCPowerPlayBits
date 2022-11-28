package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

public class GamepadInterface {
    private static abstract class GamepadControllerBase {
        protected Gamepad gamepad;
        protected Gamepad previous;

        protected GamepadControllerBase() {
            this.gamepad  = new Gamepad();
            this.previous = new Gamepad();
        }

        // Must be called at the beginning of each while opModeIsActive() loop.
        protected void update(Gamepad gamepad) {
            try {
                this.previous.copy(this.gamepad);
                this.gamepad.copy(gamepad);
            } catch (RobotCoreException e) {
                // Swallow exception, gamepad[1/2] should always be valid unless unplugged.
            }
        }
    }

    public enum GamepadButton {
        A,
        B,
        X,
        Y,
        DPAD_UP,
        DPAD_DOWN,
        DPAD_LEFT,
        DPAD_RIGHT,
        LEFT_BUMPER,
        RIGHT_BUMPER,
        LEFT_STICK_BUTTON,
        RIGHT_STICK_BUTTON,
        GUIDE,
        START,
        BACK,
        LEFT_TRIGGER,
        RIGHT_TRIGGER,
        LEFT_STICK_X,
        LEFT_STICK_Y,
        RIGHT_STICK_X,
        RIGHT_STICK_Y
    }

    public static class GamepadController extends GamepadControllerBase {
        public double getLeftStickX() {
            return this.gamepad.left_stick_x;
        }

        public double getLeftStickY() {
            return this.gamepad.left_stick_y;
        }

        public double getRightStickX() {
            return this.gamepad.right_stick_x;
        }

        public double getRightStickY() {
            return this.gamepad.right_stick_y;
        }

        public boolean isPressed(GamepadButton gamepadButton) {
            switch (gamepadButton) {
                case A:
                    return this.gamepad.a && !this.previous.a;
                case B:
                    return this.gamepad.b && !this.previous.b;
                case X:
                    return this.gamepad.x && !this.previous.x;
                case Y:
                    return this.gamepad.y && !this.previous.y;
                case DPAD_UP:
                    return this.gamepad.dpad_up && !this.previous.dpad_up;
                case DPAD_DOWN:
                    return this.gamepad.dpad_down && !this.previous.dpad_down;
                case DPAD_LEFT:
                    return this.gamepad.dpad_left && !this.previous.dpad_left;
                case DPAD_RIGHT:
                    return this.gamepad.dpad_right && !this.previous.dpad_right;
                case LEFT_BUMPER:
                    return this.gamepad.left_bumper && !this.previous.left_bumper;
                case RIGHT_BUMPER:
                    return this.gamepad.right_bumper && !this.previous.right_bumper;
                case LEFT_STICK_BUTTON:
                    return this.gamepad.left_stick_button && !this.previous.left_stick_button;
                case RIGHT_STICK_BUTTON:
                    return this.gamepad.right_stick_button && !this.previous.right_stick_button;
                case GUIDE:
                    return this.gamepad.guide && !this.previous.guide;
                case START:
                    return this.gamepad.start && !this.previous.start;
                case BACK:
                    return this.gamepad.back && !this.previous.back;
                case LEFT_TRIGGER:
                    return this.gamepad.left_trigger > 0.0 && this.previous.left_trigger == 0.0;
                case RIGHT_TRIGGER:
                    return this.gamepad.right_trigger > 0.0 && this.previous.right_trigger == 0.0;
                case LEFT_STICK_X:
                    return this.gamepad.left_stick_x != this.previous.left_stick_x;
                case LEFT_STICK_Y:
                    return this.gamepad.left_stick_y != this.previous.left_stick_y;
                case RIGHT_STICK_X:
                    return this.gamepad.right_stick_x != this.previous.right_stick_x;
                case RIGHT_STICK_Y:
                    return this.gamepad.right_stick_y != this.previous.right_stick_y;
                default:
                    return false;
            }
        }

        public boolean isHeld(GamepadButton gamepadButton) {
            switch (gamepadButton) {
                case A:
                    return this.gamepad.a;
                case B:
                    return this.gamepad.b;
                case X:
                    return this.gamepad.x;
                case Y:
                    return this.gamepad.y;
                case DPAD_UP:
                    return this.gamepad.dpad_up;
                case DPAD_DOWN:
                    return this.gamepad.dpad_down;
                case DPAD_LEFT:
                    return this.gamepad.dpad_left;
                case DPAD_RIGHT:
                    return this.gamepad.dpad_right;
                case LEFT_BUMPER:
                    return this.gamepad.left_bumper;
                case RIGHT_BUMPER:
                    return this.gamepad.right_bumper;
                case LEFT_STICK_BUTTON:
                    return this.gamepad.left_stick_button;
                case RIGHT_STICK_BUTTON:
                    return this.gamepad.right_stick_button;
                case GUIDE:
                    return this.gamepad.guide;
                case START:
                    return this.gamepad.start;
                case BACK:
                    return this.gamepad.back;
                case LEFT_TRIGGER:
                    return this.gamepad.left_trigger > 0.0;
                case RIGHT_TRIGGER:
                    return this.gamepad.right_trigger > 0.0;
                case LEFT_STICK_X:
                    return this.gamepad.left_stick_x != 0.0;
                case LEFT_STICK_Y:
                    return this.gamepad.left_stick_y != 0.0;
                case RIGHT_STICK_X:
                    return this.gamepad.right_stick_x != 0.0;
                case RIGHT_STICK_Y:
                    return this.gamepad.right_stick_y != 0.0;
                default:
                    return false;
            }
        }
    }
}
