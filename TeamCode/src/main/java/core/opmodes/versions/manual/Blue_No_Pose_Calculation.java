package core.opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import core.modules.Constants;
import core.opmodes.ManualOpMode;

@TeleOp(
        name = Constants.MAIN_MODES_GROUP + ": Main - BLUE Team | No Pose Calculation",
        group = Constants.MAIN_MODES_GROUP)
public class Blue_No_Pose_Calculation extends ManualOpMode {
    public Blue_No_Pose_Calculation() {
        super(Constants.Team.BLUE, false, false);
    }
}
