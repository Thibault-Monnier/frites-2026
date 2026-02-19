package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.TimeUnit;

import logic.Movement;
import logic.Team;

public class AutoOpMode extends OpModeBase {

    private double lastShootStartTime = 0;
    private final ElapsedTime elapsedTime = new ElapsedTime();

    public AutoOpMode(Team team) {
        super(team, true);
    }

    /*
     * =========================
     * AUTO STATES
     * =========================
     */
    private enum AutoState {
        MOVE_TO_SHOOT,
        SHOOT,

        MOVE_ROW_1,
        COLLECT_ROW_1,

        MOVE_ROW_2,
        COLLECT_ROW_2,

        MOVE_ROW_3,
        COLLECT_ROW_3,

        DONE
    }

    private AutoState state;

    @Override
    public void runOpMode() {
        initialize();
        waitForStart();

        cannon.off();

        runStart();
        startAuto();

        while (opModeIsActive()) {
            runStep();        // keep robot updating
            runAutoLogic();   // progress auto
        }
    }

    @Override
    protected void runStart() {
        super.runStart();
        cannon.on();
    }

    private void startAuto() {
        state = AutoState.MOVE_TO_SHOOT;
        move.initMoveToShoot();
    }

    /*
     * =========================
     * AUTO LOGIC
     * =========================
     */
    private void runAutoLogic() {

        // always execute macro if one is active
        boolean macroDone = move.executeActiveMacro();

        globalTelemetry.addData("Is macro done?", macroDone);

        switch (state) {

            /*
             * MOVE TO SHOOT POSITION
             */
            case MOVE_TO_SHOOT:
                cannonBuffers.shootDontContinue();
                cannonBuffers.shootReset();
                if (macroDone) {
                    state = AutoState.SHOOT;
                    lastShootStartTime = elapsedTime.time(TimeUnit.SECONDS);
                }
                break;

            /*
             * SHOOT THREE
             */
            case SHOOT:

                if (!cannon.isReadyToShoot())
                    break;

                intake.on();

                if (cannonBuffers.shootContinue(true) && elapsedTime.time(TimeUnit.SECONDS) - lastShootStartTime >= 2) {
                    intake.off();

                    if (stateAfterShoot == 1) {
                        state = AutoState.MOVE_ROW_1;
                        move.initMacro(Movement.Macro.MOVE_TO_FIRST_ARTIFACT_ROW);
                    } else if (stateAfterShoot == 2) {
                        state = AutoState.MOVE_ROW_2;
                        move.initMacro(Movement.Macro.MOVE_TO_SECOND_ARTIFACT_ROW);
                    } else if (stateAfterShoot == 3) {
                        state = AutoState.MOVE_ROW_3;
                        move.initMacro(Movement.Macro.MOVE_TO_THIRD_ARTIFACT_ROW);
                    } else {
                        state = AutoState.DONE;
                    }

                    stateAfterShoot++;
                }
                break;

            /*
             * ROW 1
             */
            case MOVE_ROW_1:
                if (macroDone) {
                    intake.on();
                    move.initMacro(Movement.Macro.COLLECT_FIRST_ARTIFACT_ROW);
                    state = AutoState.COLLECT_ROW_1;
                }
                break;

            case COLLECT_ROW_1:
                if (macroDone) {
                    intake.off();
                    move.initMoveToShoot();
                    state = AutoState.MOVE_TO_SHOOT;
                }
                break;

            /*
             * ROW 2
             */
            case MOVE_ROW_2:
                if (macroDone) {
                    intake.on();
                    move.initMacro(Movement.Macro.COLLECT_SECOND_ARTIFACT_ROW);
                    state = AutoState.COLLECT_ROW_2;
                }
                break;

            case COLLECT_ROW_2:
                if (macroDone) {
                    intake.off();
                    move.initMoveToShoot();
                    state = AutoState.MOVE_TO_SHOOT;
                }
                break;

            /*
             * ROW 3
             */
            case MOVE_ROW_3:
                if (macroDone) {
                    cannonBuffers.shootDontContinue();
                    intake.on();
                    move.initMacro(Movement.Macro.COLLECT_THIRD_ARTIFACT_ROW);
                    state = AutoState.COLLECT_ROW_3;
                }
                break;

            case COLLECT_ROW_3:
                if (macroDone) {
                    intake.off();
                    state = AutoState.MOVE_TO_SHOOT;
                }
                break;

            case DONE:
                break;
        }
    }

    private int stateAfterShoot = 1;

    /*
     * =========================
     * HIGH FREQUENCY LOOP
     * =========================
     */
    private void runStep() {
        update();

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("State", state);

        apply();

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
        log();
    }
}
