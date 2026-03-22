package opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import logic.Team;
import logic.field.PlayingField;

public class AutoOpMode extends AutoOpModeBase {
    private int nbShots = 0;
    private double collectStartTime = 0;

    enum AutoState {
        START_TO_SHOOT,
        RAMP_TO_SHOOT,
        ALIGN_MIDDLE,
        COLLECT_MIDDLE,
        MIDDLE_ROW_TO_SHOOT,
        ALIGN_BACK,
        COLLECT_BACK,
        ALIGN_COLLECT_RAMP,
        COLLECT_RAMP,
        COLLECTING_FROM_RAMP,
        COLLECT_FROM_RAMP_FINAL,
        LEAVE,
        SHOOT,
        DONE;

        PathChain path;
    }

    private AutoState state = AutoState.START_TO_SHOOT;

    public AutoOpMode(Team team) {
        super(team, false);

        Paths paths = new Paths(follower, team.isBlue());
        paths.createPaths();
    }

    @Override
    protected Object getState() {
        return state;
    }

    protected void execute() {
        switch (state) {
            case START_TO_SHOOT:
            case RAMP_TO_SHOOT:
            case MIDDLE_ROW_TO_SHOOT:
            case LEAVE:
                intake();
                runPath(AutoState.SHOOT, false);
                break;

            case ALIGN_BACK:
                runPath(AutoState.COLLECT_BACK, false);
                break;

            case COLLECT_BACK:
                intake();
                runPath(AutoState.LEAVE, true);
                break;

            case ALIGN_MIDDLE:
                runPath(AutoState.COLLECT_MIDDLE, false);
                break;

            case COLLECT_MIDDLE:
                intake();
                runPath(AutoState.MIDDLE_ROW_TO_SHOOT, true);
                break;

            case ALIGN_COLLECT_RAMP:
                runPath(AutoState.COLLECT_RAMP, false);
                break;

            case COLLECT_RAMP:
                intake();
                runPath(AutoState.COLLECTING_FROM_RAMP, true);
                break;

            case COLLECTING_FROM_RAMP:
                intake();
                if (runtime.milliseconds() - collectStartTime > 650) {
                    state = AutoState.COLLECT_FROM_RAMP_FINAL;
                }
                break;

            case COLLECT_FROM_RAMP_FINAL:
                intake();
                runPath(AutoState.RAMP_TO_SHOOT, true);
                break;

            case SHOOT:
                runShootCycle();
                break;

            case DONE:
                break;
        }
    }

    private void runPath(AutoState nextState, boolean slow) {
        if (!followPath(state.path, slow)) state = nextState;

        if (nextState == AutoState.COLLECTING_FROM_RAMP) {
            collectStartTime = runtime.milliseconds();
        }
    }

    private void runShootCycle() {
        if (shoot()) {
            nbShots++;

            if (nbShots == 1) state = AutoState.ALIGN_MIDDLE;
            else if (nbShots == 2) state = AutoState.ALIGN_COLLECT_RAMP;
            else if (nbShots == 3) state = AutoState.ALIGN_COLLECT_RAMP;
            else if (nbShots == 4) state = AutoState.ALIGN_BACK;
            else state = AutoState.DONE;
        }
    }

    private static class Paths extends PathsBase {
        public Paths(Follower follower, boolean isBlue) {
            super(follower, isBlue);
        }

        @Override
        protected void createPaths() {
            Pose startPose = PlayingField.startPose(Team.RED).toPedropathingPose();
            Pose shootingPose = new Pose(88, 76, Math.toRadians(53));
            Pose leavePose = new Pose(85, 101, Math.toRadians(38));

            Pose middleRowStartPose = new Pose(99, 62, Math.toRadians(0));
            Pose middleRowControlPoint = new Pose(126, 63, Math.toRadians(0));
            Pose middleRowEndPose = new Pose(131, 56, Math.toRadians(0));
            Pose middleRowToShootControlPoint = new Pose(106.5, 59);

            Pose backRowStartPos = new Pose(100, 84, Math.toRadians(0));
            Pose backRowControlPoint = new Pose(106.5, 88);
            Pose backRowEndPose = new Pose(126, 83, Math.toRadians(-10));

            Pose alignRampPose = new Pose(126, 61, Math.toRadians(35));
            Pose alignRampControlPoint = new Pose(102, 66);
            Pose collectRampPose = new Pose(131.75, 59.5, Math.toRadians(28));
            Pose collectRampFinalControlPoint = new Pose(127, 57.5);
            Pose collectRampFinalEndPose = new Pose(131, 56.5, Math.toRadians(20));

            Pose rampToShootControlPoint = new Pose(107, 60);

            AutoState.START_TO_SHOOT.path = lineToPath(startPose, shootingPose);

            AutoState.ALIGN_BACK.path = lineToPath(shootingPose, backRowStartPos);
            AutoState.COLLECT_BACK.path =
                    curveToPath(backRowStartPos, backRowControlPoint, backRowEndPose);
            AutoState.LEAVE.path = lineToPath(backRowEndPose, leavePose);

            AutoState.ALIGN_MIDDLE.path = lineToPath(shootingPose, middleRowStartPose);
            AutoState.COLLECT_MIDDLE.path =
                    curveToPath(middleRowStartPose, middleRowControlPoint, middleRowEndPose);
            AutoState.MIDDLE_ROW_TO_SHOOT.path =
                    curveToPath(middleRowEndPose, middleRowToShootControlPoint, shootingPose);

            AutoState.ALIGN_COLLECT_RAMP.path =
                    curveToPath(shootingPose, alignRampControlPoint, alignRampPose);
            AutoState.COLLECT_RAMP.path = lineToPath(alignRampPose, collectRampPose);
            AutoState.COLLECT_FROM_RAMP_FINAL.path =
                    curveToPath(
                            collectRampPose, collectRampFinalControlPoint, collectRampFinalEndPose);
            AutoState.RAMP_TO_SHOOT.path =
                    curveToPath(collectRampFinalEndPose, rampToShootControlPoint, shootingPose);
        }
    }
}
