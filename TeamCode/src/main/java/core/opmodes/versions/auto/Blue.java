package core.opmodes.versions.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import core.Constants;
import core.opmodes.AutoOpMode;

@Autonomous(
        name = Constants.MAIN_MODES_GROUP + ": Normal - BLUE Team",
        group = Constants.MAIN_MODES_GROUP)
public class Blue extends AutoOpMode {
    public Blue() {
        super(Constants.Team.BLUE, Constants.StartPosition.NORMAL);
    }
}
