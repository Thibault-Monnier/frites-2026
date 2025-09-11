package org.firstinspires.ftc.teamcode.manual.versions;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.manual.ManualOpMode;
import org.firstinspires.ftc.teamcode.robot.Arm;
import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(name = Constants.MAIN_MODES_GROUP + ": Main - RED Team | WITH Automation", group = Constants.MAIN_MODES_GROUP)
public class Red_Automation extends ManualOpMode {
    public Red_Automation() {
        super(Constants.Team.RED, Constants.StartPosition.ANY_OR_UNKNOWN, false, Arm.ColorSensorMode.TEAM_ONLY);
    }
}
