package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(name = Constants.TEST_MODES_GROUP + ": Independent motor test", group = Constants.TEST_MODES_GROUP)
public class IndependentMotorTest extends OpMode {
    private final static double TEST_POWER = 1.0;

    private DcMotor testMotor;

    @Override
    public void init() {
        testMotor = hardwareMap.get(DcMotor.class, Constants.TEST_MOTOR_ID);
        testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        testMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Power", TEST_POWER);
        telemetry.update();
    }

    @Override
    public void loop() {
        testMotor.setPower(TEST_POWER);
    }
}
