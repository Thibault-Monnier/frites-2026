package opmodes;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import logic.Team;
import logic.field.PlayingField;

public class AutoOpMode extends OpModeBase {

    private TelemetryManager panelsTelemetry;
    private Paths paths;

    private boolean pathActive = false;
    private int nbShots = 0;
    private double collectStartTime = 0;

    enum AutoState {
        SHOOT,
        MOVE_TO_SHOOT_1,
        RAMP_TO_SHOOT,
        MIDDLE_ROW_TO_SHOOT,
        ALIGN_BACK,
        COLLECT_BACK,
        COLLECT_MIDDLE,
        COLLECT_FRONT,
        ALIGN_MIDDLE,
        ALIGN_FRONT,
        ALIGN_COLLECT_RAMP,
        LEAVE,
        DONE,
        COLLECT_RAMP,
        COLLECT_FROM_RAMP_FINAL,
        COLLECTING_FROM_RAMP
    }

    private AutoState state = AutoState.MOVE_TO_SHOOT_1;

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

        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        paths = new Paths(follower, team.isBlue());

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);

        globalTelemetry.addData(
                "Pose start", PlayingField.startPose(Team.RED).toPedropathingPose().toString());
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
            /*case MOVE_TO_SHOOT_1:
                runPath(paths.MoveToShoot1, AutoState.SHOOT, false);

                Pose2D robotPose = robotPosition.getPose();
                Angle goalAngle = PlayingField.angleToGoal(robotPose.toPosition2D(), team);
                Distance goalDistance = PlayingField.distanceToGoal(robotPose.toPosition2D(), team);

                globalTelemetry.addLine("Cannon targets " + cannon.getTargetVelocity() + " and is ready to shoot? " + cannon.isReadyToShoot());

                Angle angleError = robotPose.getHeading().subtract(goalAngle).abs();
                if (angleError.leq(Angle.fromDegrees(5))
                        && goalDistance.geq(Distance.fromCentimeters(50))
                        && cannon.isReadyToShoot()) {
                    cannonBuffers.shootContinue(true);
                } else {
                    globalTelemetry.addData(
                            "Not shooting because angle error", angleError.toString());
                    globalTelemetry.addData(
                            "Not shooting because distance too small", goalDistance.toString());
                }

                break;
            */
            case MOVE_TO_SHOOT_1:
                runPath(paths.MoveToShoot1, AutoState.SHOOT, false);
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

            case ALIGN_FRONT:
                runPath(paths.AlignFrontRow, AutoState.COLLECT_FRONT, false);
                break;

            case COLLECT_FRONT:
                intake();
                runPath(paths.CollectFrontRow, AutoState.LEAVE, true);
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

            case ALIGN_COLLECT_RAMP:
                runPath(paths.AlignCollectRamp, AutoState.COLLECT_RAMP, false);
                break;

            case COLLECT_RAMP:
                intake();
                runPath(paths.CollectRamp, AutoState.COLLECTING_FROM_RAMP, true);
                break;

            case LEAVE:
                intake();
                runPath(paths.Leave, AutoState.SHOOT, false);
                break;

            case DONE:
                intake.off();
                cannonBuffers.off();
                break;
        }
    }

    private void runPath(PathChain path, AutoState nextState, boolean slow) {
        if (!pathActive) {
            intake.off();
            follower.followPath(path, slow ? 0.6 : 1, true);
            pathActive = true;
        }

        if (!follower.isBusy()) {
            pathActive = false;
            state = nextState;
            intake.off();
            if (nextState == AutoState.COLLECTING_FROM_RAMP) {
                collectStartTime = runtime.milliseconds();
            }
        }
    }

    private void runShootCycle() {
        if (follower.isBusy() || !cannon.isReadyToShoot()) return;

        intake.on();

        boolean done = cannonBuffers.shootContinue(true, 0.55);
        globalTelemetry.addData("Shots fired", cannonBuffers.getShotsFired());

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

    public static class Paths {
        public PathChain MoveToShoot1,
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

        private Pose mirror(Pose pose, boolean shouldMirror) {
            double x = pose.getX();
            double y = pose.getY();
            double heading = pose.getHeading();

            double newX = shouldMirror ? 144 - x : x;
            double newHeading = shouldMirror ? Math.PI - heading : heading;

            return new Pose(newX, y, newHeading);
        }

        public Paths(Follower follower, boolean isBlue) {
            Pose startPose =
                    PlayingField.startPose(isBlue ? Team.BLUE : Team.RED).toPedropathingPose();
            Pose shootingPose = mirror(new Pose(87, 77, Math.toRadians(51)), isBlue);
            Pose leavePose = mirror(new Pose(84, 102, Math.toRadians(38)), isBlue);

            Pose frontRowStartPose = mirror(new Pose(84, 36, Math.toRadians(0)), isBlue);
            Pose frontRowEndPose = mirror(new Pose(139, 36, Math.toRadians(0)), isBlue);

            Pose middleRowStartPose = mirror(new Pose(99, 59, Math.toRadians(0)), isBlue);
            Pose middleRowEndPose = mirror(new Pose(132.5, 59, Math.toRadians(0)), isBlue);
            Pose middleRowToShootControlPoint = mirror(new Pose(106.5, 59), isBlue);

            Pose backRowStartPos = mirror(new Pose(101, 84, Math.toRadians(0)), isBlue);
            Pose backRowEndPose = mirror(new Pose(126.5, 84, Math.toRadians(0)), isBlue);

            Pose alignRampPose = mirror(new Pose(126, 61, Math.toRadians(35)), isBlue);
            Pose alignRampControlPoint = mirror(new Pose(102, 66), isBlue);
            Pose collectRampPose = mirror(new Pose(131, 59.5, Math.toRadians(30)), isBlue);
            Pose collectRampFinalControlPoint = mirror(new Pose(127, 57.5), isBlue);
            Pose collectRampFinalEndPose = mirror(new Pose(133, 56.5, Math.toRadians(0)), isBlue);

            Pose rampToShootControlPoint = mirror(new Pose(107, 60), isBlue);

            MoveToShoot1 =
                    follower.pathBuilder()
                            .addPath(new BezierLine(startPose, shootingPose))
                            .setLinearHeadingInterpolation(
                                    startPose.getHeading(), shootingPose.getHeading())
                            .build();

            AlignBackRow =
                    follower.pathBuilder()
                            .addPath(new BezierLine(shootingPose, backRowStartPos))
                            .setLinearHeadingInterpolation(
                                    shootingPose.getHeading(), backRowStartPos.getHeading())
                            .build();

            CollectBackRow =
                    follower.pathBuilder()
                            .addPath(new BezierLine(backRowStartPos, backRowEndPose))
                            .setLinearHeadingInterpolation(
                                    backRowStartPos.getHeading(), backRowEndPose.getHeading())
                            .build();

            RampToShoot =
                    follower.pathBuilder()
                            .addPath(
                                    new BezierCurve(
                                            collectRampFinalEndPose,
                                            rampToShootControlPoint,
                                            shootingPose))
                            .setLinearHeadingInterpolation(
                                    collectRampFinalEndPose.getHeading(), shootingPose.getHeading())
                            .build();

            AlignMiddleRow =
                    follower.pathBuilder()
                            .addPath(new BezierLine(shootingPose, middleRowStartPose))
                            .setLinearHeadingInterpolation(
                                    shootingPose.getHeading(), middleRowStartPose.getHeading())
                            .build();
            CollectMiddleRow =
                    follower.pathBuilder()
                            .addPath(new BezierLine(middleRowStartPose, middleRowEndPose))
                            .setLinearHeadingInterpolation(
                                    middleRowStartPose.getHeading(), middleRowEndPose.getHeading())
                            .build();

            MiddleRowToShoot =
                    follower.pathBuilder()
                            .addPath(
                                    new BezierCurve(
                                            middleRowEndPose,
                                            middleRowToShootControlPoint,
                                            shootingPose))
                            .setLinearHeadingInterpolation(
                                    middleRowEndPose.getHeading(), shootingPose.getHeading())
                            .build();

            AlignFrontRow =
                    follower.pathBuilder()
                            .addPath(new BezierLine(shootingPose, frontRowStartPose))
                            .setLinearHeadingInterpolation(
                                    shootingPose.getHeading(), frontRowStartPose.getHeading())
                            .build();
            CollectFrontRow =
                    follower.pathBuilder()
                            .addPath(new BezierLine(frontRowStartPose, frontRowEndPose))
                            .setConstantHeadingInterpolation(frontRowEndPose.getHeading())
                            .build();

            AlignCollectRamp =
                    follower.pathBuilder()
                            .addPath(
                                    new BezierCurve(
                                            shootingPose, alignRampControlPoint, alignRampPose))
                            .setLinearHeadingInterpolation(
                                    shootingPose.getHeading(), alignRampPose.getHeading())
                            .build();

            CollectRamp =
                    follower.pathBuilder()
                            .addPath(new BezierLine(alignRampPose, collectRampPose))
                            .setLinearHeadingInterpolation(
                                    alignRampPose.getHeading(), collectRampPose.getHeading())
                            .build();

            CollectFromRampFinal =
                    follower.pathBuilder()
                            .addPath(
                                    new BezierCurve(
                                            collectRampPose,
                                            collectRampFinalControlPoint,
                                            collectRampFinalEndPose))
                            .setLinearHeadingInterpolation(
                                    collectRampPose.getHeading(),
                                    collectRampFinalEndPose.getHeading())
                            .build();

            Leave =
                    follower.pathBuilder()
                            .addPath(new BezierLine(backRowEndPose, leavePose))
                            .setLinearHeadingInterpolation(
                                    frontRowEndPose.getHeading(), leavePose.getHeading())
                            .build();
        }
    }
}
