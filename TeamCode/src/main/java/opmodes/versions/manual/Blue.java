package opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import logic.Team;
import opmodes.GroupConstants;
import opmodes.ManualOpMode;

@TeleOp(
        name = GroupConstants.MAIN_MODES_GROUP + ": Normal Manual - BLUE Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class Blue extends ManualOpMode {
    public Blue() {
        super(Team.BLUE, false);
    }
}
