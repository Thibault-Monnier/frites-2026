package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.roadrunner.PinpointLocalizer;

public class OdometryHandler {
    OdometryHandler instance;

    PinpointLocalizer localizer;

    OdometryHandler getInstance(HardwareMap hardwareMap) {
        if (instance == null) {
            instance = new OdometryHandler(hardwareMap);
        }
        return instance;
    }

    private OdometryHandler(HardwareMap hardwareMap) {
        localizer = new PinpointLocalizer(hardwareMap, 0.00198, new Pose2d(0, 0, 0));
    }

    public void update() {
        localizer.update();
    }

    public Pose2d getPose() {
        return localizer.getPose();
    }
}
