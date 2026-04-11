package modules.actuator.cannonBuffer;

import static config.CannonConfig.SHOOT_BALLS_AMOUNT;
import static config.CannonConfig.SHOOT_DELAY;

import modules.actuator.RobotActuatorModule;

import utils.TimeHelpers;

import java.util.HashMap;

public class CannonBufferHandler implements RobotActuatorModule {
    private final CannonBuffer buffer;

    private ShootingStage shootingStage = ShootingStage.IDLE;
    private double lastRoundStartTime = 0.0;
    private int shotsFired = 0;

    public CannonBufferHandler(CannonBuffer buffer) {
        this.buffer = buffer;
    }

    public void on() {
        buffer.on();
    }

    public void off() {
        buffer.off();
    }

    public void reverse() {
        buffer.reverse();
    }

    /// Continues current round or shoots next round if done.
    /// Returns true if the shot is finished, false otherwise.
    public boolean shootContinue() {
        switch (shootingStage) {
            case IDLE:
                shootingStage = ShootingStage.SHOOTING;
            // fall through
            case SHOOTING:
                if (isRoundFinished()) nextRound();

                on();
                return shotsFired > SHOOT_BALLS_AMOUNT;

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

    private void nextRound() {
        lastRoundStartTime = TimeHelpers.getRuntime();
        shotsFired++;
    }

    @Override
    public void apply() {
        buffer.apply();
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        HashMap<String, Object> state = new HashMap<>();
        state.put("buffer", buffer.getCurrentState());
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
