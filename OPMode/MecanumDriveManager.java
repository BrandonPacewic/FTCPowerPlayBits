package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import java.util.Arrays;
import java.util.List;

import org.firstinspires.ftc.teamcode.util.AxisDirection;
import org.firstinspires.ftc.teamcode.util.BNO055IMUUtil;

/*
 * Simple class to manage a four wheel mecanum drive train.
 * 
 * This class assumes that the drive motors are named with the following convention:
 *  - motorFrontLeft
 *  - motorFrontRight
 *  - motorBackLeft
 *  - motorBackRight
 * 
 * This class also assumes that all four drive motors are of the same type.
 * If they are not you can still use this class, however the drive using encoder
 * functionality will likely not work as expected.
 * 
 * This class has the following features:
 *  - Field centric drive
 *  - Motor power scaling
 *  - Drive set distance with motor encoders
 */
public class MecanumDriveManager {
    private DcMotorEx frontLeft;
    private DcMotorEx frontRight;
    private DcMotorEx backLeft;
    private DcMotorEx backRight;

    private List<DcMotorEx> motors;

    private BNO055IMU imu;

    // The following is used to correct for the fact that not every drive train is the same.
    // Some will need to reverse different motors and some will need to reverse the input of different gamepad axes.
    private boolean FLIP_X        = false;
    private boolean FLIP_Y        = false;
    private boolean FLIP_ROTATION = false;

    public enum MODE {
        BOT_CENTRIC,
        FIELD_CENTRIC
    }

    private MODE mode;

    /*
     * The following constants are only used for encoder drive. If you do not intend to use encoder drive
     * simply ignore these values.
     * 
     * TICKS_PER_REV should be taken directly from the motor manufacturer.
     * 
     * WHEEL_DIAMETER is the diameter of the mecanum wheels in inches.
     * 
     * GEAR_RATIO is the gear ratio between the motor and the wheel. This is typically 1:1, however
     * if you have a gearbox between the motor and the wheel you will need to take that into account.
     * 
     * TRACK_WIDTH is the lateral distance between the center of the left and right side wheels.
     * It's ok if this is only an estimate however if you have significant inaccuracies in your turning
     * come back to this value.
     * 
     * All units are in inches.
     */
    private static double TICKS_PER_REV  = 0;
    private static double WHEEL_DIAMETER = 0;
    private static double GEAR_RATIO     = 0;
    private static double TRACK_WIDTH    = 0;

    // The following value is used to set the power of the motors when driving using the encoders.
    // It is useful for the value to be about 30% of your maximum motor power as
    // driving using the encoders with mecanum wheels can become extremely inaccurate at higher speeds.
    private static double ENCODER_DRIVE_POWER = 0.3;

    public MecanumDriveManager(HardwareMap hardwareMap) {
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight  = hardwareMap.get(DcMotorEx.class, "backRight");

        motors = Arrays.asList(frontLeft, frontRight, backLeft, backRight);

        for (DcMotorEx motor : motors) {
            MotorConfigurationType motorConfigurationType = motor.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            motor.setMotorType(motorConfigurationType);
        }

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // TODO: Reverse any motors using DcMotor.setDirection()

        // TODO: If you do not intend to use the IMU, comment out the IMU initialization.
        // This section can add 2-3 seconds during the init phase of the robot
        // and can be quite annoying if you are not using it.
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);

        // The following section is taken from the Roadrunner quickstart.
        // TODO: If the hub containing the IMU you are using is mounted so that the "REV" logo does
        // not face up, remap the IMU axes so that the z-axis points upward (normal to the floor.)
        //
        //             | +Z axis
        //             |
        //             |
        //             |
        //      _______|_____________     +Y axis
        //     /       |_____________/|__________
        //    /   REV / EXPANSION   //
        //   /       / HUB         //
        //  /_______/_____________//
        // |_______/_____________|/
        //        /
        //       / +X axis
        //
        // This diagram is derived from the axes in section 3.4 https://www.bosch-sensortec.com/media/boschsensortec/downloads/datasheets/bst-bno055-ds000.pdf
        // and the placement of the dot/orientation from https://docs.revrobotics.com/rev-control-system/control-system-overview/dimensions#imu-location
        //
        // For example, if +Y in this diagram faces downwards, you would use AxisDirection.NEG_Y.
        // BNO055IMUUtil.remapZAxis(imu, AxisDirection.NEG_Y);
        BNO055IMUUtil.remapZAxis(imu, AxisDirection.NEG_Y);

