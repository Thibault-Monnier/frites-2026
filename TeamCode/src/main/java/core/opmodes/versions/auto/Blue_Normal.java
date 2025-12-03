package core.opmodes.versions.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import core.modules.Constants;
import core.opmodes.AutoOpMode;

@Autonomous(name = Constants.MAIN_MODES_GROUP + ": Blue Normal", group = Constants.MAIN_MODES_GROUP)
public class Blue_Normal extends AutoOpMode {
    public Blue_Normal() {
        super(Constants.Team.BLUE, Constants.StartPosition.NORMAL);
    }
}
