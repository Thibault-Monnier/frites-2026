package opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import logic.Team;
import opmodes.GroupConstants;
import opmodes.ManualOpMode;

@TeleOp(
        name = GroupConstants.MAIN_MODES_GROUP + ": CHILD Manual - BLUE Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class BlueChild extends ManualOpMode {
    public BlueChild() {
        super(Team.BLUE, false, true);
    }
}
