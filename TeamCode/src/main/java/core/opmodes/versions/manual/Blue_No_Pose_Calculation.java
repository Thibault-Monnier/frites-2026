package core.opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import core.logic.Team;
import core.opmodes.GroupConstants;
import core.opmodes.ManualOpMode;

@TeleOp(
        name = GroupConstants.MAIN_MODES_GROUP + ": No Pose Calculation - BLUE Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class Blue_No_Pose_Calculation extends ManualOpMode {
    public Blue_No_Pose_Calculation() {
        super(Team.BLUE, false, false);
    }
}
