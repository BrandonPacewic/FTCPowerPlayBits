package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import java.util.Arrays;
import java.util.List;

// Requires my GamepadInterface class.
import static org.firstinspires.ftc.teamcode.opmode.GamepadInterface.GamepadController;
import static org.firstinspires.ftc.teamcode.opmode.GamepadInterface.GamepadButton;

@TeleOp(name="Advanced Field Centric Mecanum Drive", group="TeleOp")
public class AdvancedMecanumDrive extends LinearOpMode {
    private DcMotorEx motorFrontLeft;
    private DcMotorEx motorFrontRight;
    private DcMotorEx motorBackLeft;
    private DcMotorEx motorBackRight;
    private List<DcMotorEx> mecanumMotors;

    private BNO055IMU imu;

    private static class MotorPowerFactors {
        public static final double lowDrive  = 0.3;
        public static final double highDrive = 0.6;
    }

    private final GamepadController gamepadController = new GamepadController();

    @Override
    public void runOpMode() throws InterruptedException {
        double driveMotorPowerFactor = MotorPowerFactors.lowDrive;

        motorFrontLeft  = hardwareMap.get(DcMotorEx.class, "motorFrontLeft");
        motorBackLeft   = hardwareMap.get(DcMotorEx.class, "motorBackLeft");
        motorFrontRight = hardwareMap.get(DcMotorEx.class, "motorFrontRight");
        motorBackRight  = hardwareMap.get(DcMotorEx.class, "motorBackRight");

        mecanumMotors = Arrays.asList(motorFrontLeft, motorBackLeft, motorFrontRight, motorBackRight);

        for (DcMotorEx motor : mecanumMotors) {
            MotorConfigurationType motorConfigurationType = motor.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            motor.setMotorType(motorConfigurationType);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        motorFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        while (opModeIsActive()) {
            gamepadController.update(gamepad1);

            double ly = -gamepadController.getLeftStickY(); // reversed
            double lx = gamepadController.getLeftStickX();
            double rx = gamepadController.getRightStickX();

            double botHeading = imu.getAngularOrientation().firstAngle;

            // Adjust the controller input by the robot's heading.
            double adjustedLy  = ly * Math.cos(botHeading) + lx * Math.sin(botHeading);
            double adjustedLx  = -ly * Math.sin(botHeading) + lx * Math.cos(botHeading);
            double denominator = Math.max(Math.abs(adjustedLy) + Math.abs(adjustedLx) + Math.abs(rx), 1);

            driveMotorPowerFactor = getDrivePowerFactor(driveMotorPowerFactor);

            motorFrontRight.setPower(((adjustedLy + adjustedLx + rx) / denominator) * driveMotorPowerFactor);
            motorBackLeft.setPower(((adjustedLy - adjustedLx + rx) / denominator) * driveMotorPowerFactor);
            motorFrontRight.setPower(((adjustedLy - adjustedLx - rx) / denominator) * driveMotorPowerFactor);
            motorBackRight.setPower(((adjustedLy + adjustedLx - rx) / denominator) * driveMotorPowerFactor);

            idle();
        }
    }

    private double getDrivePowerFactor(double previousPowerFactor) {
        if (!gamepadController.isPressed(GamepadButton.A)) {
            return previousPowerFactor;
        }

        if (previousPowerFactor == MotorPowerFactors.lowDrive) {
            return MotorPowerFactors.highDrive;
        } else {
            return MotorPowerFactors.lowDrive;
        }
    }
}
