package org.firstinspires.ftc.teamcode.debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.roadrunner.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(name = "Odometry OpMode", group = "concept")
public class OdometryOpMode extends LinearOpMode {
    Telemetry globalTelemetry;

    GoBildaPinpointDriver pinpoint;

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

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        // TODO: Set this according to their physical placement on the robot
        pinpoint.setOffsets(0, 0);

        pinpoint.resetPosAndIMU();
    }

    public void tick() {
        pinpoint.update();

        double Xpos = pinpoint.getPosX();
        double Ypos = pinpoint.getPosY();

        globalTelemetry.addData("X (mm)", Xpos);
        globalTelemetry.addData("Y (mm)", Ypos);
        globalTelemetry.update();
    }
}
