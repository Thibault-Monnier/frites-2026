package core.opmodes.versions.calibration.cannon;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import core.Constants;
import core.opmodes.CannonCalibrationOpMode;

@TeleOp(
        name = Constants.CALIBRATION_MODES_GROUP + ": Cannon Calibration - BLUE Team",
        group = Constants.CALIBRATION_MODES_GROUP)
public class Blue extends CannonCalibrationOpMode {
    public Blue() {
        super(Constants.Team.BLUE);
    }
}
