package opmodes;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import logic.Team;

import pedropathing.Constants;

public class AutoOpMode extends OpModeBase {

    private TelemetryManager panelsTelemetry;
    private Follower follower;
    private Paths paths;

    private boolean pathActive = false;
    private int nbShots = 0;

    enum AutoState {
        SHOOT,
        MOVE_TO_SHOOT_1,
        MOVE_TO_SHOOT_2,
        MOVE_TO_SHOOT_3,
        MOVE_TO_SHOOT_4,
        COLLECT_BACK,
        COLLECT_MIDDLE,
        COLLECT_FRONT,
        ALIGN_MIDDLE,
        ALIGN_FRONT,
        LEAVE,
        DONE
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

        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(120, 120, Math.toRadians(45)));

        paths = new Paths(follower, team.isBlue());

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    protected void runStart() {
        super.runStart();

        cannon.setTargetVelocity(1200);
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

        follower.update();
        follower.setPose(robotPosition.getPose().toPedropathingPose());
    }

    @Override
    protected void log() {
        super.log();

        panelsTelemetry.debug("State", state);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());
        panelsTelemetry.update(telemetry);
    }

    private void execute() {
        switch (state) {
            case MOVE_TO_SHOOT_1:
                cannonBuffers.off();
                runPath(paths.MoveToShoot1, AutoState.SHOOT, false);
                break;

            case MOVE_TO_SHOOT_2:
                intake.on();
                cannonBuffers.off();
                runPath(paths.MoveToShoot2, AutoState.SHOOT, false);
                break;

            case MOVE_TO_SHOOT_3:
                intake.on();
                cannonBuffers.off();
                runPath(paths.MoveToShoot3, AutoState.SHOOT, false);
                break;

            case MOVE_TO_SHOOT_4:
                intake.on();
                cannonBuffers.off();
                runPath(paths.MoveToShoot4, AutoState.SHOOT, false);
                break;

            case COLLECT_BACK:
                intake.on();
                cannonBuffers.reverse();
                runPath(paths.CollectBackRow, AutoState.MOVE_TO_SHOOT_2, true);
                break;

            case ALIGN_MIDDLE:
                intake.on();
                runPath(paths.AlignMiddleRow, AutoState.COLLECT_MIDDLE, false);
                break;

            case COLLECT_MIDDLE:
                intake.on();
                cannonBuffers.reverse();
                runPath(paths.CollectMiddleRow, AutoState.MOVE_TO_SHOOT_3, true);
                break;

            case ALIGN_FRONT:
                intake.on();
                runPath(paths.AlignFrontRow, AutoState.COLLECT_FRONT, false);
                break;

            case COLLECT_FRONT:
                intake.on();
                cannonBuffers.reverse();
                runPath(paths.CollectFrontRow, AutoState.MOVE_TO_SHOOT_4, true);
                break;

            case SHOOT:
                runShootCycle();
                break;

            case LEAVE:
                runPath(paths.Leave, AutoState.DONE, false);
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
        }
    }

    private void runShootCycle() {
        if (follower.isBusy()) return;

        intake.on();

        boolean done = cannonBuffers.shootContinue(true);

        if (done) {
            cannonBuffers.shootReset();
            nbShots++;

            if (nbShots == 1) state = AutoState.COLLECT_BACK;
            else if (nbShots == 2) state = AutoState.ALIGN_MIDDLE;
            else if (nbShots == 3) state = AutoState.ALIGN_FRONT;
            else state = AutoState.LEAVE;
        }
    }

    /*------------------------------------------------*/
    /*                     PATHS                      */
    /*------------------------------------------------*/

    public static class Paths {
        public PathChain MoveToShoot1,
                CollectBackRow,
                MoveToShoot2,
                AlignMiddleRow,
                CollectMiddleRow,
                MoveToShoot3,
                AlignFrontRow,
                CollectFrontRow,
                MoveToShoot4,
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
            Pose startPose = mirror(new Pose(120, 120, Math.toRadians(45)), isBlue);
            Pose shootingPose = mirror(new Pose(84, 84, Math.toRadians(45)), isBlue);
            Pose leavePose = mirror(new Pose(96, 70, Math.toRadians(0)), isBlue);

            Pose middleRowStartPose = mirror(new Pose(84, 57, Math.toRadians(0)), isBlue);
            Pose frontRowStartPose = mirror(new Pose(84, 36, Math.toRadians(0)), isBlue);

            Pose backRowEndPose = mirror(new Pose(135, 84, Math.toRadians(0)), isBlue);
            Pose middleRowEndPose = mirror(new Pose(139, 57, Math.toRadians(0)), isBlue);
            Pose frontRowEndPose = mirror(new Pose(139, 36, Math.toRadians(0)), isBlue);

            Pose middleRowBackOutPose = mirror(new Pose(100, 57, Math.toRadians(0)), isBlue);

            MoveToShoot1 =
                    follower.pathBuilder()
                            .addPath(new BezierLine(startPose, shootingPose))
                            .setLinearHeadingInterpolation(
                                    startPose.getHeading(), shootingPose.getHeading())
                            .build();

            CollectBackRow =
                    follower.pathBuilder()
                            .addPath(new BezierLine(shootingPose, backRowEndPose))
                            .setConstantHeadingInterpolation(backRowEndPose.getHeading())
                            .build();

            MoveToShoot2 =
                    follower.pathBuilder()
                            .addPath(new BezierLine(backRowEndPose, shootingPose))
                            .setLinearHeadingInterpolation(
                                    backRowEndPose.getHeading(), shootingPose.getHeading())
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
                            .addPath(new BezierLine(middleRowEndPose, middleRowBackOutPose))
                            .setConstantHeadingInterpolation(middleRowEndPose.getHeading())
                            .build();

            MoveToShoot3 =
                    follower.pathBuilder()
                            .addPath(new BezierLine(middleRowBackOutPose, shootingPose))
                            .setLinearHeadingInterpolation(
                                    middleRowBackOutPose.getHeading(), shootingPose.getHeading())
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

            MoveToShoot4 =
                    follower.pathBuilder()
                            .addPath(new BezierLine(frontRowEndPose, shootingPose))
                            .setLinearHeadingInterpolation(
                                    frontRowEndPose.getHeading(), shootingPose.getHeading())
                            .build();

            Leave =
                    follower.pathBuilder()
                            .addPath(new BezierLine(shootingPose, leavePose))
                            .setLinearHeadingInterpolation(
                                    shootingPose.getHeading(), leavePose.getHeading())
                            .build();
        }
    }
}
