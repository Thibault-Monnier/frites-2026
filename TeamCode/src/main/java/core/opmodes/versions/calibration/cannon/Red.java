package core.opmodes.versions.calibration.cannon;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import core.Constants;
import core.opmodes.CannonCalibrationOpMode;

@TeleOp(
        name = Constants.CALIBRATION_MODES_GROUP + ": Cannon Calibration - RED Team",
        group = Constants.CALIBRATION_MODES_GROUP)
public class Red extends CannonCalibrationOpMode {
    public Red() {
        super(Constants.Team.RED);
    }
}
