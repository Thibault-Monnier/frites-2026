package org.firstinspires.ftc.teamcode.auto.versions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.auto.MainAutoOpMode;
import org.firstinspires.ftc.teamcode.robot.Constants;

@Autonomous(name = Constants.MAIN_MODES_GROUP + ": Blue Normal", group = Constants.MAIN_MODES_GROUP)
public class Blue_Normal extends MainAutoOpMode {

    public Blue_Normal() {
        super(Constants.Team.BLUE, Constants.StartPosition.NORMAL);
    }
}
