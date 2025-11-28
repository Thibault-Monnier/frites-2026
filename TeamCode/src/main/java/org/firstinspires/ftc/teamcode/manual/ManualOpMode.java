package org.firstinspires.ftc.teamcode.manual;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.field.PlayingField;
import org.firstinspires.ftc.teamcode.field.RobotPosition;
import org.firstinspires.ftc.teamcode.models.RobotModule;
import org.firstinspires.ftc.teamcode.robot.Cannon;
import org.firstinspires.ftc.teamcode.robot.CannonBuffer;
import org.firstinspires.ftc.teamcode.robot.Constants;
import org.firstinspires.ftc.teamcode.robot.Intake;
import org.firstinspires.ftc.teamcode.robot.Movement;
import org.firstinspires.ftc.teamcode.robot.Replayer;

@Config
public class ManualOpMode extends LinearOpMode {
    private final Constants.Team team;
    private final boolean replay;
    private ElapsedTime runtime;
    private Telemetry globalTelemetry;

    private RobotPosition robotPosition;
    private final PlayingField playingField;

    private GamepadController gamepad;
    private Movement move;

    // Modules
    private Cannon cannon;

    private CannonBuffer cannonBuffer;

    private Intake intake;

    private Replayer.Logger replaySaver;

    public ManualOpMode(Constants.Team team, boolean replay) {
        this.team = team;
        this.replay = replay;
        this.playingField = new PlayingField();
    }

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            runStep();
        }

        if (replay) {
            replaySaver.saveAndExit(Constants.REPLAY_FILE_DEST);
        }
    }

    public void initialize() {
        runtime = new ElapsedTime();
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        robotPosition = new RobotPosition(globalTelemetry, hardwareMap);

        gamepad = new GamepadController(runtime, gamepad1);

        IMU onBoardIMU = hardwareMap.get(IMU.class, Constants.IMU_ID);
        move =
                new Movement(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, Constants.FRONT_LEFT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, Constants.FRONT_RIGHT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, Constants.BACK_LEFT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, Constants.BACK_RIGHT_MOTOR_ID),
                        onBoardIMU);

        cannon = new Cannon(
                globalTelemetry,
                hardwareMap.get(DcMotor.class, Constants.CANNON_MOTOR_2_ID)
        );

        cannonBuffer = new CannonBuffer(
                globalTelemetry,
                hardwareMap.get(Servo.class, Constants.CANNON_BUFFER)
        );

        intake =
                new Intake(
                        globalTelemetry, hardwareMap.get(DcMotor.class, Constants.INTAKE_MOTOR_ID));

        if (replay) {
            replaySaver = new Replayer.Logger(runtime, new RobotModule[]{move});
        }
    }

    public void runStep() {
        move.reset();
        gamepad.update();
        robotPosition.updatePose();

        /*
        if (gamepad.isPressed(GamepadController.Button.LEFT_STICK)) {
            if (move.getMovementMode() == Movement.MovementMode.ROBOT_CENTRIC) {
                move.setMovementMode(Movement.MovementMode.FIELD_CENTRIC);
            } else {
                move.setMovementMode(Movement.MovementMode.ROBOT_CENTRIC);
            }
        }
        */

        // Translation : unpressed (fast) and pressed (slow)
        move.joystickTranslate(gamepad1, gamepad.isPressing(GamepadController.Button.LEFT_STICK));

        // Rotation : bumpers (fast) and triggers (slow)
        move.bumperTurn(gamepad1);

        /* --- ACTIONS --- */
        if (gamepad.isPressed(GamepadController.Button.X)) cannon.toggle();
        double targetDistance = playingField.distanceToGoal(robotPosition.get2dPosition(), team);
        cannon.update(targetDistance);

        if (gamepad.isPressed(GamepadController.Button.B)) cannonBuffer.toggle();

        if (gamepad.isPressed(GamepadController.Button.A)) intake.toggle();

        /* --- OPMODE TELEMETRY --- */
        globalTelemetry.addLine("--- MANUAL MODE ---");
        globalTelemetry.addData("Team", team);

        /* --- APPLY --- */
        move.apply();
        cannon.apply();
        intake.apply();

        globalTelemetry.update();

        /* --- REPLAY --- */
        if (replay) {
            replaySaver.logCurrentState();
        }
    }
}
