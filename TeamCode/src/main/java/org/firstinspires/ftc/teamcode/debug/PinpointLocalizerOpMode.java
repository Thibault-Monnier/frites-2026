package org.firstinspires.ftc.teamcode.debug;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.roadrunner.PinpointLocalizer;
import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(name = "PinpointLocalizer OpMode", group = "concept")
public class PinpointLocalizerOpMode extends LinearOpMode {
    Telemetry telemetry;

    IMU imu;
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
        imu = hardwareMap.get(IMU.class, Constants.IMU_ID);

        localizer = new PinpointLocalizer(hardwareMap, 0.00198, new Pose2d(0, 0, 0));
    }

    public void tick() {
        Pose2d pose = localizer.getPose();

        telemetry.addData("X (mm)", pose.position.x);
        telemetry.addData("Y (mm)", pose.position.y);

        telemetry.addData("Heading (deg)", Math.toDegrees(pose.heading.real));
        telemetry.update();
    }
}
