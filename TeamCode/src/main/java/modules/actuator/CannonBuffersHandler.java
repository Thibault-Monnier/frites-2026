package modules.actuator;

import math.TimeHelpers;

import java.util.HashMap;

public class CannonBuffersHandler implements RobotActuatorModule {
    private final CannonBuffer leftBuffer;
    private final CannonBuffer rightBuffer;

    private ShootingStage shootingStage = ShootingStage.IDLE;
    private double lastRoundStartTime = 0.0;
    private static final double SHOOT_DELAY = 0.5; // seconds

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

        if (shootingStage == ShootingStage.IDLE) {
            if (startLeft) leftOnly();
            else rightOnly();
            shootingStage = ShootingStage.SHOT_ONCE;
        } else if (shootingStage == ShootingStage.SHOT_ONCE) {
            rightOnly();
            shootingStage = ShootingStage.SHOT_TWICE;
        } else if (shootingStage == ShootingStage.SHOT_TWICE) {
            leftOnly();
            shootingStage = ShootingStage.FINISHED;
        } else {
            off();
            return true;
        }

        return false;
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
        shootingStage = ShootingStage.IDLE;
        off();
    }

    private void leftOnly() {
        leftBuffer.on();
        rightBuffer.off();
    }

    private void rightOnly() {
        leftBuffer.off();
        rightBuffer.on();
    }

    @Override
    public void apply() {
        leftBuffer.apply();
        rightBuffer.apply();
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        throw new UnsupportedOperationException(
                "Cannon buffers handler does not support state saving.");
    }

    @Override
    public void setState(HashMap<String, String> state) {
        throw new UnsupportedOperationException(
                "Cannon buffers handler does not support state loading.");
    }

    enum ShootingStage {
        IDLE,
        SHOT_ONCE,
        SHOT_TWICE,
        FINISHED
    }
}
