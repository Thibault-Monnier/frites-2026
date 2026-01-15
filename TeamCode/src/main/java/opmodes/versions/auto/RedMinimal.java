package opmodes.versions.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import logic.Team;

import opmodes.AutoOpModeMinimal;
import opmodes.GroupConstants;

@Autonomous(
        name = GroupConstants.MAIN_MODES_GROUP + ": Normal Auto MINIMAL - RED Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class RedMinimal extends AutoOpModeMinimal {
    public RedMinimal() {
        super(Team.RED);
    }
}
