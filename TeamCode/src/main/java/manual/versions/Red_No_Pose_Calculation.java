package manual.versions;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import manual.ManualOpMode;
import robot.Constants;

@TeleOp(
        name = Constants.MAIN_MODES_GROUP + ": Main - RED Team | No Pose Calculation",
        group = Constants.MAIN_MODES_GROUP)
public class Red_No_Pose_Calculation extends ManualOpMode {
    public Red_No_Pose_Calculation() {
        super(Constants.Team.RED, false, false);
    }
}
