package modules.actuator;

import static config.CannonConfig.SHOOT_BALLS_AMOUNT;
import static config.CannonConfig.SHOOT_DELAY;

import math.TimeHelpers;

import java.util.HashMap;

public class CannonBuffersHandler implements RobotActuatorModule {
    private final CannonBuffer leftBuffer;
    private final CannonBuffer rightBuffer;

    private ShootingStage shootingStage = ShootingStage.IDLE;
    private double lastRoundStartTime = 0.0;

    private boolean lastShotLeft = true;
    private int shotsFired = 0;

    public CannonBuffersHandler(CannonBuffer leftBuffer, CannonBuffer rightBuffer) {
        this.leftBuffer = leftBuffer;
        this.rightBuffer = rightBuffer;
    }

    /// Sets both buffers on.
    public void on() {
        leftBuffer.on();
        rightBuffer.on();
    }

    /// Sets both buffers off.
    public void off() {
        leftBuffer.off();
        rightBuffer.off();
    }

    /// Clears both buffers.
    public void clear() {
        leftBuffer.clear();
        rightBuffer.clear();
    }

    public void reverse() {
        leftBuffer.reverse();
        rightBuffer.reverse();
    }

    /// Continues current round or shoots next round if done.
    /// Returns true if the shooting sequence is finished, false otherwise.
    ///
    /// @param startLeft Whether to start shooting with the left or right buffer.
    public boolean shootContinue(boolean startLeft) {
        if (!isRoundFinished()) {
            return false;
        }

        lastRoundStartTime = TimeHelpers.getRuntime();

        switch (shootingStage) {
            case IDLE:
                lastShotLeft = !startLeft;
                shootingStage = ShootingStage.SHOOTING;
            // fall through
            case SHOOTING:
                nextShoot();
                return shotsFired >= SHOOT_BALLS_AMOUNT;

            default:
                throw new IllegalStateException("Unexpected value: " + shootingStage);
        }
    }

    /// Continues current round or stops if done.
    public void shootDontContinue() {
        if (isRoundFinished()) {
            off();
        }
    }

    private boolean isRoundFinished() {
        double time = TimeHelpers.getRuntime();
        return time - lastRoundStartTime >= SHOOT_DELAY;
    }

    /// Resets the shooting stage and turns both buffers off.
    public void shootReset() {
        lastRoundStartTime = 0.0;
        shootingStage = ShootingStage.IDLE;
        shotsFired = 0;
        off();
    }

    private void nextShoot() {
        if (lastShotLeft) {
            rightOnly();
        } else {
            leftOnly();
        }
        lastShotLeft = !lastShotLeft;
        shotsFired++;
    }

    private void leftOnly() {
        leftBuffer.on();
        rightBuffer.off();
    }

    private void rightOnly() {
        leftBuffer.off();
        rightBuffer.on();
    }

    private void both() {
        leftBuffer.on();
        rightBuffer.on();
    }

    @Override
    public void apply() {
        leftBuffer.apply();
        rightBuffer.apply();
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        HashMap<String, Object> state = new HashMap<>();
        state.put("leftBuffer", leftBuffer.getCurrentState());
        state.put("rightBuffer", rightBuffer.getCurrentState());
        state.put("shootingStage", shootingStage.name());
        state.put("lastRoundStartTime", lastRoundStartTime);
        return state;
    }

    @Override
    public void setState(HashMap<String, String> state) {
        throw new UnsupportedOperationException(
                "Cannon buffers handler does not support state loading.");
    }

    enum ShootingStage {
        IDLE,
        SHOOTING
    }
}
