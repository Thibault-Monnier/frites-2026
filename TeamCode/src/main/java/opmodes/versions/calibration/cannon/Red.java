package opmodes.versions.calibration.cannon;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import logic.Team;
import opmodes.CannonCalibrationOpMode;
import opmodes.GroupConstants;

@TeleOp(
        name = GroupConstants.CALIBRATION_MODES_GROUP + ": Cannon Calibration - RED Team",
        group = GroupConstants.CALIBRATION_MODES_GROUP)
public class Red extends CannonCalibrationOpMode {
    public Red() {
        super(Team.RED);
    }
}
