package org.firstinspires.ftc.teamcode.test;

import static org.firstinspires.ftc.teamcode.robot.Constants.ASCENT_MOTOR_ID;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.Constants;

@Config
@TeleOp(
        name = Constants.TEST_MODES_GROUP + ": Ascent Motor Test",
        group = Constants.TEST_MODES_GROUP)
public class AscentMotorTest extends OpMode {
    private static final int STEP = 10;

    private DcMotor motor;
    private int targetPosition;
    private Telemetry globalTelemetry;

    @Override
    public void init() {
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        motor = hardwareMap.get(DcMotor.class, ASCENT_MOTOR_ID);

        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        targetPosition = 0;
    }

    @Override
    public void loop() {
        if (gamepad1.y) {
            targetPosition += STEP;
        } else if (gamepad1.a) {
            targetPosition -= STEP;
        }

        motor.setPower(1.0);
        motor.setTargetPosition(targetPosition);

        globalTelemetry.addData("Target Position", targetPosition);
        globalTelemetry.addData("Current Position", motor.getCurrentPosition());
        globalTelemetry.addData("Motor Power", motor.getPower());
        globalTelemetry.update();
    }
}
