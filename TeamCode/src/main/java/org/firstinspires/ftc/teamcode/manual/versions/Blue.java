package org.firstinspires.ftc.teamcode.manual.versions;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.manual.ManualOpMode;
import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(
        name = Constants.MAIN_MODES_GROUP + ": Main - BLUE Team",
        group = Constants.MAIN_MODES_GROUP)
public class Blue extends ManualOpMode {
    public Blue() {
        super(Constants.Team.BLUE, false);
    }
}
