package opmodes.debug;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import logic.Team;

import opmodes.GroupConstants;
import opmodes.OpModeBase;

import pedropathing.RobotLocalizer;

import utils.TelemetryHandler;

@TeleOp(
        name = GroupConstants.DEBUGGER_MODES_GROUP + ": RobotLocalizer",
        group = GroupConstants.DEBUGGER_MODES_GROUP)
public class RobotLocalizerDebugger extends OpModeBase {
    private RobotLocalizer localizer;

    public RobotLocalizerDebugger() {
        super(Team.RED, false, true);
    }

    @Override
    public void runOpMode() {
        initialize();

        localizer = new RobotLocalizer(robotPosition);

        waitForStart();

        runStart();

        while (opModeIsActive()) {
            robotPosition.updatePose();

            TelemetryHandler.addData("Pose", robotPosition.getPose().toString());
            TelemetryHandler.addData("Localizer Pose", localizer.getPose().toString());
            TelemetryHandler.addData("Localizer Velocity", localizer.getVelocity().toString());
            TelemetryHandler.addData("Pose Velocity", robotPosition.getPoseVelocity().toString());
            TelemetryHandler.update();
        }

        runStop();
    }
}
