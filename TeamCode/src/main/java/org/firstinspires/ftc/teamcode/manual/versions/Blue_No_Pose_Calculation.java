package org.firstinspires.ftc.teamcode.manual.versions;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.manual.ManualOpMode;
import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(
        name = Constants.MAIN_MODES_GROUP + ": Main - BLUE Team | No Pose Calculation",
        group = Constants.MAIN_MODES_GROUP)
public class Blue_No_Pose_Calculation extends ManualOpMode {
    public Blue_No_Pose_Calculation() {
        super(Constants.Team.BLUE, false, false);
    }
}
