package org.firstinspires.ftc.teamcode.manual.versions;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.manual.ManualOpMode;
import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(
        name = Constants.MAIN_MODES_GROUP + ": Main - BLUE Team | WITH Automation",
        group = Constants.MAIN_MODES_GROUP)
public class Blue_Automation extends ManualOpMode {
    public Blue_Automation() {
        super(Constants.Team.BLUE, false);
    }
}
