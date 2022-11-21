import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

public class GamepadControllerBase {
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
            // Swallow exception, gamepad[1/2] should always be valid.
        }
    }
}

private class GamepadController extends GamepadControllerBase {
    public boolean isPressedA() {
        return gamepad.a && !previous.a;
    }

    public boolean isPressedB() {
        return gamepad.b && !previous.b;
    }

    public boolean isPressedX() {
        return gamepad.x && !previous.x;
    }

    public boolean isPressedY() {
        return gamepad.y && !previous.y;
    }

    public boolean isPressedDPadUp() {
        return gamepad.dpad_up && !previous.dpad_up;
    }

    public boolean isPressedDPadDown() {
        return gamepad.dpad_down && !previous.dpad_down;
    }

    public boolean isPressedDPadLeft() {
        return gamepad.dpad_left && !previous.dpad_left;
    }

    public boolean isPressedDPadRight() {
        return gamepad.dpad_right && !previous.dpad_right;
    }

    public boolean isPressedLeftBumper() {
        return gamepad.left_bumper && !previous.left_bumper;
    }

    public boolean isPressedRightBumper() {
        return gamepad.right_bumper && !previous.right_bumper;
    }

    public boolean isPressedLeftStickButton() {
        return gamepad.left_stick_button && !previous.left_stick_button;
    }

    public boolean isPressedRightStickButton() {
        return gamepad.right_stick_button && !previous.right_stick_button;
    }

    public boolean isPressedStart() {
        return gamepad.start && !previous.start;
    }

    public boolean isPressedBack() {
        return gamepad.back && !previous.back;
    }

    public boolean isPressedGuide() {
        return gamepad.guide && !previous.guide;
    }

    public boolean isPressedLeftTrigger() {
        return gamepad.left_trigger > 0.5 && previous.left_trigger <= 0.5;
    }

    public boolean isPressedRightTrigger() {
        return gamepad.right_trigger > 0.5 && previous.right_trigger <= 0.5;
    }

    public boolean isHeldA() {
        return gamepad.a && previous.a;
    }

    public boolean isHeldB() {
        return gamepad.b && previous.b;
    }

    public boolean isHeldX() {
        return gamepad.x && previous.x;
    }

    public boolean isHeldY() {
        return gamepad.y && previous.y;
    }

    public boolean isHeldDPadUp() {
        return gamepad.dpad_up && previous.dpad_up;
    }

    public boolean isHeldDPadDown() {
        return gamepad.dpad_down && previous.dpad_down;
    }

    public boolean isHeldDPadLeft() {
        return gamepad.dpad_left && previous.dpad_left;
    }

    public boolean isHeldDPadRight() {
        return gamepad.dpad_right && previous.dpad_right;
    }

    public boolean isHeldLeftBumper() {
        return gamepad.left_bumper && previous.left_bumper;
    }

    public boolean isHeldRightBumper() {
        return gamepad.right_bumper && previous.right_bumper;
    }

    public boolean isHeldLeftStickButton() {
        return gamepad.left_stick_button && previous.left_stick_button;
    }

    public boolean isHeldRightStickButton() {
        return gamepad.right_stick_button && previous.right_stick_button;
    }

    public boolean isHeldStart() {
        return gamepad.start && previous.start;
    }

    public boolean isHeldBack() {
        return gamepad.back && previous.back;
    }

    public boolean isHeldGuide() {
        return gamepad.guide && previous.guide;
    }

    public boolean isHeldLeftTrigger() {
        return gamepad.left_trigger > 0.5 && previous.left_trigger > 0.5;
    }

    public boolean isHeldRightTrigger() {
        return gamepad.right_trigger > 0.5 && previous.right_trigger > 0.5;
    }
}
