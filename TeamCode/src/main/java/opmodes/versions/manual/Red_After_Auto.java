package opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import logic.Team;

import opmodes.GroupConstants;
import opmodes.ManualOpMode;

@TeleOp(
        name = GroupConstants.MAIN_MODES_GROUP + ": Normal Manual After Auto - RED Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class Red_After_Auto extends ManualOpMode {
    public Red_After_Auto() {
        super(Team.RED, true);
    }
}
