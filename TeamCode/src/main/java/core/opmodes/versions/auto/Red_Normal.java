package core.opmodes.versions.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import core.Constants;
import core.opmodes.AutoOpMode;

@Autonomous(name = Constants.MAIN_MODES_GROUP + ": Red Normal", group = Constants.MAIN_MODES_GROUP)
public class Red_Normal extends AutoOpMode {
    public Red_Normal() {
        super(Constants.Team.RED, Constants.StartPosition.NORMAL);
    }
}
