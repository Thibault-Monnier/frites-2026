package core.opmodes.versions.manual;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import core.opmodes.ManualOpMode;
import core.modules.Constants;

@TeleOp(name = Constants.MAIN_MODES_GROUP + ": Main - RED Team", group = Constants.MAIN_MODES_GROUP)
public class Red extends ManualOpMode {
    public Red() {
        super(Constants.Team.RED, false);
    }
}
