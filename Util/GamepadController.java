package org.firstinspires.ftc.teamcode.util;

/*
 * Gamepad controller class further extends gamepadcontroller base.
 * 
 * Provides the utility to extract what buttons are and aren't pressed.
 */
public class GamepadController extends GamepadControllerBase {
    public double getStick(GamepadButton stick) {
        switch (stick) {
            case LEFT_STICK_X:
                return this.gamepad.left_stick_x;
            case LEFT_STICK_Y:
                return this.gamepad.left_stick_y;
            case RIGHT_STICK_X:
                return this.gamepad.right_stick_x;
            case RIGHT_STICK_Y:
                return this.gamepad.right_stick_y;
            default:
                return 0.0; // TODO: Add an error message. No valid stick was given.
        }
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
                return false; // TODO: Add an error.
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
                return false; // TODO: Add an error.
        }
    }
}
