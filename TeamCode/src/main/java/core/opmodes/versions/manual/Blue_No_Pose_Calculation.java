package core.opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import core.Constants;
import core.opmodes.ManualOpMode;

@TeleOp(
        name = Constants.MAIN_MODES_GROUP + ": No Pose Calculation - BLUE Team",
        group = Constants.MAIN_MODES_GROUP)
public class Blue_No_Pose_Calculation extends ManualOpMode {
    public Blue_No_Pose_Calculation() {
        super(Constants.Team.BLUE, false, false);
    }
}
