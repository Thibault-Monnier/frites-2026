package core.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import core.localization.RobotPosition;
import core.logic.Movement;
import core.logic.PlayingField;
import core.logic.Replayer;
import core.math.Distance;
import core.modules.Cannon;
import core.modules.CannonBuffer;
import core.Constants;
import core.modules.GamepadController;
import core.modules.Intake;
import core.modules.IntakeSwitcher;
import core.modules.RobotModule;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class ManualOpMode extends LinearOpMode {
    private final Constants.Team team;
    private final boolean replay;
    private final boolean calculatePose;
    private ElapsedTime runtime;
    private Telemetry globalTelemetry;

    private RobotPosition robotPosition;
    private final PlayingField playingField;

    private GamepadController gamepad;
    private Movement move;

    // Modules
    private Cannon cannon;

    private CannonBuffer cannonBufferLeft;
    private CannonBuffer cannonBufferRight;

    private Intake intake;

    private IntakeSwitcher intakeSwitcher;

    private Replayer.Logger replaySaver;

    public ManualOpMode(Constants.Team team, boolean replay, boolean calculatePose) {
        this.team = team;
        this.replay = replay;
        this.calculatePose = calculatePose;
        this.playingField = new PlayingField();
    }

    public ManualOpMode(Constants.Team team, boolean replay) {
        this(team, replay, true);
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

        if (calculatePose) robotPosition = RobotPosition.getInstance(globalTelemetry, hardwareMap);

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

        cannon =
                new Cannon(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, Constants.CANNON_MOTOR_2_ID));

        cannonBufferLeft = new CannonBuffer(
                globalTelemetry, hardwareMap.get(CRServo.class, Constants.CANNON_BUFFER_LEFT), DcMotorSimple.Direction.REVERSE
        );
        cannonBufferRight = new CannonBuffer(
                globalTelemetry, hardwareMap.get(CRServo.class, Constants.CANNON_BUFFER_RIGHT), DcMotorSimple.Direction.FORWARD);

        intake =
                new Intake(
                        globalTelemetry, hardwareMap.get(DcMotor.class, Constants.INTAKE_MOTOR_ID));

        intakeSwitcher = new IntakeSwitcher(
                globalTelemetry, hardwareMap.get(Servo.class, Constants.INTAKE_SWITCHER_SERVO)
        );

        if (replay) {
            replaySaver = new Replayer.Logger(runtime, new RobotModule[] {move});
        }
    }

    public void runStep() {
        move.reset();
        gamepad.update();
        if (calculatePose) robotPosition.updatePose();

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
        Distance targetDistance = new Distance(DistanceUnit.CM, 130); // Default distance
        if (calculatePose)
            targetDistance = playingField.distanceToGoal(robotPosition.getPosition(), team);
        cannon.update(targetDistance);

        if (gamepad.isPressed(GamepadController.Button.X)) cannon.toggle();
        if (gamepad.isPressing(GamepadController.Button.DPAD_LEFT)) {
            cannonBufferLeft.on();
        } else {
            cannonBufferLeft.off();
        }
        if (gamepad.isPressing(GamepadController.Button.DPAD_RIGHT)) {
            cannonBufferRight.on();
        } else {
            cannonBufferRight.off();
        }

        if (gamepad.isPressed(GamepadController.Button.A)) intake.toggle();

        if (gamepad.isPressed(GamepadController.Button.DPAD_UP)) intakeSwitcher.toggle();
        if (gamepad.isPressed(GamepadController.Button.DPAD_DOWN)) intakeSwitcher.center();


        /* --- OPMODE TELEMETRY --- */
        globalTelemetry.addLine("--- MANUAL MODE ---");
        globalTelemetry.addData("Team", team);

        /* --- APPLY --- */
        move.apply();
        cannon.apply();
        intake.apply();

        cannonBufferRight.apply();
        cannonBufferLeft.apply();

        intakeSwitcher.apply();

        globalTelemetry.update();

        /* --- REPLAY --- */
        if (replay) {
            replaySaver.logCurrentState();
        }
    }
}
