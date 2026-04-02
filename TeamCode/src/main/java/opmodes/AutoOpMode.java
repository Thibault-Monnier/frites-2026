package opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import logic.Team;
import logic.action.Action;
import logic.action.ActionSequence;
import logic.field.PlayingField;

public class AutoOpMode extends AutoOpModeBase {
    private ActionSequence sequence;

    private Paths paths;

    public AutoOpMode(Team team) {
        super(team, false);
    }

    @Override
    protected void initialize() {
        super.initialize();

        paths = new Paths(follower, team.isBlue());

        sequence =
                new ActionSequence(
                        startCannon(),
                        startToShoot(),
                        middleRowCycle(),
                        rampCycle(),
                        rampCycle(),
                        backRowCycle());
    }

    private Action startToShoot() {
        return new ActionSequence(pathAction(paths.startToShoot, true, false), shootAction());
    }

    private Action middleRowCycle() {
        return new ActionSequence(
                pathAction(paths.alignMiddleRow, false, false),
                pathAction(paths.collectMiddleRow, true, true),
                pathAction(paths.middleRowToShoot, true, false),
                shootAction());
    }

    private Action backRowCycle() {
        return new ActionSequence(
                pathAction(paths.alignBackRow, false, false),
                pathAction(paths.collectBackRow, true, true),
                pathAction(paths.leave, true, false),
                shootAction());
    }

    private Action rampCycle() {
        return new ActionSequence(
                pathAction(paths.alignCollectRamp, false, false),
                pathAction(paths.collectRamp, true, true),
                waitAction(0.650, this::intake),
                pathAction(paths.collectFromRampFinal, true, false),
                pathAction(paths.rampToShoot, true, false),
                shootAction());
    }

    protected void execute() {
        sequence.run();
    }

    private static class Paths extends PathsBase {
        PathChain startToShoot,
                alignBackRow,
                collectBackRow,
                rampToShoot,
                alignMiddleRow,
                collectMiddleRow,
                middleRowToShoot,
                alignCollectRamp,
                collectRamp,
                collectFromRampFinal,
                leave;

        public Paths(Follower follower, boolean isBlue) {
            super(follower, isBlue);
            createPaths();
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

            startToShoot = lineToPath(startPose, shootingPose);

            alignBackRow = lineToPath(shootingPose, backRowStartPos);
            collectBackRow = curveToPath(backRowStartPos, backRowControlPoint, backRowEndPose);
            leave = lineToPath(backRowEndPose, leavePose);

            alignMiddleRow = lineToPath(shootingPose, middleRowStartPose);
            collectMiddleRow =
                    curveToPath(middleRowStartPose, middleRowControlPoint, middleRowEndPose);
            middleRowToShoot =
                    curveToPath(middleRowEndPose, middleRowToShootControlPoint, shootingPose);

            alignCollectRamp = curveToPath(shootingPose, alignRampControlPoint, alignRampPose);
            collectRamp = lineToPath(alignRampPose, collectRampPose);
            collectFromRampFinal =
                    curveToPath(
                            collectRampPose, collectRampFinalControlPoint, collectRampFinalEndPose);
            rampToShoot =
                    curveToPath(collectRampFinalEndPose, rampToShootControlPoint, shootingPose);
        }
    }
}
