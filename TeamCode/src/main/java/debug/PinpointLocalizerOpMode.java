package debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import roadrunner.PinpointLocalizer;

@TeleOp(name = "PinpointLocalizer OpMode", group = "concept")
public class PinpointLocalizerOpMode extends LinearOpMode {
    Telemetry globalTelemetry;
    PinpointLocalizer localizer;

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        while (opModeIsActive()) {
            tick();
        }
    }

    public void initialize() {
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        localizer = new PinpointLocalizer(hardwareMap, 0.00198, new Pose2d(0, 0, 0));
    }

    public void tick() {
        localizer.update();

        Pose2d pose = localizer.getPose();


        globalTelemetry.addData("X (mm)", pose.position.x);
        globalTelemetry.addData("Y (mm)", pose.position.y);

        globalTelemetry.addData("Heading (deg)", Math.toDegrees(pose.heading.real));
        globalTelemetry.update();
    }
}
