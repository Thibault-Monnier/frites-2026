package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import logic.ArtifactSequence;
import logic.PlayingField;
import logic.RobotPosition;
import logic.Team;

import math.Distance;

import modules.HardwareConstants;
import modules.actuator.Cannon;
import modules.actuator.CannonBuffer;
import modules.actuator.CannonBuffersHandler;
import modules.actuator.Intake;
import modules.actuator.IntakeSwitcher;
import modules.actuator.Movement;
import modules.sensor.BatteryMonitor;
import modules.sensor.GamepadController;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class ManualOpMode extends LinearOpMode {
    private final Team team;
    private final boolean calculatePose;
    private final boolean isAfterAuto;

    private ElapsedTime runtime;
    private Telemetry globalTelemetry;

    private RobotPosition robotPosition;

    private BatteryMonitor batteryMonitor;

    private GamepadController gamepad;
    private Movement move;

    private Cannon cannon;
    private CannonBuffersHandler cannonBuffers;

    private Intake intake;
    private IntakeSwitcher intakeSwitcher;

    private ArtifactSequence artifactSequence;

    public ManualOpMode(Team team, boolean isAfterAuto, boolean calculatePose) {
        this.team = team;
        this.calculatePose = calculatePose;
        this.isAfterAuto = isAfterAuto;
    }

    public ManualOpMode(Team team, boolean isAfterAuto) {
        this(team, isAfterAuto, true);
    }

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        runtime.reset();

        double prevTime = runtime.milliseconds();
        while (opModeIsActive()) {
            // Consistent step duration for better PIDs
            double time = runtime.milliseconds();
            while (time - prevTime < 100) {
                time = runtime.milliseconds();
            }
            prevTime = time;

            runStep();
        }
    }

    private void initialize() {
        runtime = new ElapsedTime();
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        if (calculatePose)
            robotPosition =
                    RobotPosition.getInstance(globalTelemetry, hardwareMap, team, !isAfterAuto);

        batteryMonitor = new BatteryMonitor(hardwareMap, globalTelemetry);

        gamepad = new GamepadController(runtime, gamepad1);

        DcMotor moveFL = hardwareMap.get(DcMotor.class, HardwareConstants.FRONT_LEFT_MOTOR_ID);
        DcMotor moveFR = hardwareMap.get(DcMotor.class, HardwareConstants.FRONT_RIGHT_MOTOR_ID);
        DcMotor moveBL = hardwareMap.get(DcMotor.class, HardwareConstants.BACK_LEFT_MOTOR_ID);
        DcMotor moveBR = hardwareMap.get(DcMotor.class, HardwareConstants.BACK_RIGHT_MOTOR_ID);
        DcMotorEx cannonLeft =
                hardwareMap.get(DcMotorEx.class, HardwareConstants.CANNON_MOTOR_LEFT_ID);
        DcMotorEx cannonRight =
                hardwareMap.get(DcMotorEx.class, HardwareConstants.CANNON_MOTOR_RIGHT_ID);
        CRServo cannonBufferLeft =
                hardwareMap.get(CRServo.class, HardwareConstants.CANNON_BUFFER_LEFT);
        CRServo cannonBufferRight =
                hardwareMap.get(CRServo.class, HardwareConstants.CANNON_BUFFER_RIGHT);
        DcMotor intake = hardwareMap.get(DcMotor.class, HardwareConstants.INTAKE_MOTOR_ID);
        Servo intakeSwitcher =
                hardwareMap.get(Servo.class, HardwareConstants.INTAKE_SWITCHER_SERVO);

        IMU onBoardIMU = hardwareMap.get(IMU.class, HardwareConstants.IMU_ID);
        Movement.MovementMode movementMode =
                calculatePose
                        ? Movement.MovementMode.FIELD_CENTRIC
                        : Movement.MovementMode.ROBOT_CENTRIC;
        move =
                new Movement(
                        globalTelemetry, moveFL, moveFR, moveBL, moveBR, movementMode, onBoardIMU);

        cannon = new Cannon(globalTelemetry, cannonLeft, cannonRight);

        CannonBuffer leftBuffer =
                new CannonBuffer(
                        globalTelemetry, cannonBufferLeft, DcMotorSimple.Direction.REVERSE);
        CannonBuffer rightBuffer =
                new CannonBuffer(
                        globalTelemetry, cannonBufferRight, DcMotorSimple.Direction.FORWARD);
        this.cannonBuffers = new CannonBuffersHandler(leftBuffer, rightBuffer);

        this.intake = new Intake(globalTelemetry, intake);
        this.intakeSwitcher = new IntakeSwitcher(globalTelemetry, intakeSwitcher);
    }

    private void runStep() {
        resetNeeded();
        update();

        executeActions();

        globalTelemetry.addData("Team", team);
        batteryMonitor.log();

        apply();

        globalTelemetry.update();
    }

    private void resetNeeded() {
        move.reset();
    }

    private void update() {
        gamepad.update();
        if (calculatePose) robotPosition.updatePose();

        if (calculatePose) {
            if (artifactSequence == null) {
                artifactSequence =
                        ArtifactSequence.findCurrentSequence(robotPosition.getLimelightHandler());
            }
            if (artifactSequence != null) {
                // Show the current artifactSequence
                globalTelemetry.addData("Pattern", artifactSequence.toString());
            }
        }

        Distance targetDistance = new Distance(DistanceUnit.CM, 200); // Default distance
        if (calculatePose)
            targetDistance = PlayingField.distanceToGoal(robotPosition.getPosition(), team);
        globalTelemetry.addData("Target Dist", targetDistance.toString());
        cannon.update(targetDistance);
    }

    private void executeActions() {
        if (calculatePose && gamepad.isPressing(GamepadController.Button.BUMPER_LEFT)) {
            // Lock towards the goal
            move.lockedJoystickTranslate(
                    gamepad1,
                    gamepad.isPressing(GamepadController.Button.LEFT_STICK),
                    robotPosition,
                    team,
                    PlayingField.goalPos(team));
        } else {
            move.joystickTranslate(
                    gamepad1,
                    gamepad.isPressing(GamepadController.Button.LEFT_STICK),
                    robotPosition,
                    team);
            move.joystickRotate(gamepad1, gamepad.isPressing(GamepadController.Button.RIGHT_STICK));
        }

        if (gamepad.isPressed(GamepadController.Button.X)) cannon.toggle();

        // LED indication for cannon readiness
        double r = cannon.isReadyToShoot() ? 0.0 : 1.0;
        gamepad.gamepad.setLedColor(r, 1.0 - r, 0.0, Gamepad.LED_DURATION_CONTINUOUS);

        if (gamepad.isPressing(GamepadController.Button.TRIGGER_LEFT)) {
            intake.on();
            cannonBuffers.clear();
        } else {
            intake.off();
        }

        // Make sure the cannon reached its target velocity
        if ((gamepad.isPressing(GamepadController.Button.TRIGGER_RIGHT) && cannon.isReadyToShoot())
                || gamepad.isPressing(GamepadController.Button.BUMPER_RIGHT)) {
            cannonBuffers.shootContinue(
                    intakeSwitcher.getCurrentPosition() == IntakeSwitcher.Position.RIGHT);
            intake.on();
        } else {
            cannonBuffers.shootDontContinue();

            if (gamepad.isPressing(GamepadController.Button.TRIGGER_RIGHT))
                gamepad.rumble(50); // Cannon isn't ready
            else cannonBuffers.shootReset();
        }

        if (gamepad.isPressed(GamepadController.Button.Y)) intakeSwitcher.toggle();
        else if (gamepad.isPressed(GamepadController.Button.DPAD_DOWN)) intakeSwitcher.center();

        if (gamepad.isPressing(GamepadController.Button.A)) {
            intake.clear();
            cannonBuffers.clear();
        }

        if (gamepad.isDoublePressed(GamepadController.Button.B)) move.toggleSuperSlow();
        if (gamepad.isLongPressed(GamepadController.Button.B) && calculatePose)
            robotPosition.resetPose();
    }

    private void apply() {
        move.apply();

        intake.apply();
        intakeSwitcher.apply();

        cannon.apply();
        cannonBuffers.apply();
    }
}
