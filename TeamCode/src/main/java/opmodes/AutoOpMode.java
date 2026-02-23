package opmodes;


import logic.Team;
import logic.action.Action;
import logic.action.ActionSequence;
import logic.action.SimpleAction;
import logic.field.Artifact;
import logic.field.PlayingField;

public class AutoOpMode extends OpModeBase {
    private final ActionSequence actionSequence;

    public AutoOpMode(Team team) {
        super(team, true);
        actionSequence = new ActionSequence();
    }

    @Override
    public void runOpMode() {
        initialize();
        initSequence();

        waitForStart();

        runStart();

        double prevTime = runtime.milliseconds();
        while (opModeIsActive() /*&& !actionSequence.isEmpty()*/) {
            // Consistent step duration for better PIDs
            double time = runtime.milliseconds();
            globalTelemetry.addData("Delta time", time - prevTime);
            while (time - prevTime < 35) {
                time = runtime.milliseconds();
            }
            prevTime = time;

            runStep();
        }
    }

    private void initSequence() {
        registerAction(powerOnCannon());

        shootSequence();

        collectArtifactRowSequence(Artifact.Row.BACK);
        shootSequence();

        registerAction(driveActions.driveToArtifactRowEntryPose(Artifact.Row.MIDDLE));
        registerAction(turnTowardsArtifactRow(Artifact.Row.MIDDLE));

        collectArtifactRowSequence(Artifact.Row.MIDDLE);
        shootSequence();

        registerAction(driveActions.driveToLeavePose());
        registerAction(turnTowardsLeavePose());
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
        return () -> cannonBuffers.shootContinue(true);
    }

    private Action turnTowardsGoal() {
        return () -> move.turnTowards(PlayingField.goalPos(team));
    }

    private Action turnTowardsArtifactRow(Artifact.Row row) {
        return () ->
                move.turnTowardsHeading(PlayingField.artifactRowEntryPose(team, row).getHeading());
    }

    private Action turnTowardsLeavePose() {
        return () -> move.turnTowardsHeading(PlayingField.autoModeLeavePose(team).getHeading());
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

    private void registerAction(Action action) {
        actionSequence.addAction(action);
    }

    private void runStep() {
        update();

        if (actionSequence.run()) {
            globalTelemetry.addLine("AUTO MODE COMPLETE");
        }

        apply();
        log();
    }
}
