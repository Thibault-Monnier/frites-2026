package opmodes.debug;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import logic.Team;
import logic.action.ActionSequence;
import logic.field.PlayingField;

import opmodes.AutoOpModeBase;
import opmodes.GroupConstants;

@Autonomous(
        name = GroupConstants.DEBUGGER_MODES_GROUP + ": Shoot Auto Op Mode",
        group = GroupConstants.DEBUGGER_MODES_GROUP)
public class ShootAutoOpMode extends AutoOpModeBase {
    private ActionSequence sequence;

    private ShootAutoOpMode.Paths paths;

    public ShootAutoOpMode() {
        super(Team.RED, false);
    }

    @Override
    protected void initialize() {
        super.initialize();

        paths = new ShootAutoOpMode.Paths(follower, team.isBlue());

        sequence =
                new ActionSequence(
                        startCannon(),
                        pathAction(paths.startToShoot, true, false, true),
                        shootAction(),
                        stopCannon());
    }

    protected void execute() {
        sequence.run();
    }

    private static class Paths extends PathsBase {
        PathChain startToShoot;

        public Paths(Follower follower, boolean isBlue) {
            super(follower, isBlue);
            createPaths();
        }

        @Override
        protected void createPaths() {
            Pose startPose = PlayingField.startPose(Team.RED).toPedropathingPose();
            Pose shootingPose = new Pose(88, 76, Math.toRadians(53));
            startToShoot = lineToPath(startPose, shootingPose);
        }
    }
}
