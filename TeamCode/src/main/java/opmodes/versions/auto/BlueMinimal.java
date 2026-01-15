package opmodes.versions.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import logic.Team;

import opmodes.AutoOpModeMinimal;
import opmodes.GroupConstants;

@Autonomous(
        name = GroupConstants.MAIN_MODES_GROUP + ": Normal Auto MINIMAL - BLUE Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class BlueMinimal extends AutoOpModeMinimal {
    public BlueMinimal() {
        super(Team.BLUE);
    }
}
