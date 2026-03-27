package opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import logic.Team;

import opmodes.GroupConstants;
import opmodes.ManualOpMode;

@TeleOp(
        name = GroupConstants.MAIN_MODES_GROUP + ": CHILD Manual - RED Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class RedChild extends ManualOpMode {
    public RedChild() {
        super(Team.RED, false, true);
    }
}
