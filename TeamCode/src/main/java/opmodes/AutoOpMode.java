package opmodes;

import logic.Team;
import logic.action.Action;
import logic.action.ActionSequence;
import logic.action.SimpleAction;
import logic.field.Artifact;

public class AutoOpMode extends OpModeBase {
    private final ActionSequence actionSequence;

    public AutoOpMode(Team team) {
        super(team, true);
        actionSequence = new ActionSequence();
    }

    @Override
    public void runOpMode() {
        initialize();

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

    protected void initialize() {
        super.initialize();
        initSequence();
    }

    private void initSequence() {
        registerAction(powerOnCannon());

        shootSequence();

        collectArtifactRowSequence(Artifact.Row.BACK);
        shootSequence();

        collectArtifactRowSequence(Artifact.Row.MIDDLE);
        shootSequence();

        registerAction(driveActions.driveToLeavePose());
    }

    private void shootSequence() {
        registerAction(driveActions.driveToGoalShootPosition());

        registerAction(prepareToShoot());
        registerAction(intakeOn());
        registerAction(shoot());
        registerAction(intakeOff());
    }

    private void collectArtifactRowSequence(Artifact.Row row) {
        registerAction(driveActions.driveToArtifactRowEntryPose(row));

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
