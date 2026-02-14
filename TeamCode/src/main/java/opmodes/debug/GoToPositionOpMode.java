package opmodes.debug;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import logic.Team;
import logic.field.PlayingField;

import math.Distance;
import math.Position2D;

import modules.sensor.GamepadController;

import opmodes.GroupConstants;
import opmodes.OpModeBase;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Random;

@TeleOp(
        name = GroupConstants.DEBUGGER_MODES_GROUP + ": Go To Position",
        group = GroupConstants.DEBUGGER_MODES_GROUP)
public class GoToPositionOpMode extends OpModeBase {
    private static final Distance MIN_WALL_DISTANCE = new Distance(DistanceUnit.CM, 50);

    private final Random random = new Random();

    private Position2D targetPosition;

    public GoToPositionOpMode() {
        super(Team.BLUE, true);
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
    }

    private void runStep() {
        update();
        move.reloadPIDFCoefficients();

        executeActions();

        apply();
        log();
    }

    private void executeActions() {
        if (targetPosition == null || gamepadController.isPressed(GamepadController.Button.A)) {
            targetPosition = pickRandomTarget();
        }

        boolean isTranslating = move.translateToPosition(targetPosition);
        globalTelemetry.addData("Translating", isTranslating);

        Position2D currentPosition = robotPosition.getPosition();
        globalTelemetry.addData("Target", targetPosition);
        globalTelemetry.addData("Position", currentPosition);
        globalTelemetry.addData("Team", team);
        globalTelemetry.addData("Distance Error", targetPosition.distanceTo(currentPosition));
    }

    private Position2D pickRandomTarget() {
        double rangeRadius =
                PlayingField.FIELD.halfWidth().subtract(MIN_WALL_DISTANCE).toMillimeters();
        double x = randomInRange(-rangeRadius, rangeRadius);
        double y = randomInRange(-rangeRadius, rangeRadius);
        return new Position2D(Distance.fromMillimeters(x), Distance.fromMillimeters(y));
    }

    private double randomInRange(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }
}
