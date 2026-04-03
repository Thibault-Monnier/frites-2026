package opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import logic.Team;
import logic.action.Action;
import logic.action.DelayAction;
import logic.action.SimpleAction;
import logic.field.PlayingField;

import utils.TelemetryHandler;

public abstract class AutoOpModeBase extends OpModeBase {
    private boolean pathActive = false;
    private double pathStartTime = -1000;

    public AutoOpModeBase(Team team, boolean useFarStartPose) {
        super(team, useFarStartPose, true);
    }

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        runStart();

        double prevTime = runtime.milliseconds();
        while (opModeIsActive()) {
            // Consistent step duration for better PIDs
            double time = runtime.milliseconds();
            TelemetryHandler.addData("Delta time", time - prevTime);
            while (time - prevTime < 35) {
                time = runtime.milliseconds();
            }
            prevTime = time;

            runStep();
        }

        runStop();
    }

    @Override
    protected void initialize() {
        super.initialize();

        TelemetryHandler.addData(
                "Pose start (Pedro Pathing)",
                PlayingField.startPose(team).toPedropathingPose().toString());
    }

    @Override
    protected void runStart() {
        super.runStart();
        cannon.on();
    }

    private void runStep() {
        update();

        execute();

        apply(true); // Pedro Pathing controls drive motors
        log();
    }

    @Override
    protected void update() {
        super.update();
        intake.off();
        cannonBuffers.off();
    }

    @Override
    protected void log() {
        super.log();

        TelemetryHandler.addData("Pose", follower.getPose().toString());
    }

    protected abstract void execute();

    protected Action startCannon() {
        return new SimpleAction(() -> cannon.on());
    }

    protected Action waitAction(double delay, Runnable doWhile) {
        return new DelayAction(delay, doWhile);
    }

    protected Action pathAction(PathChain path, boolean useIntake, boolean isSlow) {
        return () -> {
            if (!pathActive) {
                follower.followPath(path, isSlow ? 0.7 : 1, true);
                pathActive = true;
                pathStartTime = runtime.milliseconds();
            }

            if (useIntake) intake();

            if (!follower.isBusy()
                    || follower.isRobotStuck()
                    || runtime.milliseconds() - pathStartTime > 4000) { // Hard time limit as backup
                follower.breakFollowing();
                pathActive = false;
                return true;
            }

            return false;
        };
    }

    protected Action shootAction() {
        return () -> {
            if (!cannon.isReadyToShoot()) return false;

            intake.on();

            // If red, start right
            boolean done = cannonBuffers.shootContinue(team.isBlue(), 0.5);

            if (done) cannonBuffers.shootReset();

            return done;
        };
    }

    protected void intake() {
        cannonBuffers.reverse();
        intake.on();
    }

    protected abstract static class PathsBase {
        private final Follower follower;
        private final boolean isBlue;

        public PathsBase(Follower follower, boolean isBlue) {
            this.follower = follower;
            this.isBlue = isBlue;
            createPaths();
        }

        /// Create paths for red team. It will be automatically mirrored if necessary later on.
        protected abstract void createPaths();

        protected PathChain lineToPath(Pose start, Pose end) {
            start = resolveRealPose(start);
            end = resolveRealPose(end);

            return follower.pathBuilder()
                    .addPath(new BezierLine(start, end))
                    .setLinearHeadingInterpolation(start.getHeading(), end.getHeading())
                    .build();
        }

        protected PathChain curveToPath(Pose start, Pose controlPoint, Pose end) {
            start = resolveRealPose(start);
            controlPoint = resolveRealPose(controlPoint);
            end = resolveRealPose(end);

            return follower.pathBuilder()
                    .addPath(new BezierCurve(start, controlPoint, end))
                    .setLinearHeadingInterpolation(start.getHeading(), end.getHeading())
                    .build();
        }

        /// Mirrors pose if blue team
        private Pose resolveRealPose(Pose pose) {
            double x = pose.getX();
            double y = pose.getY();
            double heading = pose.getHeading();

            double newX = isBlue ? 144 - x : x;
            double newHeading = isBlue ? Math.PI - heading : heading;

            return new Pose(newX, y, newHeading);
        }
    }
}
