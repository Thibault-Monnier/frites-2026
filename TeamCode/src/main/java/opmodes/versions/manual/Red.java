package opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import logic.Team;
import opmodes.GroupConstants;
import opmodes.ManualOpMode;

@TeleOp(
        name = GroupConstants.MAIN_MODES_GROUP + ": Normal Manual - RED Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class Red extends ManualOpMode {
    public Red() {
        super(Team.RED, false);
    }
}
