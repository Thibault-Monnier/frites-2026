package opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import logic.Team;
import opmodes.GroupConstants;
import opmodes.ManualOpMode;

@TeleOp(
        name = GroupConstants.MAIN_MODES_GROUP + ": No Pose Calculation - RED Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class Red_No_Pose_Calculation extends ManualOpMode {
    public Red_No_Pose_Calculation() {
        super(Team.RED, false, false);
    }
}
