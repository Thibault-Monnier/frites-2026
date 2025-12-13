package opmodes.versions.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import logic.RobotPosition;
import logic.Team;
import opmodes.AutoOpMode;
import opmodes.GroupConstants;

@Autonomous(
        name = GroupConstants.MAIN_MODES_GROUP + ": Normal Auto - RED Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class Red extends AutoOpMode {
    public Red() {
        super(Team.RED, RobotPosition.StartPosition.NORMAL);
    }
}
