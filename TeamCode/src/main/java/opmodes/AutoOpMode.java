package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import logic.Team;

public class AutoOpMode extends OpModeBase {
    // private final Deque<Action> actionSequence = new ArrayDeque<>();

    public AutoOpMode(Team team) {
        super(team, true, true);
    }

    @Override
    public void runOpMode() {
        initialize();
        // initSequence();

        waitForStart();

        runtime.reset();

        double prevTime = runtime.milliseconds();
        while (opModeIsActive() /*&& !actionSequence.isEmpty()*/) {
            // Consistent step duration for better PIDs
            double time = runtime.milliseconds();
            while (time - prevTime < 100) {
                time = runtime.milliseconds();
            }
            prevTime = time;

            runStep();
        }
    }

    /*private void initSequence() {
        registerAction(powerOnCannon());
        registerAction(intakeSwitcherRight());

        shootSequence();

        collectArtifactRowSequence(Artifact.Row.BACK);
        shootSequence();

        registerAction(driveActions.driveToArtifactRowEntryPose(Artifact.Row.MIDDLE));
        registerAction(turnTowardsArtifactRow(Artifact.Row.MIDDLE));

        // collectArtifactRowSequence(Artifact.Row.MIDDLE);
        // shootSequence();

        // registerAction(driveActions.driveToLeavePose());
        // registerAction(turnTowardsLeavePose());
    }

    private void shootSequence() {
        registerAction(driveActions.driveToGoalShootPosition());
        registerAction(turnTowardsGoal());

        registerAction(prepareToShoot());
        registerAction(intakeOn());
        registerAction(shoot());
        registerAction(intakeOff());
    }

    private void collectArtifactRowSequence(Artifact.Row row) {
        registerAction(driveActions.driveToArtifactRowEntryPose(row));
        registerAction(turnTowardsArtifactRow(row));

        registerAction(intakeOn());
        registerAction(driveActions.collectArtifactsFromRow(row));
        registerAction(intakeOff());
        registerAction(driveActions.driveBackToArtifactRowEntryPose(row));
    }

    private Action powerOnCannon() {
        return new SimpleAction(() -> cannon.on());
    }

    private Action prepareToShoot() {
        return new SimpleAction(() -> cannonBuffers.shootReset());
    }

    private Action shoot() {
        return telemetryPacket ->
                !cannonBuffers.shootContinue(
                        intakeSwitcher.getCurrentPosition() == IntakeSwitcher.Position.RIGHT);
    }

    private Action turnTowardsGoal() {
        return telemetryPacket -> move.turnTowards(robotPosition, PlayingField.goalPos(team));
    }

    private Action turnTowardsArtifactRow(Artifact.Row row) {
        return telemetryPacket ->
                move.turnTowardsHeading(
                        robotPosition, PlayingField.artifactRowEntryPose(team, row).getHeading());
    }

    private Action turnTowardsLeavePose() {
        return telemetryPacket ->
                move.turnTowardsHeading(
                        robotPosition, PlayingField.autoModeLeavePose(team).getHeading());
    }

    private Action intakeOn() {
        return new SimpleAction(
                () -> {
                    intake.on();
                    cannonBuffers.reverse();
                });
    }

    private Action intakeOff() {
        return new SimpleAction(
                () -> {
                    intake.off();
                    cannonBuffers.off();
                });
    }

    private Action intakeSwitcherRight() {
        return new SimpleAction(() -> intakeSwitcher.right());
    }

    private Action intakeSwitcherLeft() {
        return new SimpleAction(() -> intakeSwitcher.left());
    }

    private Action intakeSwitcherCenter() {
        return new SimpleAction(() -> intakeSwitcher.center());
    }

    private void registerAction(Action action) {
        actionSequence.addLast(action);
    }*/

    private void runStep() {
        update();

        TelemetryPacket packet = new TelemetryPacket();

        /*Action currentAction = actionSequence.getFirst();

        currentAction.preview(packet.fieldOverlay());

        if (!currentAction.run(packet)) {
            actionSequence.removeFirst();
        }*/

        apply();

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
        log();
    }
}
