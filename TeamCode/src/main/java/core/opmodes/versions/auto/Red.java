package core.opmodes.versions.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import core.Constants;
import core.opmodes.AutoOpMode;

@Autonomous(name = Constants.MAIN_MODES_GROUP + ": Normal Auto - RED Team", group = Constants.MAIN_MODES_GROUP)
public class Red extends AutoOpMode {
    public Red() {
        super(Constants.Team.RED, Constants.StartPosition.NORMAL);
    }
}
