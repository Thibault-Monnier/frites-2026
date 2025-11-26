package org.firstinspires.ftc.teamcode.debug;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.roadrunner.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(name = "Odometry OpMode", group = "concept")
public class OdometryOpMode extends LinearOpMode {
    Telemetry telemetry;

    IMU imu;
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
        imu = hardwareMap.get(IMU.class, Constants.IMU_ID);

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        // TODO: Set this according to their physical placement on the robot
        // inpoint.setOffsets(xOffsetMM, yOffsetMM);

        pinpoint.resetPosAndIMU();
    }

    public void tick() {
        double Xpos = pinpoint.getPosX();
        double Ypos = pinpoint.getPosY();

        telemetry.addData("X (mm)", Xpos);
        telemetry.addData("Y (mm)", Ypos);
        telemetry.update();
    }
}
