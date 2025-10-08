package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.models.Clamp;

import java.util.HashMap;

public class ServoClamp implements Clamp {
    private final String STATE_KEY_EXTENSION = "State";

    private final Servo servo;
    private final double openPos;
    private final double closedPos;

    private final String key;

    private State state;

    public ServoClamp(Servo servo, double openPos, double closedPos, String id) {
        this.servo = servo;
        this.openPos = openPos;
        this.closedPos = closedPos;

        this.key = id + STATE_KEY_EXTENSION;

        this.servo.resetDeviceConfigurationForOpMode();

        this.close();
        //        this.apply();
    }

    @Override
    public void setState(HashMap<String, String> state) {
        this.state = State.valueOf(state.get(key));
    }

    @Override
    public void apply() {
        if (state == State.OPEN) {
            servo.setPosition(openPos);
        } else {
            servo.setPosition(closedPos);
        }
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        return new HashMap<String, Object>() {
            {
                put(key, state);
            }
        };
    }

    @Override
    public void open() {
        state = State.OPEN;
    }

    @Override
    public void close() {
        state = State.CLOSED;
    }

    @Override
    public String getStateKey() {
        return this.key;
    }
}
