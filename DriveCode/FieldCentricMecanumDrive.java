package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp(name="Field Centric Mecanum Drive", group="TeleOp")
public class FieldCentricMecanumDrive extends LinearOpMode {
    // General gamepad controller, all robot specific methods will be placed inside
    // the robot specific implementation of GamepadController.
    private class GamepadControllerBase {
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
    }

    private static class MotorPowerFactors {
        public static final double lowDrive = 0.3;
        public static final double highDrive = 0.75;
    }

    private double getDrivePowerFactor(final double previousPowerFactor) {
        if (!gamepadController.isPressedA()) {
            return previousPowerFactor;
        }

        if (previousPowerFactor == MotorPowerFactors.lowDrive) {
            return MotorPowerFactors.highDrive;
        } else {
            return MotorPowerFactors.lowDrive;
        }
    }

    private final GamepadController gamepadController = new GamepadController();

    @Override
    public void runOpMode() throws InterruptedException {
        double driveMotorPowerFactor = MotorPowerFactors.lowDrive;

        DcMotor[] mecanumMotors = {
            hardwareMap.get(DcMotor.class, "motorFrontLeft"),
            hardwareMap.get(DcMotor.class, "motorBackLeft"),
            hardwareMap.get(DcMotor.class, "motorFrontRight"),
            hardwareMap.get(DcMotor.class, "motorBackRight"),
        };

        // Reverse left side motors.
        mecanumMotors[0].setDirection(DcMotorSimple.Direction.REVERSE);
        mecanumMotors[1].setDirection(DcMotorSimple.Direction.REVERSE);

        // Retrieve imu from hardware map.
        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        while (opModeIsActive()) {
            gamepadController.update(gamepad1);

            double ly = -gamepadController.gamepad.left_stick_y; // reversed
            double lx = -gamepadController.gamepad.left_stick_x; // reversed
            double rx = gamepadController.gamepad.right_stick_x;

            double botHeading = imu.getAngularOrientation().firstAngle;

            // Adjust the controller input by the robot's heading.
            double adjustedLy  = ly * Math.cos(botHeading) + lx * Math.sin(botHeading);
            double adjustedLx  = -ly * Math.sin(botHeading) + lx * Math.cos(botHeading);
            double denominator = Math.max(Math.abs(ly) + Math.abs(lx) + Math.abs(rx), 1);

            double[] motorPowers = {
                (adjustedLy + adjustedLx + rx) / denominator, // front left
                (adjustedLy - adjustedLx + rx) / denominator, // back left
                (adjustedLy - adjustedLx - rx) / denominator, // front right
                (adjustedLy + adjustedLx - rx) / denominator, // back right
            };

            driveMotorPowerFactor = getDrivePowerFactor(driveMotorPowerFactor);

            for (int i = 0; i < motorPowers.length; i++) {
                mecanumMotors[i].setPower(motorPowers[i] * driveMotorPowerFactor);
            }

            idle();
        }
    }
}
