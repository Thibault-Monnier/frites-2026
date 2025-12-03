package core.opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import core.Constants;
import core.opmodes.ManualOpMode;

@TeleOp(
        name = Constants.MAIN_MODES_GROUP + ": No Pose Calculation - RED Team",
        group = Constants.MAIN_MODES_GROUP)
public class Red_No_Pose_Calculation extends ManualOpMode {
    public Red_No_Pose_Calculation() {
        super(Constants.Team.RED, false, false);
    }
}
