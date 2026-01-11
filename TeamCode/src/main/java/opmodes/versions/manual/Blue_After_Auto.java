package opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import logic.Team;

import opmodes.GroupConstants;
import opmodes.ManualOpMode;

@TeleOp(
        name = GroupConstants.MAIN_MODES_GROUP + ": Normal Manual After Auto - BLUE Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class Blue_After_Auto extends ManualOpMode {
    public Blue_After_Auto() {
        super(Team.BLUE, true);
    }
}
