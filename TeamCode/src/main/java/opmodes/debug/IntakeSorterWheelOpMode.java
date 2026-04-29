package opmodes.debug;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

import opmodes.GroupConstants;

@TeleOp(
        name = GroupConstants.TEST_MODES_GROUP + ": Intake Sorter Wheel",
        group = GroupConstants.TEST_MODES_GROUP)
public class IntakeSorterWheelOpMode extends LinearOpMode {

    @Override
    public void runOpMode() {
        CRServo wheel = hardwareMap.get(CRServo.class, "wheel");

        waitForStart();

        while (opModeIsActive()) {
            wheel.setPower(1.0);
        }
    }
}
