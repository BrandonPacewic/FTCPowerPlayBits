package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp(name="Mecanum Drive", group="TeleOp")
public class MecanumDrive extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor motorFrontLeft  = hardwareMap.dcMotor.get("motorFrontLeft");
        DcMotor motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        DcMotor motorBackLeft   = hardwareMap.dcMotor.get("motorBackLeft");
        DcMotor motorBackRight  = hardwareMap.dcMotor.get("motorBackRight");

        // Wait for the start button to be pressed.
        waitForStart();

        // Loop until the stop button is pressed.
        while (opModeIsActive()) {
            double ly = -gamepad1.left_stick_y; // Reversed.
            double lx =  gamepad1.left_stick_x;
            double rx =  gamepad1.right_stick_x;
            double denominator = Math.max(Math.abs(ly) + Math.abs(lx) + Math.abs(rx), 1);

            motorFrontLeft.setPower((ly + lx + rx) / denominator);
            motorFrontRight.setPower((ly - lx - rx) / denominator);
            motorBackLeft.setPower((ly - lx + rx) / denominator);
            motorBackRight.setPower((ly + lx - rx) / denominator);

            idle();
        }
    }
}
