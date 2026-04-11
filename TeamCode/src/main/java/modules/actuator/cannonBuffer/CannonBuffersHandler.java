package modules.actuator.cannonBuffer;

import static config.CannonConfig.SHOOT_BALLS_AMOUNT;
import static config.CannonConfig.SHOOT_DELAY;

import modules.actuator.RobotActuatorModule;
import utils.TimeHelpers;

import java.util.HashMap;

public class CannonBuffersHandler implements RobotActuatorModule {
    private final CannonBuffer buffer;

    private ShootingStage shootingStage = ShootingStage.IDLE;

    // Duration to keep buffers on while shooting (seconds)
    private static final double SHOOT_DURATION_SEC = 0.3;

    // Timer
    private double shootingStartTime = 0;
    private int ballsShot = 0;

    public CannonBuffersHandler(CannonBuffer buffer) {
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

    public boolean shootContinue(boolean startLeft, boolean isReady) {
        double currentTime = TimeHelpers.getRuntime();

        switch (shootingStage) {
            case IDLE:
                ballsShot = 0;
                shootingStage = ShootingStage.WAITING;
                break;

            case WAITING:
                buffer.off();
                if (isReady) {
                    shootingStage = ShootingStage.SHOOTING;
                    shootingStartTime = currentTime;
                    buffer.on();
                }
                break;

            case SHOOTING:
                if (currentTime - shootingStartTime >= SHOOT_DURATION_SEC) {
                    buffer.off();
                    shootingStage = ShootingStage.WAITING;
                    ballsShot++;
                } else {
                    buffer.on();
                }
                if (ballsShot >= 2) {
                    shootingStage = ShootingStage.IDLE;
                }
        }

        return shootingStage != ShootingStage.IDLE;
    }

    public void shootReset() {
        shootingStage = ShootingStage.IDLE;
        buffer.off();
    }

    public void shootDontContinue() {
        if (shootingStage == ShootingStage.SHOOTING) {
            shootingStage = ShootingStage.WAITING;
            buffer.off();
        }
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
        return state;
    }

    @Override
    public void setState(HashMap<String, String> state) {
        throw new UnsupportedOperationException(
                "Cannon buffers handler does not support state loading.");
    }

    enum ShootingStage {
        IDLE,
        WAITING,
        SHOOTING
    }
}