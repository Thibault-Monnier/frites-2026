package core.opmodes.versions.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import core.logic.RobotPosition;
import core.logic.Team;
import core.opmodes.AutoOpMode;
import core.opmodes.GroupConstants;

@Autonomous(
        name = GroupConstants.MAIN_MODES_GROUP + ": Normal Auto - RED Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class Red extends AutoOpMode {
    public Red() {
        super(Team.RED, RobotPosition.StartPosition.NORMAL);
    }
}
