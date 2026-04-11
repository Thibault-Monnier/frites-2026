package modules.actuator.cannonBuffer;

import static config.CannonConfig.SHOOT_BALLS_AMOUNT;
import static config.CannonConfig.SHOOT_DELAY;

import modules.actuator.RobotActuatorModule;

import utils.TimeHelpers;

import java.util.HashMap;

public class CannonBuffersHandler implements RobotActuatorModule {
    private final CannonBuffer buffer;

    private ShootingStage shootingStage = ShootingStage.IDLE;
    private double lastRoundStartTime = 0.0;

    private boolean lastShotLeft = true;
    private int shotsFired = 0;

    public CannonBuffersHandler(CannonBuffer buffer) {
        this.buffer = buffer;
    }

    /// Sets both buffers on.
    public void on() {
        buffer.on();
    }

    /// Sets both buffers off.
    public void off() {
        buffer.off();
    }

    /// Reverses both buffers.
    public void reverse() {
        buffer.reverse();
    }

    /// Continues current round or shoots next round if done.
    /// Returns true if the shooting sequence is finished, false otherwise.
    public boolean shootContinue(boolean startLeft, double shootDelay) {

        switch (shootingStage) {
            case IDLE:
                lastShotLeft = !startLeft;
                shootingStage = ShootingStage.SHOOTING;
            // fall through
            case SHOOTING:
                if (isRoundFinished(shootDelay)) {
                    nextShoot();
                }

                if (shotsFired >= SHOOT_BALLS_AMOUNT) both();
                else if (lastShotLeft) leftOnly();
                else rightOnly();

                return shotsFired > SHOOT_BALLS_AMOUNT;

            default:
                throw new IllegalStateException("Unexpected value: " + shootingStage);
        }
    }

    /// Continues current round or shoots next round if done.
    /// Returns true if the shooting sequence is finished, false otherwise.
    ///
    /// @param startLeft Whether to start shooting with the left or right buffer.
    public boolean shootContinue(boolean startLeft) {
        return shootContinue(startLeft, SHOOT_DELAY);
    }

    /// Continues current round or stops if done.
    public void shootDontContinue() {
        if (isRoundFinished(SHOOT_DELAY)) {
            off();
        }
    }

    private boolean isRoundFinished(double shootDelay) {
        double time = TimeHelpers.getRuntime();
        return time - lastRoundStartTime >= shootDelay;
    }

    /// Resets the shooting stage and turns both buffers off.
    public void shootReset() {
        lastRoundStartTime = 0.0;
        shootingStage = ShootingStage.IDLE;
        shotsFired = 0;
        off();
    }

    public int getShotsFired() {
        return shotsFired;
    }

    private void nextShoot() {
        lastRoundStartTime = TimeHelpers.getRuntime();
        lastShotLeft = !lastShotLeft;
        shotsFired++;
    }

    private void leftOnly() {
        buffer.on();
    }

    private void rightOnly() {
        buffer.on();
    }

    private void both() {
        buffer.on();
    }

    @Override
    public void apply() {
        buffer.apply();
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        HashMap<String, Object> state = new HashMap<>();
        state.put("leftBuffer", buffer.getCurrentState());
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
