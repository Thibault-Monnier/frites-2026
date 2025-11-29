package debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import field.LimelightHandler;

@TeleOp(name = "Camera Localizer OpMode", group = "Concept")
public class CameraLocalizerOpMode extends LinearOpMode {
    private Telemetry globalTelemetry;

    private LimelightHandler limelightHandler;

    public CameraLocalizerOpMode() {}

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        limelightHandler.start();

        while (opModeIsActive()) {
            if (limelightHandler.update()) {
                Pose2D pose = limelightHandler.getLastKnownPose();
                globalTelemetry.addLine("--- Last Known Pose: ---");
                globalTelemetry.addData("X (mm)", pose.getX(DistanceUnit.MM));
                globalTelemetry.addData("Y (mm)", pose.getY(DistanceUnit.MM));
                globalTelemetry.addData("Yaw (deg)", pose.getHeading(AngleUnit.DEGREES));
            }

            globalTelemetry.update();
        }

        limelightHandler.stop();
    }

    private void initialize() {
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        limelightHandler = LimelightHandler.getInstance(globalTelemetry, hardwareMap);
        limelightHandler.init();
    }
}
