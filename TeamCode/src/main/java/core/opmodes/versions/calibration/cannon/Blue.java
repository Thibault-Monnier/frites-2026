package core.opmodes.versions.calibration.cannon;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import core.logic.Team;
import core.opmodes.CannonCalibrationOpMode;
import core.opmodes.GroupConstants;

@TeleOp(
        name = GroupConstants.CALIBRATION_MODES_GROUP + ": Cannon Calibration - BLUE Team",
        group = GroupConstants.CALIBRATION_MODES_GROUP)
public class Blue extends CannonCalibrationOpMode {
    public Blue() {
        super(Team.BLUE);
    }
}
