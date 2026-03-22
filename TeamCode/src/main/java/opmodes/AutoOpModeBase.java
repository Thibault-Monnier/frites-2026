package opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import logic.Team;
import logic.field.PlayingField;

public abstract class AutoOpModeBase extends OpModeBase {
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
            globalTelemetry.addData("Delta time", time - prevTime);
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
        useFollower();

        globalTelemetry.addData(
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

        apply(false); // Pedro Pathing controls drive motors
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

        globalTelemetry.addData("Pose", follower.getPose().toString());
        globalTelemetry.addData("State", getState().toString());
    }

    protected abstract Object getState();

    protected abstract void execute();

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
