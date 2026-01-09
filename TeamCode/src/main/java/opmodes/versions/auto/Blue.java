package opmodes.versions.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import logic.RobotPosition;
import logic.Team;

import opmodes.AutoOpModeOld;
import opmodes.GroupConstants;

@Autonomous(
        name = GroupConstants.MAIN_MODES_GROUP + ": Normal Auto - BLUE Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class Blue extends AutoOpModeOld {
    public Blue() {
        super(Team.BLUE, RobotPosition.StartPosition.NORMAL);
    }
}
