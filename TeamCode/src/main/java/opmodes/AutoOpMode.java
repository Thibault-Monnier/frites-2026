package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;

import logic.PlayingField;
import logic.RobotPosition;
import logic.Team;

import modules.HardwareConstants;
import modules.actuator.Cannon;
import modules.actuator.CannonBuffer;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import roadrunner.MecanumDrive;

@Autonomous(name = "Auto Mode")
public class AutoOpMode extends LinearOpMode {

    private Telemetry globalTelemetry;
    private RobotPosition robotPosition;

    private Cannon cannon;
    private CannonBuffer bufferLeft, bufferRight;

    // FTC requires a no-arg constructor
    private final Team team = Team.RED;
    private final RobotPosition.StartPosition startPosition = RobotPosition.StartPosition.NORMAL;

    public AutoOpMode() {
    }

    @Override
    public void runOpMode() {
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // --- Position tracking ---
        robotPosition = RobotPosition.getInstance(globalTelemetry, hardwareMap, team);

        // --- IMU ---
        IMU imu = hardwareMap.get(IMU.class, HardwareConstants.IMU_ID);

        // --- Cannon ---
        cannon =
                new Cannon(
                        globalTelemetry,
                        hardwareMap.get(
                                com.qualcomm.robotcore.hardware.DcMotorEx.class,
                                HardwareConstants.CANNON_MOTOR_LEFT_ID),
                        hardwareMap.get(
                                com.qualcomm.robotcore.hardware.DcMotorEx.class,
                                HardwareConstants.CANNON_MOTOR_RIGHT_ID));

        bufferLeft =
                new CannonBuffer(
                        globalTelemetry,
                        hardwareMap.get(
                                com.qualcomm.robotcore.hardware.CRServo.class,
                                HardwareConstants.CANNON_BUFFER_LEFT),
                        com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE);

        bufferRight =
                new CannonBuffer(
                        globalTelemetry,
                        hardwareMap.get(
                                com.qualcomm.robotcore.hardware.CRServo.class,
                                HardwareConstants.CANNON_BUFFER_RIGHT),
                        com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD);

        telemetry.addLine("Ready");
        telemetry.update();

        waitForStart();

        // ---------------- AFTER START ----------------

        Pose2d startPose = robotPosition.getPose().toPose2d(DistanceUnit.METER, AngleUnit.RADIANS);

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        for (int i = 0; i < 4 && opModeIsActive(); i++) {
            Action spin = drive.actionBuilder(drive.localizer.getPose()).turn(Math.PI / 2).build();

            Actions.runBlocking(spin);
        }
    }
}
