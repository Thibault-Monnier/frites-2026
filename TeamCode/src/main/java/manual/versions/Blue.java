package manual.versions;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import manual.ManualOpMode;
import robot.Constants;

@TeleOp(
        name = Constants.MAIN_MODES_GROUP + ": Main - BLUE Team",
        group = Constants.MAIN_MODES_GROUP)
public class Blue extends ManualOpMode {
    public Blue() {
        super(Constants.Team.BLUE, false);
    }
}
