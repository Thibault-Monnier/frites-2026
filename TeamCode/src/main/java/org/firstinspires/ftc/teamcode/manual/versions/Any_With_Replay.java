package org.firstinspires.ftc.teamcode.manual.versions;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.manual.ManualOpMode;
import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(
        name = Constants.MAIN_MODES_GROUP + ": Main - ANY Team | Replay mode",
        group = Constants.MAIN_MODES_GROUP)
public class Any_With_Replay extends ManualOpMode {
    public Any_With_Replay() {
        super(Constants.Team.ANY_OR_UNKNOWN, true);
    }
}
