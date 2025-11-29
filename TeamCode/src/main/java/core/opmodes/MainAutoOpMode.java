package core.opmodes;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import core.logic.PlayingField;
import core.roadrunner.MecanumDrive;
import core.modules.Constants;
import core.logic.DriveActions;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "Main Auto Mode")
public class MainAutoOpMode extends LinearOpMode {
    private final Constants.Team team;
    private final Constants.StartPosition startPosition;
    private final FtcDashboard dash = FtcDashboard.getInstance();
    List<Action> runningActions = new ArrayList<>();

    private ElapsedTime runtime;
    private Telemetry globalTelemetry;
    private MecanumDrive drive;
    private DriveActions driveActions;

    private State previousState;
    private State currentState;
    private double stateStartTime;

    public MainAutoOpMode(Constants.Team team, Constants.StartPosition position) {
        this.team = team;
        this.startPosition = position;
    }

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        runtime.reset();

        while (opModeIsActive()) {
            runStep();
        }
    }

    public void initialize() {
        runtime = new ElapsedTime();

        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        IMU onBoardIMU = hardwareMap.get(IMU.class, Constants.IMU_ID);

        drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        driveActions = new DriveActions(drive);

        runtime.reset();

        if (startPosition == Constants.StartPosition.NORMAL) {
            drive.localizer.setPose(PlayingField.startPose(team));
        }
    }

    public void runStep() {
        TelemetryPacket packet = new TelemetryPacket();

        switch (currentState) {
            case TURN_TOWARDS_GOAL:
                registerAction(
                        new SequentialAction(
                                driveActions.turnTowardsGoal(drive.localizer.getPose(), team),
                                queueState(State.IDLE)));
                break;
            case IDLE:
                break;
        }

        List<Action> newActions = new ArrayList<>();
        for (Action action : runningActions) {
            action.preview(packet.fieldOverlay());
            if (action.run(packet)) {
                newActions.add(action);
            }
        }
        runningActions = newActions;

        dash.sendTelemetryPacket(packet);

        globalTelemetry.addLine("--- Main Auto Mode ---");
        globalTelemetry.addData("State", currentState);
        globalTelemetry.addData("State Start Time", stateStartTime);
        globalTelemetry.addData("Runtime", runtime.seconds());
    }

    private void setState(State state) {
        previousState = currentState;
        currentState = state;
        stateStartTime = runtime.seconds();
    }

    private void registerAction(Action action) {
        if (action != null) {
            runningActions.add(action);
        }
    }

    private Action queueState(State nextState) {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                setState(nextState);
                return true; // Indicate that the action is complete
            }

            @Override
            public void preview(@NonNull Canvas fieldOverlay) {
                // Optionally implement preview logic if needed
            }
        };
    }

    private enum OpModes {
        MAX_SPECIMENS,
    }

    public enum State {
        TURN_TOWARDS_GOAL,

        IDLE,
    }
}
