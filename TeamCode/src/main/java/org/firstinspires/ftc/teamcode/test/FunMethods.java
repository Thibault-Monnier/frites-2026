package org.firstinspires.ftc.teamcode.test;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.Constants;

@Config
@TeleOp(name = Constants.TEST_MODES_GROUP + ": Fun methods", group = Constants.TEST_MODES_GROUP)
public class FunMethods extends OpMode {
    @Override
    public void init() {}

    @Override
    public void start() {
        telemetry.speak("Hello world !");
        telemetry.update();
    }

    @Override
    public void loop() {}
}
