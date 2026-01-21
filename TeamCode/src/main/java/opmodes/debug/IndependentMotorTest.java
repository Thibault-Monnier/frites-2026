package opmodes.debug;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import config.HardwareConfig;

import opmodes.GroupConstants;

@TeleOp(
        name = GroupConstants.TEST_MODES_GROUP + ": Independent motor test",
        group = GroupConstants.TEST_MODES_GROUP)
public class IndependentMotorTest extends OpMode {
    private DcMotor testMotor1;
    private DcMotor testMotor2;

    private void resetMotor(DcMotor motor) {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void init() {
        testMotor1 = hardwareMap.get(DcMotor.class, HardwareConfig.TEST_MOTOR_1_ID);
        testMotor2 = hardwareMap.get(DcMotor.class, HardwareConfig.TEST_MOTOR_2_ID);
        resetMotor(testMotor1);
        resetMotor(testMotor2);

        telemetry.addData("Motor 1 power", HardwareConfig.TEST_MOTOR_1_POWER);
        telemetry.addData("Motor 2 power", HardwareConfig.TEST_MOTOR_2_POWER);
        telemetry.update();
    }

    @Override
    public void loop() {
        testMotor1.setPower(HardwareConfig.TEST_MOTOR_1_POWER);
        testMotor2.setPower(HardwareConfig.TEST_MOTOR_2_POWER);
    }
}
