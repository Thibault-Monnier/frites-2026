package opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import logic.Team;
import logic.field.PlayingField;

public class AutoOpMode extends OpModeBase {
    private Paths paths;

    private boolean pathActive = false;
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
        super(team, true);
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

        paths = new Paths(follower, team.isBlue());

        globalTelemetry.addData(
                "Pose start", PlayingField.startPose(team).toPedropathingPose().toString());
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

        globalTelemetry.addData("State", state);
        globalTelemetry.addData("X", follower.getPose().getX());
        globalTelemetry.addData("Y", follower.getPose().getY());
        globalTelemetry.addData("Heading", follower.getPose().getHeading());
    }

    private void execute() {
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
        }

        if (!follower.isBusy() || follower.isRobotStuck()) {
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

    private void intake() {
        cannonBuffers.reverse();
        intake.on();
    }

    private static class Paths {
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

        private final Follower follower;
        private final boolean isBlue;

        public Paths(Follower follower, boolean isBlue) {
            this.follower = follower;
            this.isBlue = isBlue;
            createPaths();
        }

        private void createPaths() {
            Pose startPose =
                    PlayingField.startPose(isBlue ? Team.BLUE : Team.RED).toPedropathingPose();
            Pose shootingPose = mirror(new Pose(88, 76, Math.toRadians(53)), isBlue);
            Pose leavePose = mirror(new Pose(85, 101, Math.toRadians(38)), isBlue);

            Pose frontRowStartPose = mirror(new Pose(84, 36, Math.toRadians(0)), isBlue);
            Pose frontRowEndPose = mirror(new Pose(139, 36, Math.toRadians(0)), isBlue);

            Pose middleRowStartPose = mirror(new Pose(99, 62, Math.toRadians(0)), isBlue);
            Pose middleRowControlPoint = mirror(new Pose(126, 63, Math.toRadians(0)), isBlue);
            Pose middleRowEndPose = mirror(new Pose(131, 56, Math.toRadians(0)), isBlue);
            Pose middleRowToShootControlPoint = mirror(new Pose(106.5, 59), isBlue);

            Pose backRowStartPos = mirror(new Pose(100, 84, Math.toRadians(0)), isBlue);
            Pose backRowControlPoint = mirror(new Pose(106.5, 88), isBlue);
            Pose backRowEndPose = mirror(new Pose(126, 83, Math.toRadians(-10)), isBlue);

            Pose alignRampPose = mirror(new Pose(126, 61, Math.toRadians(35)), isBlue);
            Pose alignRampControlPoint = mirror(new Pose(102, 66), isBlue);
            Pose collectRampPose = mirror(new Pose(131, 59.5, Math.toRadians(30)), isBlue);
            Pose collectRampFinalControlPoint = mirror(new Pose(127, 57.5), isBlue);
            Pose collectRampFinalEndPose = mirror(new Pose(131, 56.5, Math.toRadians(20)), isBlue);

            Pose rampToShootControlPoint = mirror(new Pose(107, 60), isBlue);

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

        private PathChain lineToPath(Pose start, Pose end) {
            return follower.pathBuilder()
                    .addPath(new BezierLine(start, end))
                    .setLinearHeadingInterpolation(start.getHeading(), end.getHeading())
                    .build();
        }

        private PathChain curveToPath(Pose start, Pose controlPoint, Pose end) {
            return follower.pathBuilder()
                    .addPath(new BezierCurve(start, controlPoint, end))
                    .setLinearHeadingInterpolation(start.getHeading(), end.getHeading())
                    .build();
        }

        private Pose mirror(Pose pose, boolean shouldMirror) {
            double x = pose.getX();
            double y = pose.getY();
            double heading = pose.getHeading();

            double newX = shouldMirror ? 144 - x : x;
            double newHeading = shouldMirror ? Math.PI - heading : heading;

            return new Pose(newX, y, newHeading);
        }
    }
}
