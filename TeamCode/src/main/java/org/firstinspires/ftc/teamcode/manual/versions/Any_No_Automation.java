package org.firstinspires.ftc.teamcode.manual.versions;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.manual.ManualOpMode;
import org.firstinspires.ftc.teamcode.robot.Arm;
import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(name = Constants.MAIN_MODES_GROUP + ": Main - ANY Team | WITHOUT Automation", group = Constants.MAIN_MODES_GROUP)
public class Any_No_Automation extends ManualOpMode {
    public Any_No_Automation() {
        super(Constants.Team.ANY_OR_UNKNOWN, Constants.StartPosition.ANY_OR_UNKNOWN, false, Arm.ColorSensorMode.NO_DETECTION);
    }
}
