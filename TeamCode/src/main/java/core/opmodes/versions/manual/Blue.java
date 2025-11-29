package core.opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import core.modules.Constants;
import core.opmodes.ManualOpMode;

@TeleOp(
        name = Constants.MAIN_MODES_GROUP + ": Main - BLUE Team",
        group = Constants.MAIN_MODES_GROUP)
public class Blue extends ManualOpMode {
    public Blue() {
        super(Constants.Team.BLUE, false);
    }
}
