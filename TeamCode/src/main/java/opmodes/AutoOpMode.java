package opmodes;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.FTCCoordinates;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.PedroCoordinates;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.util.ElapsedTime;

import logic.Team;
import logic.position.LimelightHandler;

import math.Pose2D;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import pedropathing.Constants;

public class AutoOpMode extends OpModeBase {

    private TelemetryManager panelsTelemetry;
    private LimelightHandler limelightHandler;
    private Follower follower;
    private Paths paths;

    private ElapsedTime stateTimer;

    private boolean pathActive = false;
    private int nbShots = 0;

    enum AutoState {
        MOVE_TO_SHOOT_1,
        SHOOT,
        COLLECT_TOP,
        MOVE_TO_SHOOT_2,
        ALIGN_MIDDLE,
        COLLECT_MIDDLE,
        MOVE_TO_SHOOT_3,
        ALIGN_BOTTOM,
        COLLECT_BOTTOM,
        MOVE_TO_SHOOT_4,
        PARK,
        DONE
    }

    private AutoState state = AutoState.MOVE_TO_SHOOT_1;

    @Override
    protected void runStart() {
        // Do nothing.
    }

    public AutoOpMode(Team team, boolean shouldResetPose) {
        super(team, shouldResetPose);
    }

    @Override
    public void runOpMode() {
        stateTimer = new ElapsedTime();
        initialize();

        limelightHandler = new LimelightHandler(globalTelemetry, hardwareMap);
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(120, 120, Math.toRadians(45)));

