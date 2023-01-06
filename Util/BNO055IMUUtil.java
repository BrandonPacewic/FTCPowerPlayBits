package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;

/*
 * This class provides the utility to remap the axes of the BNO055IMU.
 * This is useful when the IMU is mounted in a non-standard orientation.
 * 
 * Adapted from the Roadrunner quickstart.
 */
public class BNO055IMUUtil {
    // Message for invalid remapping of the axes.
    private static class InvalidAxisRemapException extends RuntimeException {
        public InvalidAxisRemapException(String message) {
            super(message);
        }
    }

    private static void swapThenFlipAxes(BNO055IMU imu, AxesOrder order, AxesSigns signs) {
        try {
            int[] indices = order.indices();
            int axisMapSigns = signs.bVal;

            if (indices[0] == indices[1] || indices[0] == indices[2] || indices[1] == indices[2]) {
                throw new InvalidAxisRemapException("Axes must be unique");
            }

            // Ensure a right-handed coordinate system.
            boolean isXSwapped = indices[0] != 0;
            boolean isYSwapped = indices[1] != 1;
            boolean isZSwapped = indices[2] != 2;
            boolean areTwoAxesSwapped = (isXSwapped ? 1 : 0) + (isYSwapped ? 1 : 0) + (isZSwapped ? 1 : 0) == 2;
            boolean oddNumOfFlips = Integer.bitCount(axisMapSigns) % 2 == 1;

            if (areTwoAxesSwapped != oddNumOfFlips) {
                throw new InvalidAxisRemapException("Coordinate system is left-handed");
            }

            int axisMapConfig = indices[2] << 4 | indices[1] << 2 | indices[0];

            imu.write8(BNO055IMU.Register.OPR_MODE, BNO055IMU.SensorMode.CONFIG.bVal & 0x0F);

            Thread.sleep(100);

            imu.write8(BNO055IMU.Register.AXIS_MAP_CONFIG, axisMapConfig & 0x3F);
            imu.write8(BNO055IMU.Register.AXIS_MAP_SIGN, axisMapSigns & 0x07);
            imu.write8(BNO055IMU.Register.OPR_MODE, imu.getParameters().mode.bVal & 0x0F);

            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void remapZAxis(BNO055IMU imu, AxisDirection direction) {
        switch (direction) {
            case POS_X:
                swapThenFlipAxes(imu, AxesOrder.ZYX, AxesSigns.NPP);
                break;
            case NEG_X:
                swapThenFlipAxes(imu, AxesOrder.ZYX, AxesSigns.PPN);
                break;
            case POS_Y:
                swapThenFlipAxes(imu, AxesOrder.XZY, AxesSigns.PNP);
                break;
            case NEG_Y:
                swapThenFlipAxes(imu, AxesOrder.XZY, AxesSigns.PPN);
                break;
            case POS_Z:
                swapThenFlipAxes(imu, AxesOrder.XYZ, AxesSigns.PPP);
                break;
            case NEG_Z:
                swapThenFlipAxes(imu, AxesOrder.XYZ, AxesSigns.PNN);
                break;
        }
    }
}
