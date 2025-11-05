package org.firstinspires.ftc.teamcode.manual;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.models.RobotModule;
import org.firstinspires.ftc.teamcode.robot.Cannon;
import org.firstinspires.ftc.teamcode.robot.Constants;
import org.firstinspires.ftc.teamcode.robot.Movement;
import org.firstinspires.ftc.teamcode.robot.Replayer;

@Config
public class ManualOpMode extends OpMode {
    private final Constants.Team team;
    private final boolean replay;
    private ElapsedTime runtime;
    private GamepadController gamepad;
    private Movement move;
    private Telemetry globalTelemetry;

    // Modules
    private Cannon cannon1;
    private Cannon cannon2;

    private Replayer.Logger replaySaver;

    public ManualOpMode(Constants.Team team, boolean replay) {
        this.team = team;
        this.replay = replay;
    }

    @Override
    public void init() {
        runtime = new ElapsedTime();

        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

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

        cannon1 =
                new Cannon(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, Constants.CANNON_MOTOR_1_ID));
        cannon2 =
                new Cannon(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, Constants.CANNON_MOTOR_2_ID));

        if (replay) {
            replaySaver = new Replayer.Logger(runtime, new RobotModule[] {move});
        }
    }

    @Override
    public void init_loop() {
        runtime.reset();
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {
        /* --- MOVEMENT --- */
        move.reset();

        /*
        if (gamepad.press(GamepadController.Button.LEFT_STICK)) {
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
        if (gamepad.isPressed(GamepadController.Button.B)) cannon1.toggle();
        if (gamepad.isPressed(GamepadController.Button.X)) cannon2.toggle();
        cannon1.update();
        cannon2.update();

        /* --- OPMODE TELEMETRY --- */
        globalTelemetry.addLine("--- MANUAL MODE ---");
        globalTelemetry.addData("Team", team);

        /* --- APPLY --- */
        move.apply();
        cannon1.apply();
        cannon2.apply();

        globalTelemetry.update();

        /* --- REPLAY --- */
        if (replay) {
            replaySaver.logCurrentState();
        }
    }

    @Override
    public void stop() {
        if (replay) {
            replaySaver.saveAndExit(Constants.REPLAY_FILE_DEST);
        }
    }
}
