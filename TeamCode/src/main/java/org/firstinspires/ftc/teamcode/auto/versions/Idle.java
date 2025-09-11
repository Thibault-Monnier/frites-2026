package org.firstinspires.ftc.teamcode.auto.versions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.auto.MainAutoOpMode;
import org.firstinspires.ftc.teamcode.robot.Constants;

@Autonomous(name = Constants.MAIN_MODES_GROUP + ": Idle", group = Constants.MAIN_MODES_GROUP)
public class Idle extends MainAutoOpMode {

    public Idle() {
        super(Constants.Team.ANY_OR_UNKNOWN, Constants.StartPosition.ANY_OR_UNKNOWN);
    }
}
