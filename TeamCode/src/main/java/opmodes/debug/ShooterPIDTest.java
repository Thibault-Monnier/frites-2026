package opmodes.debug;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import logic.PIDCoefficients;
import logic.PIDController;

import modules.HardwareConstants;

@Config
@TeleOp
public class ShooterPIDTest extends LinearOpMode {
    DcMotorEx motorLeft;
    DcMotorEx motorRight;

    PIDController pidControllerLeft;
    PIDController pidControllerRight;

    public static double TARGET_VELOCITY = 1800;

    public static PIDCoefficients TARGET_PID = new PIDCoefficients(50.0, 0.0, 0.0);

    @Override
    public void runOpMode() {
        this.motorLeft = hardwareMap.get(DcMotorEx.class, HardwareConstants.CANNON_MOTOR_LEFT_ID);
        this.motorRight = hardwareMap.get(DcMotorEx.class, HardwareConstants.CANNON_MOTOR_RIGHT_ID);

        this.motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        motorLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        motorRight.setDirection(DcMotorSimple.Direction.REVERSE);

        motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pidControllerLeft =
                new PIDController(motorLeft, HardwareConstants.SHOOTER_MAX_VELOCITY, telemetry);
        pidControllerRight =
                new PIDController(motorRight, HardwareConstants.SHOOTER_MAX_VELOCITY, telemetry);

        waitForStart();

        while (opModeIsActive()) {
            pidControllerLeft.setCoefficients(TARGET_PID);
            pidControllerRight.setCoefficients(TARGET_PID);
            telemetry.addData("PID", TARGET_PID.toString());

            motorLeft.setPower(pidControllerLeft.get(TARGET_VELOCITY, true));
            motorRight.setPower(pidControllerRight.get(TARGET_VELOCITY, true));

            telemetry.update();
        }
    }
}
