package org.firstinspires.ftc.teamcode.auto;

import static org.firstinspires.ftc.teamcode.auto.DriveActions.START_BASKET;
import static org.firstinspires.ftc.teamcode.auto.DriveActions.START_OBSERVATION_ZONE;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.robot.Constants;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "Main Auto Mode")
public class MainAutoOpMode extends OpMode {
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
    public void init() {
        runtime = new ElapsedTime();

        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        IMU onBoardIMU = hardwareMap.get(IMU.class, Constants.IMU_ID);

        drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        driveActions = new DriveActions(drive);

        runtime.reset();

        if (startPosition == Constants.StartPosition.OBSERVATION_ZONE) {
            drive.localizer.setPose(START_OBSERVATION_ZONE);
        } else if (startPosition == Constants.StartPosition.BASKET) {
            drive.localizer.setPose(START_BASKET);
        }
    }

    @Override
    public void loop() {
        TelemetryPacket packet = new TelemetryPacket();

        switch (currentState) {
            case START_TO_SUB:
                registerAction(
                        new SequentialAction(
                                driveActions.gotToSubmersible(drive.localizer.getPose()),
                                queueState(State.SUB_TO_OBS)));
                break;
            case SUB_TO_OBS:
                if (previousState != currentState) {
                    registerAction(
                            new SequentialAction(
                                    driveActions.goFromSubmersibleToObservation(false),
                                    queueState(State.OBS_TO_SUB)));
                }
                break;
            case OBS_TO_SUB:
                break;
            case SUB_TO_OBS_3_SAMPLE:
                break;

            case HANG_SPECIMEN:
                break;
            case TAKE_SPECIMEN:
                break;

            case WAIT:
                break;
            case IDLE:
                break;
            case ERROR:
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
        // Obs. = Observation
        // Sub. = Submersible

        // TRAJECTORIES
        START_TO_SUB,
        SUB_TO_OBS,
        OBS_TO_SUB,
        SUB_TO_OBS_3_SAMPLE,

        // ACTIONS
        HANG_SPECIMEN,
        TAKE_SPECIMEN,

        // OTHER
        WAIT,
        IDLE,
        ERROR
    }
}
