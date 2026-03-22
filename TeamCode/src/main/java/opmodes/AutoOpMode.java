package opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import logic.Team;
import logic.field.PlayingField;

public class AutoOpMode extends AutoOpModeBase {
    private final Paths paths;

    private boolean pathActive = false;
    private double pathStartTime = -1000;

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
        // COLLECT_FRONT,
        // ALIGN_FRONT,
        ALIGN_COLLECT_RAMP,
        COLLECT_RAMP,
        COLLECTING_FROM_RAMP,
        COLLECT_FROM_RAMP_FINAL,
        LEAVE,
        SHOOT,
        DONE,
    }

    private AutoState state = AutoState.START_TO_SHOOT;

    public AutoOpMode(Team team) {
        super(team, false);
        paths = new Paths(follower, team.isBlue());
    }

    @Override
    protected Object getState() {
        return state;
    }

    protected void execute() {
        switch (state) {
            case START_TO_SHOOT:
                intake();
                runPath(paths.StartToShoot, AutoState.SHOOT, false);
                break;

            case RAMP_TO_SHOOT:
                intake();
                runPath(paths.RampToShoot, AutoState.SHOOT, false);
                break;

            case MIDDLE_ROW_TO_SHOOT:
                intake();
                runPath(paths.MiddleRowToShoot, AutoState.SHOOT, false);
                break;

            case ALIGN_BACK:
                runPath(paths.AlignBackRow, AutoState.COLLECT_BACK, false);
                break;

            case COLLECT_BACK:
                intake();
                runPath(paths.CollectBackRow, AutoState.LEAVE, true);
                break;

            case ALIGN_MIDDLE:
                runPath(paths.AlignMiddleRow, AutoState.COLLECT_MIDDLE, false);
                break;

            case COLLECT_MIDDLE:
                intake();
                runPath(paths.CollectMiddleRow, AutoState.MIDDLE_ROW_TO_SHOOT, true);
                break;

            // case ALIGN_FRONT:
            //     runPath(paths.AlignFrontRow, AutoState.COLLECT_FRONT, false);
            //     break;
            //
            // case COLLECT_FRONT:
            //     intake();
            //     runPath(paths.CollectFrontRow, AutoState.LEAVE, true);
            //     break;

            case ALIGN_COLLECT_RAMP:
                runPath(paths.AlignCollectRamp, AutoState.COLLECT_RAMP, false);
                break;

            case COLLECT_RAMP:
                intake();
                runPath(paths.CollectRamp, AutoState.COLLECTING_FROM_RAMP, true);
                break;

            case COLLECTING_FROM_RAMP:
                intake();
                if (runtime.milliseconds() - collectStartTime > 650) {
                    state = AutoState.COLLECT_FROM_RAMP_FINAL;
                }
                break;

            case COLLECT_FROM_RAMP_FINAL:
                intake();
                runPath(paths.CollectFromRampFinal, AutoState.RAMP_TO_SHOOT, true);
                break;

            case SHOOT:
                runShootCycle();
                break;

            case LEAVE:
                intake();
                runPath(paths.Leave, AutoState.SHOOT, false);
                break;

            case DONE:
                break;
        }
    }

    private void runPath(PathChain path, AutoState nextState, boolean slow) {
        if (!pathActive) {
            follower.followPath(path, slow ? 0.7 : 1, true);
            pathActive = true;
            pathStartTime = runtime.milliseconds();
        }

        if (!follower.isBusy()
                || follower.isRobotStuck()
                || runtime.milliseconds() - pathStartTime > 4000) { // Hard time limit as backup
            follower.breakFollowing();
            pathActive = false;
            state = nextState;
            if (nextState == AutoState.COLLECTING_FROM_RAMP) {
                collectStartTime = runtime.milliseconds();
            }
        }
    }

    private void runShootCycle() {
        if (!cannon.isReadyToShoot()) return;

        intake.on();

        // If red, start right
        boolean done = cannonBuffers.shootContinue(team.isBlue(), 0.5);

        if (done) {
            cannonBuffers.shootReset();
            nbShots++;

            if (nbShots == 1) state = AutoState.ALIGN_MIDDLE;
            else if (nbShots == 2) state = AutoState.ALIGN_COLLECT_RAMP;
            else if (nbShots == 3) state = AutoState.ALIGN_COLLECT_RAMP;
            else if (nbShots == 4) state = AutoState.ALIGN_BACK;
            else state = AutoState.DONE;
        }
    }

    private static class Paths extends PathsBase {
        PathChain StartToShoot,
                AlignBackRow,
                CollectBackRow,
                RampToShoot,
                AlignMiddleRow,
                CollectMiddleRow,
                MiddleRowToShoot,
                AlignFrontRow,
                CollectFrontRow,
                AlignCollectRamp,
                CollectRamp,
                CollectFromRampFinal,
                Leave;

        public Paths(Follower follower, boolean isBlue) {
            super(follower, isBlue);
        }

        @Override
        protected void createPaths() {
            Pose startPose = PlayingField.startPose(Team.RED).toPedropathingPose();
            Pose shootingPose = new Pose(88, 76, Math.toRadians(53));
            Pose leavePose = new Pose(85, 101, Math.toRadians(38));

            Pose frontRowStartPose = new Pose(84, 36, Math.toRadians(0));
            Pose frontRowEndPose = new Pose(139, 36, Math.toRadians(0));

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

            StartToShoot = lineToPath(startPose, shootingPose);

            AlignBackRow = lineToPath(shootingPose, backRowStartPos);
            CollectBackRow = curveToPath(backRowStartPos, backRowControlPoint, backRowEndPose);
            Leave = lineToPath(backRowEndPose, leavePose);

            AlignMiddleRow = lineToPath(shootingPose, middleRowStartPose);
            CollectMiddleRow =
                    curveToPath(middleRowStartPose, middleRowControlPoint, middleRowEndPose);
            MiddleRowToShoot =
                    curveToPath(middleRowEndPose, middleRowToShootControlPoint, shootingPose);

            AlignFrontRow = lineToPath(shootingPose, frontRowStartPose);
            CollectFrontRow = lineToPath(frontRowStartPose, frontRowEndPose);

            AlignCollectRamp = curveToPath(shootingPose, alignRampControlPoint, alignRampPose);
            CollectRamp = lineToPath(alignRampPose, collectRampPose);
            CollectFromRampFinal =
                    curveToPath(
                            collectRampPose, collectRampFinalControlPoint, collectRampFinalEndPose);
            RampToShoot =
                    curveToPath(collectRampFinalEndPose, rampToShootControlPoint, shootingPose);
        }
    }
}
