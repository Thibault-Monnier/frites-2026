package org.firstinspires.ftc.teamcode.test;

import static org.firstinspires.ftc.teamcode.robot.Constants.BASKET_CLOSED_POSITION;
import static org.firstinspires.ftc.teamcode.robot.Constants.BASKET_OPEN_POSITION;
import static org.firstinspires.ftc.teamcode.robot.Constants.PLASTIC_CLAMP_CLOSED_POSITION;
import static org.firstinspires.ftc.teamcode.robot.Constants.PLASTIC_CLAMP_OPEN_POSITION;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.models.RobotModule;
import org.firstinspires.ftc.teamcode.robot.Arm;
import org.firstinspires.ftc.teamcode.robot.Constants;
import org.firstinspires.ftc.teamcode.robot.GamepadController;
import org.firstinspires.ftc.teamcode.robot.Movement;
import org.firstinspires.ftc.teamcode.robot.Replayer;
import org.firstinspires.ftc.teamcode.robot.ServoClamp;

@Config
@Autonomous(name = Constants.TEST_MODES_GROUP + ": Replayer", group = Constants.TEST_MODES_GROUP)
public class ModuleReplayer extends Replayer.ReplayerMode {
    private Movement move;
    private Arm arm;

    private ElapsedTime runtime;
    private Telemetry globalTelemetry;

    public ModuleReplayer() {
        super(Constants.REPLAY_FILE_DEST);
    }

    @Override
    public void init() {
        super.init();

        runtime = new ElapsedTime();

        globalTelemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        GamepadController gamepad = new GamepadController(
                globalTelemetry,
                runtime,
                gamepad1
        );

        IMU onBoardIMU = hardwareMap.get(IMU.class, Constants.IMU_ID);

        move = new Movement(
                globalTelemetry,
                runtime,
                hardwareMap.get(DcMotor.class, Constants.FRONT_LEFT_MOTOR_ID),
                hardwareMap.get(DcMotor.class, Constants.FRONT_RIGHT_MOTOR_ID),
                hardwareMap.get(DcMotor.class, Constants.BACK_LEFT_MOTOR_ID),
                hardwareMap.get(DcMotor.class, Constants.BACK_RIGHT_MOTOR_ID),
                onBoardIMU
        );

        arm = new Arm(
                globalTelemetry,
                hardwareMap.get(DcMotor.class, Constants.ELEVATOR_MOTOR_ID),
                hardwareMap.get(DcMotor.class, Constants.ARM_MOTOR_ID),
                new ServoClamp(hardwareMap.get(Servo.class, Constants.CLAMP_SERVO_ID), PLASTIC_CLAMP_OPEN_POSITION, PLASTIC_CLAMP_CLOSED_POSITION, Constants.PLASTIC_CLAMP_ID),
                new ServoClamp(hardwareMap.get(Servo.class, Constants.BASKET_SERVO_ID), BASKET_OPEN_POSITION, BASKET_CLOSED_POSITION, Constants.BASKET_ID),
                hardwareMap.get(Servo.class, Constants.ROTATION_SERVO_ID)
        );
        arm.tryResetMotors();
        arm.setColorSensor(null, Arm.ColorSensorMode.NO_DETECTION);

        this.robotModules = new RobotModule[]{
                this.move,
                this.arm
        };
    }

    public void loop() {
        move.reset();

        super.loop();
    }
}
