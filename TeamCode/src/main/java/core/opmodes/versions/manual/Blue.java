package core.opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import core.logic.Team;
import core.opmodes.GroupConstants;
import core.opmodes.ManualOpMode;

@TeleOp(
        name = GroupConstants.MAIN_MODES_GROUP + ": Normal Manual - BLUE Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class Blue extends ManualOpMode {
    public Blue() {
        super(Team.BLUE, false);
    }
}