        paths = new Paths(follower, team == Team.BLUE);

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);

        hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        waitForStart();
        runStart();

        cannon.motorTargetVelocity = 1200;
        cannon.on();

        while (opModeIsActive()) {
            for (LynxModule hub : hubs) {
                hub.clearBulkCache();
            }

            // Don't call move.apply() — PedroPathing controls the drive motors in Auto.
            intake.apply();
            cannon.apply();
            cannonBuffers.apply();

            follower.update();
            limelightHandler.update();
//            follower.setPose(getRobotPoseFromCamera());

            updateStateMachine();

            panelsTelemetry.debug("State", state);
            panelsTelemetry.debug("X", follower.getPose().getX());
            panelsTelemetry.debug("Y", follower.getPose().getY());
            panelsTelemetry.debug("Heading", follower.getPose().getHeading());
            panelsTelemetry.debug("Heading", cannon.motorTargetVelocity);
            panelsTelemetry.update(telemetry);
        }
    }

    /*------------------------------------------------*/
    /*                STATE MACHINE                   */
    /*------------------------------------------------*/

    private void updateStateMachine() {

        switch (state) {

            case MOVE_TO_SHOOT_1:
//                intake.on();
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

            case COLLECT_TOP:
                intake.on();
                cannonBuffers.reverse();
                runPath(paths.CollectTopRow, AutoState.MOVE_TO_SHOOT_2, true);
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

            case ALIGN_BOTTOM:
                intake.on();
                runPath(paths.AlignBottomRow, AutoState.COLLECT_BOTTOM, false);
                break;

            case COLLECT_BOTTOM:
                intake.on();
                cannonBuffers.reverse();
                runPath(paths.CollectBottomRow, AutoState.MOVE_TO_SHOOT_4, true);
                break;

            case SHOOT:
                runShootCycle();
                break;

            case PARK:
                runPath(paths.Park, AutoState.DONE, false);
                break;

            case DONE:
                intake.off();
                cannonBuffers.off();
                break;
        }
    }

    /*------------------------------------------------*/
    /*                PATH EXECUTION                  */
    /*------------------------------------------------*/

    private void runPath(PathChain path, AutoState nextState, boolean slow) {

        if (!pathActive) {
            intake.off();
            follower.followPath(path, slow ? 0.6 : 1, true);
            pathActive = true;
        }

        if (!follower.isBusy()) {
            pathActive = false;
            state = nextState;
            stateTimer.reset();   // timer starts AFTER motion completes
        }
    }

    /*------------------------------------------------*/
    /*                SHOOTING LOGIC                  */
    /*------------------------------------------------*/

    private void runShootCycle() {

        if (follower.isBusy()) return;

        intake.on();

        double t = stateTimer.seconds();

        if (t < 0.5) {
            cannonBuffers.leftBuffer.on();
        } else if (t < 1.0) {
            cannonBuffers.leftBuffer.off();
            cannonBuffers.rightBuffer.on();
        } else if (t < 1.5) {
            cannonBuffers.rightBuffer.on();
            cannonBuffers.leftBuffer.on();
        } else if (t < 2.0) {
            cannonBuffers.leftBuffer.off();
            cannonBuffers.rightBuffer.off();
        } else {
            cannonBuffers.off();
            nbShots++;

            if (nbShots == 1) state = AutoState.COLLECT_TOP;
            else if (nbShots == 2) state = AutoState.ALIGN_MIDDLE;
            else if (nbShots == 3) state = AutoState.ALIGN_BOTTOM;
            else state = AutoState.PARK;

            stateTimer.reset();
        }
    }

    /*------------------------------------------------*/
    /*                VISION POSE                     */
    /*------------------------------------------------*/

    private Pose getRobotPoseFromCamera() {
        Pose2D lastPose = limelightHandler.getLastKnownPose();
        if (lastPose == null) {
            return follower.getPose();
        }

        return new Pose(
                lastPose.getX(DistanceUnit.INCH),
                lastPose.getY(DistanceUnit.INCH),
                lastPose.getHeading(AngleUnit.RADIANS),
                FTCCoordinates.INSTANCE
        ).getAsCoordinateSystem(PedroCoordinates.INSTANCE);
    }

    /*------------------------------------------------*/
    /*                     PATHS                      */
    /*------------------------------------------------*/

    public static class Paths {

        public PathChain MoveToShoot1, CollectTopRow, MoveToShoot2,
                AlignMiddleRow, CollectMiddleRow, MoveToShoot3,
                AlignBottomRow, CollectBottomRow, MoveToShoot4, Park;

        private double mirrorX(double x, boolean blue) {
            return blue ? 144 - x : x;
        }

        private double mirrorHeading(double headingRad, boolean blue) {
            if (!blue) return headingRad;
            return Math.PI - headingRad;
        }

        public Paths(Follower follower, boolean blue) {

            MoveToShoot1 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(mirrorX(120, blue), 120),
                            new Pose(mirrorX(84, blue), 84)))
                    .setLinearHeadingInterpolation(
                            mirrorHeading(Math.toRadians(45), blue),
                            mirrorHeading(Math.toRadians(45), blue))
                    .build();

            CollectTopRow = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(mirrorX(84, blue), 84),
                            new Pose(mirrorX(135, blue), 84)))
                    .setConstantHeadingInterpolation(0)
                    .build();

            MoveToShoot2 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(mirrorX(135, blue), 84),
                            new Pose(mirrorX(84, blue), 84)))
                    .setLinearHeadingInterpolation(
                            mirrorHeading(Math.toRadians(0), blue),
                            mirrorHeading(Math.toRadians(45), blue))
                    .build();

            AlignMiddleRow = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(mirrorX(84, blue), 84),
                            new Pose(mirrorX(84, blue), 57)))
                    .setLinearHeadingInterpolation(
                            mirrorHeading(Math.toRadians(45), blue),
                            mirrorHeading(Math.toRadians(0), blue))
                    .build();
            CollectMiddleRow = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(mirrorX(84, blue), 57),
                            new Pose(mirrorX(139, blue), 57)))
                    .addPath(new BezierLine(
                            new Pose(mirrorX(139, blue), 57),
                            new Pose(mirrorX(100, blue), 57)
                    ))
                    .setConstantHeadingInterpolation(0)
                    .build();

            MoveToShoot3 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(mirrorX(139, blue), 60),
                            new Pose(mirrorX(84, blue), 84)))
                    .setLinearHeadingInterpolation(
                            mirrorHeading(Math.toRadians(0), blue),
                            mirrorHeading(Math.toRadians(45), blue))
                    .build();

            AlignBottomRow = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(mirrorX(84, blue), 84),
                            new Pose(mirrorX(84, blue), 36)))
                    .setLinearHeadingInterpolation(
                            mirrorHeading(Math.toRadians(45), blue),
                            mirrorHeading(Math.toRadians(0), blue))
                    .build();

            CollectBottomRow = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(mirrorX(84, blue), 36),
                            new Pose(mirrorX(139, blue), 36)))
                    .setConstantHeadingInterpolation(0)
                    .build();

            MoveToShoot4 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(mirrorX(139, blue), 36),
                            new Pose(mirrorX(84, blue), 84)))
                    .setLinearHeadingInterpolation(
                            mirrorHeading(Math.toRadians(0), blue),
                            mirrorHeading(Math.toRadians(45), blue))
                    .build();

            Park = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(mirrorX(84, blue), 84),
                            new Pose(mirrorX(96, blue), 70)))
                    .setLinearHeadingInterpolation(
                            mirrorHeading(Math.toRadians(45), blue),
                            mirrorHeading(Math.toRadians(0), blue))
                    .build();
        }
    }
}