        setMode(MODE.BOT_CENTRIC);
    }

    private static double inchesToEncoderTicks(double inches) {
        return (inches / (WHEEL_DIAMETER * Math.PI)) * TICKS_PER_REV * GEAR_RATIO;
    }

    private List<Double> getInvertedAxes(double x, double y, double turn) {
        return Arrays.asList(
                FLIP_X ? -x : x,
                FLIP_Y ? -y : y,
                FLIP_ROTATION ? -turn : turn
            );
    }

    // Used to reset the drift of the IMU.
    // WARNING: Doing this un-intentionally can cause the robot to drive in a direction that is not expected.
    // Before using this method ensure that the robot is facing the correct direction.
    public void resetIMUZeroHeading() {
        imu.initialize(imu.getParameters());
    }

    public void setMotorPowers(double power) {
        for (DcMotorEx motor : motors) {
            motor.setPower(power);
        }
    }

    public void setMotorPowers(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower) {
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }

    public void setDrivePower(double x, double y, double turn) {
        List<Double> invertedAxes = getInvertedAxes(x, y, turn);
        x    = invertedAxes.get(0);
        y    = invertedAxes.get(1);
        turn = invertedAxes.get(2);

        if (mode == MODE.FIELD_CENTRIC) {
            double botHeading = getRawExternalHeading();

            double adjustedX = -y * Math.sin(botHeading) + x * Math.cos(botHeading);
            double adjustedY =  y * Math.cos(botHeading) + x * Math.sin(botHeading);

            x = adjustedX;
            y = adjustedY;
        }

        double frontLeft  = y + x + turn;
        double frontRight = y - x - turn;
        double backLeft   = y - x + turn;
        double backRight  = y + x - turn;

        // Used to keep all the motor powers within the same ratio.
        double denominator = Math.max(
                Math.max(Math.abs(y), Math.abs(x)) + Math.abs(turn), 1
            );

        setMotorPowers(
                frontLeft  / denominator,
                frontRight / denominator,
                backLeft   / denominator,
                backRight  / denominator
            );
    }

    public void setWeightedDrivePower(double x, double y, double turn, double powerFactor) {
        List<Double> invertedAxes = getInvertedAxes(x, y, turn);
        x    = invertedAxes.get(0);
        y    = invertedAxes.get(1);
        turn = invertedAxes.get(2);

        if (mode == MODE.FIELD_CENTRIC) {
            double botHeading = getRawExternalHeading();

            double adjustedX = -y * Math.sin(botHeading) + x * Math.cos(botHeading);
            double adjustedY =  y * Math.cos(botHeading) + x * Math.sin(botHeading);

            x = adjustedX;
            y = adjustedY;
        }

        double frontLeft  = y + x + turn;
        double frontRight = y - x - turn;
        double backLeft   = y - x + turn;
        double backRight  = y + x - turn;

        // Used to keep all the motor powers within the same ratio.
        double denominator = Math.max(
                Math.max(Math.abs(y), Math.abs(x)) + Math.abs(turn), 1
            );

        setMotorPowers(
                (frontLeft  / denominator) * powerFactor,
                (frontRight / denominator) * powerFactor,
                (backLeft   / denominator) * powerFactor,
                (backRight  / denominator) * powerFactor
            );
    }

    public void flipX() { // Used for correcting variations in different drive trains.
        FLIP_X = !FLIP_X;
    }

    public void flipY() { // Used for correcting variations in different drive trains.
        FLIP_Y = !FLIP_Y;
    }

    public void flipRotation() { // Used for correcting variations in different drive trains.
        FLIP_ROTATION = !FLIP_ROTATION;
    }

    public double getRawExternalHeading() {
        return imu.getAngularOrientation().firstAngle;
    }

    private double radiansToEncoderTicks(double radians) {
        return radians * (TICKS_PER_REV * GEAR_RATIO) / (2 * Math.PI);
    }

    public void setMode(DcMotor.RunMode runMode) {
        for (DcMotorEx motor : motors) {
            motor.setMode(runMode);
        }
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    private void setTargetPosition(int targetPosition) {
        for (DcMotorEx motor : motors) {
            motor.setTargetPosition(targetPosition);
        }
    }

    private void setTargetPositions(int frontLeft, int frontRight, int backLeft, int backRight) {
        frontLeft.setTargetPosition(frontLeft);
        frontRight.setTargetPosition(frontRight);
        backLeft.setTargetPosition(backLeft);
        backRight.setTargetPosition(backRight);
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        for (DcMotorEx motor : motors) {
            motor.setZeroPowerBehavior(zeroPowerBehavior);
        }
    }

    /*
     * The following methods are used for encoder driving.
     * 
     * All units are in inches / radians.
     */
    public boolean isBusy() {
        for (DcMotorEx motor : motors) {
            if (motor.isBusy()) {
                return true;
            }
        }

        return false;
    }

    public void waitForIdle() {
        while (isBusy()) {
            // Wait for the motors to finish moving.
        }
    }

    public void forwards(double distance) {
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setTargetPosition((int) inchesToEncoderTicks(distance));
        setMotorPowers(ENCODER_DRIVE_POWER);

        waitForIdle();
    }

    public void backwards(double distance) {
        forwards(-distance);
    }

    public void strafeLeft(double distance) {
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setTargetPositions(
                (int) inchesToEncoderTicks(distance),
                (int) inchesToEncoderTicks(-distance),
                (int) inchesToEncoderTicks(-distance),
                (int) inchesToEncoderTicks(distance)
            );
        setMotorPowers(ENCODER_DRIVE_POWER);
            
        waitForIdle();
    }

    public void strafeRight(double distance) {
        strafeLeft(-distance);
    }

    public void turn(double radians) {
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setTargetPositions(
                (int) radiansToEncoderTicks(radians),
                (int) radiansToEncoderTicks(-radians),
                (int) radiansToEncoderTicks(radians),
                (int) radiansToEncoderTicks(-radians)
            );
        setMotorPowers(ENCODER_DRIVE_POWER);
            
        waitForIdle();
    }
}
