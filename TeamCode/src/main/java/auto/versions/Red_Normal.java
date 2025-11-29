package auto.versions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import auto.MainAutoOpMode;
import robot.Constants;

@Autonomous(name = Constants.MAIN_MODES_GROUP + ": Red Normal", group = Constants.MAIN_MODES_GROUP)
public class Red_Normal extends MainAutoOpMode {
    public Red_Normal() {
        super(Constants.Team.RED, Constants.StartPosition.NORMAL);
    }
}
