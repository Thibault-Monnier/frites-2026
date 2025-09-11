package org.firstinspires.ftc.teamcode.utils;

import static org.firstinspires.ftc.teamcode.robot.Constants.CLAMP_CLOSED_POSITION;
import static org.firstinspires.ftc.teamcode.robot.Constants.CLAMP_OPEN_POSITION;
import static org.firstinspires.ftc.teamcode.robot.Constants.CLAMP_SERVO_ID;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.Constants;
import org.firstinspires.ftc.teamcode.robot.ServoClamp;

@Config
@TeleOp(name = Constants.DEBUGGER_MODES_GROUP + ": Arm Re-setter", group = Constants.DEBUGGER_MODES_GROUP)
public class ArmResetter extends OpMode {
    private static final double POWER = 0.3;

    private Telemetry globalTelemetry;
    private ServoClamp basket;
    private DcMotor armMotor;
    private ColorSensor colorSensor;

    @Override
    public void init() {
        globalTelemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        basket = new ServoClamp(
                hardwareMap.get(Servo.class, CLAMP_SERVO_ID),
                CLAMP_OPEN_POSITION,
                CLAMP_CLOSED_POSITION,
                "clamp_servo"
        );

        armMotor = hardwareMap.get(DcMotor.class, Constants.ARM_MOTOR_ID);

        armMotor.resetDeviceConfigurationForOpMode();
        armMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        colorSensor = hardwareMap.get(ColorSensor.class, Constants.ARM_COLOR_SENSOR_ID);
        colorSensor.resetDeviceConfigurationForOpMode();
    }

    public void start() {
        basket.open();
    }

    @Override
    public void loop() {
        globalTelemetry.addLine("--- Re setter ---");

        double alpha = colorSensor.alpha();

        telemetry.addData("Color Sensor Alpha", alpha);
        if (alpha > 2000) {
            globalTelemetry.addLine("=> Color sensor detected white, resetting arm...");
            armMotor.setPower(POWER);
        } else {
            globalTelemetry.addLine("=> Color sensor detected black, stopping arm...");
            armMotor.setPower(0);
        }
        globalTelemetry.update();
    }

}
