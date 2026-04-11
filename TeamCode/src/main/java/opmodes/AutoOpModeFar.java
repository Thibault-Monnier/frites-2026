package opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import logic.Team;
import logic.field.PlayingField;

import utils.TelemetryHandler;

public class AutoOpModeFar extends OpModeBase {

    private Paths paths;

    private boolean pathActive = false;
    private int nbShots = 0;

    enum AutoState {
        SHOOT,
        MOVE_TO_SHOOT_1,
        ALIGN_COLLECT_HUMAN_PLAYER,
        COLLECT_HUMAN_PLAYER,
        MOVE_TO_SHOOT_AFTER_COLLECT_HUMAN_PLAYER,
        ALIGN_COLLECT_BOTTOM_ROW,
        COLLECT_BOTTOM_ROW,
        MOVE_TO_SHOOT_AFTER_COLLECT_BOTTOM_ROW,
        LEAVE,
        DONE
    }

    private AutoState state = AutoState.MOVE_TO_SHOOT_1;

    public AutoOpModeFar(Team team) {
        super(team, true, true);
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

            //runStep();
        }

        runStop();
    }

    @Override
    protected void initialize() {
        super.initialize();

        paths = new Paths(follower, team.isBlue());

        TelemetryHandler.addData(
                "Pose start", PlayingField.startPose(Team.RED).toPedropathingPose().toString());
    }

    @Override
    protected void runStart() {
        super.runStart();
        cannon.on();
    }

   /* private void runStep() {
        update();

        execute();

        apply(false); // Pedro Pathing controls drive motors
        log();
    }*/

    /*private void execute() {
        switch (state) {
            case MOVE_TO_SHOOT_1:
                intake();
                runPath(paths.MoveToShoot1, AutoState.SHOOT, false);
                break;
            case SHOOT:
                runShootCycle();
                break;
            case ALIGN_COLLECT_HUMAN_PLAYER:
                intake();
                runPath(paths.AlignCollectHumanPlayer, AutoState.COLLECT_HUMAN_PLAYER, false);
                break;
            case COLLECT_HUMAN_PLAYER:
                intake();
                runPath(
                        paths.CollectHumanPlayer,
                        AutoState.MOVE_TO_SHOOT_AFTER_COLLECT_HUMAN_PLAYER,
                        true);
                break;
            case MOVE_TO_SHOOT_AFTER_COLLECT_HUMAN_PLAYER:
                intake();
                runPath(paths.MoveToShootAfterCollectHumanPlayer, AutoState.SHOOT, false);
                break;
            case ALIGN_COLLECT_BOTTOM_ROW:
                intake();
                runPath(paths.AlignCollectBottomRow, AutoState.COLLECT_BOTTOM_ROW, false);
                break;
            case COLLECT_BOTTOM_ROW:
                intake();
                runPath(
                        paths.CollectBottomRow,
                        AutoState.MOVE_TO_SHOOT_AFTER_COLLECT_BOTTOM_ROW,
                        true);
                break;
            case MOVE_TO_SHOOT_AFTER_COLLECT_BOTTOM_ROW:
                intake();
                runPath(paths.MoveToShootAfterCollectBottomRow, AutoState.SHOOT, false);
                break;
            case LEAVE:
                runPath(paths.Leave, AutoState.DONE, false);
                break;
            case DONE:
                intake.off();
                cannonBuffers.off();
                break;
        }
    }*/

    public static class Paths {
        public PathChain MoveToShoot1,
                AlignCollectHumanPlayer,
                CollectHumanPlayer,
                MoveToShootAfterCollectHumanPlayer,
                AlignCollectBottomRow,
                CollectBottomRow,
                MoveToShootAfterCollectBottomRow,
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
            Pose startPose = mirror(new Pose(90, 9, Math.toRadians(90)), isBlue);
            Pose shootingPose = mirror(new Pose(90, 12, Math.toRadians(65)), isBlue);
            Pose leavePose = mirror(new Pose(100, 20, Math.toRadians(0)), isBlue);

            Pose humanPlayerAlignPose = mirror(new Pose(105, 9, Math.toRadians(0)), isBlue);
            Pose humanPlayerCollectPose = mirror(new Pose(138, 9, Math.toRadians(0)), isBlue);

            Pose bottomRowAlignPose = mirror(new Pose(90, 36, Math.toRadians(0)), isBlue);
            Pose bottomRowCollectPose = mirror(new Pose(138, 36, Math.toRadians(0)), isBlue);

            MoveToShoot1 =
                    follower.pathBuilder()
                            .addPath(new BezierLine(startPose, shootingPose))
                            .setLinearHeadingInterpolation(
                                    startPose.getHeading(), shootingPose.getHeading())
                            .build();

            AlignCollectHumanPlayer =
                    follower.pathBuilder()
                            .addPath(new BezierCurve(shootingPose, humanPlayerAlignPose, leavePose))
                            .setLinearHeadingInterpolation(
                                    shootingPose.getHeading(), humanPlayerAlignPose.getHeading())
                            .build();

            CollectHumanPlayer =
                    follower.pathBuilder()
                            .addPath(new BezierLine(humanPlayerAlignPose, humanPlayerCollectPose))
                            .setLinearHeadingInterpolation(
                                    humanPlayerAlignPose.getHeading(),
                                    humanPlayerCollectPose.getHeading())
                            .build();

            MoveToShootAfterCollectHumanPlayer =
                    follower.pathBuilder()
                            .addPath(new BezierLine(humanPlayerCollectPose, shootingPose))
                            .setLinearHeadingInterpolation(
                                    humanPlayerCollectPose.getHeading(), shootingPose.getHeading())
                            .build();

            AlignCollectBottomRow =
                    follower.pathBuilder()
                            .addPath(new BezierCurve(shootingPose, bottomRowAlignPose, leavePose))
                            .setLinearHeadingInterpolation(
                                    shootingPose.getHeading(), bottomRowAlignPose.getHeading())
                            .build();

            CollectBottomRow =
                    follower.pathBuilder()
                            .addPath(new BezierLine(bottomRowAlignPose, bottomRowCollectPose))
                            .setLinearHeadingInterpolation(
                                    bottomRowAlignPose.getHeading(),
                                    bottomRowCollectPose.getHeading())
                            .build();

            MoveToShootAfterCollectBottomRow =
                    follower.pathBuilder()
                            .addPath(new BezierLine(bottomRowCollectPose, shootingPose))
                            .setLinearHeadingInterpolation(
                                    bottomRowCollectPose.getHeading(), shootingPose.getHeading())
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
