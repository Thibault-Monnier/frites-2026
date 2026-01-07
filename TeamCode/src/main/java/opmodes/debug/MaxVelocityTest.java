package opmodes.debug;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.opencv.core.Mat;

import modules.HardwareConstants;

@Config
@TeleOp
public class MaxVelocityTest extends LinearOpMode {
    DcMotorEx motorLeft;
    DcMotorEx motorRight;
    double currentVelocityLeft = 0;
    double currentVelocityRight = 0;
    double maxVelocityLeft = 0;
    double maxVelocityRight = 0;
    public static double TARGET_VELOCITY = 1800;

    public static PIDFCoefficients TARGET_PIDF = new PIDFCoefficients(0, 0, 0, 13.5);

    @Override
    public void runOpMode() {
        this.motorLeft = hardwareMap.get(DcMotorEx.class, HardwareConstants.CANNON_MOTOR_LEFT_ID);
        this.motorRight = hardwareMap.get(DcMotorEx.class, HardwareConstants.CANNON_MOTOR_RIGHT_ID);

//        PIDFCoefficients pidf = new PIDFCoefficients(0, 0,0, 28.75);


        this.motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        motorLeft.setVelocity(-TARGET_VELOCITY);
        motorRight.setVelocity(TARGET_VELOCITY);

        while (opModeIsActive()) {
            PIDFCoefficients pidf = TARGET_PIDF;
            this.motorLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
            this.motorRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

            currentVelocityLeft = Math.abs(motorLeft.getVelocity());
            currentVelocityRight = Math.abs(motorRight.getVelocity());

            if (currentVelocityLeft > maxVelocityLeft) {
                maxVelocityLeft = currentVelocityLeft;
            }
            if (currentVelocityRight > maxVelocityRight) {
                maxVelocityRight = currentVelocityRight;
            }

            telemetry.addData("PIDF", pidf.toString());
            telemetry.addData("currentVelocityLeft", currentVelocityLeft);
            telemetry.addData("maxVelocityLeft", maxVelocityLeft);
            telemetry.addData("currentVelocityRight", currentVelocityRight);
            telemetry.addData("maxVelocityRight", maxVelocityRight);

            telemetry.update();
        }
    }
}
