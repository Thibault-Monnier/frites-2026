package opmodes.debug;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import logic.PIDFCoefficients;
import logic.PIDFController;

import math.TimeHelpers;

import config.HardwareConfig;

@Config
@TeleOp
public class ShooterPIDTest extends LinearOpMode {
    DcMotorEx motorLeft;
    DcMotorEx motorRight;

    PIDFController PIDFControllerLeft;
    PIDFController PIDFControllerRight;

    public static double TARGET_VELOCITY = 1800;

    public static PIDFCoefficients TARGET_PIDF = new PIDFCoefficients(0.02, 0.0, 0.0, -0.5);

    public static double stepInterval = 0.02;

    @Override
    public void runOpMode() {
        this.motorLeft = hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_LEFT_ID);
        this.motorRight = hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_RIGHT_ID);

        this.motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        motorLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        motorRight.setDirection(DcMotorSimple.Direction.REVERSE);

        motorLeft.setMode(
                DcMotor.RunMode.RUN_WITHOUT_ENCODER); // Important to avoid an extra PID layer
        motorRight.setMode(
                DcMotor.RunMode.RUN_WITHOUT_ENCODER); // Important to avoid an extra PID layer

        PIDFControllerLeft =
                new PIDFController(motorLeft, HardwareConfig.SHOOTER_MAX_VELOCITY, telemetry);
        PIDFControllerRight =
                new PIDFController(motorRight, HardwareConfig.SHOOTER_MAX_VELOCITY, telemetry);

        waitForStart();

        double prevTime = TimeHelpers.getRuntime();
        while (opModeIsActive()) {
            while (true) {
                double time = TimeHelpers.getRuntime();
                if (time - prevTime >= stepInterval) {
                    telemetry.addData("Delta time", time - prevTime);
                    prevTime = time;
                    break;
                }
            }
            PIDFControllerLeft.setCoefficients(TARGET_PIDF);
            PIDFControllerRight.setCoefficients(TARGET_PIDF);
            telemetry.addData("PID", TARGET_PIDF.toString());

            motorLeft.setPower(PIDFControllerLeft.get(TARGET_VELOCITY, true));
            motorRight.setPower(PIDFControllerRight.get(TARGET_VELOCITY, true));

            telemetry.update();
        }
    }
}
