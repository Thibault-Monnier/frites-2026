package core.opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import core.logic.Team;
import core.opmodes.GroupConstants;
import core.opmodes.ManualOpMode;

@TeleOp(
        name = GroupConstants.MAIN_MODES_GROUP + ": No Pose Calculation - RED Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class Red_No_Pose_Calculation extends ManualOpMode {
    public Red_No_Pose_Calculation() {
        super(Team.RED, false, false);
    }
}
