package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(name = Constants.TEST_MODES_GROUP + ": Independent motor test", group = Constants.TEST_MODES_GROUP)
public class IndependentMotorTest extends OpMode {
    private DcMotor testMotor1;
    private DcMotor testMotor2;

    private void resetMotor(DcMotor motor) {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void init() {
        testMotor1 = hardwareMap.get(DcMotor.class, Constants.TEST_MOTOR_1_ID);
        testMotor2 = hardwareMap.get(DcMotor.class, Constants.TEST_MOTOR_2_ID);
        resetMotor(testMotor1);
        resetMotor(testMotor2);

        telemetry.addData("Motor power", Constants.TEST_POWER);
        telemetry.update();
    }

    @Override
    public void loop() {
        testMotor1.setPower(Constants.TEST_POWER);
        testMotor2.setPower(Constants.TEST_POWER);
    }
}
