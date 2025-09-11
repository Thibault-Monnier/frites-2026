package org.firstinspires.ftc.teamcode.robot;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.lang.reflect.Field;

public class BetterTelemetry {
    private final Telemetry telemetry;

    public BetterTelemetry(Telemetry globalTelemetry) {
        this.telemetry = globalTelemetry;
    }

    public void addVariable(Object var) throws IllegalAccessException, InstantiationException {
        Class<?> objectClass = var.getClass();
        for (Field field : objectClass.getFields()) {
            field.setAccessible(true);
            if (field.get(field.getType().newInstance()) == var) {
                this.telemetry.addData(field.getName(), var.toString());
                return;
            }
        }
    }
}
