package debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import robot.Constants;

@Config
@TeleOp(name = Constants.DEBUGGER_MODES_GROUP + ": Servo", group = Constants.DEBUGGER_MODES_GROUP)
public class ServoTest extends OpMode {
    public static String motorId = "rotation_servo";

    private Servo servo;
    private Telemetry globalTelemetry;

    @Override
    public void init() {
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        servo = hardwareMap.get(Servo.class, motorId);
        servo.resetDeviceConfigurationForOpMode();
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            servo.setPosition(0.0);
        } else if (gamepad1.b) {
            servo.setPosition(0.5);
        } else if (gamepad1.x) {
            servo.setPosition(.85);
        }

        globalTelemetry.addLine("--- Infos ---");
        globalTelemetry.addData("getPosition", servo.getPosition());
        globalTelemetry.addData("getController", servo.getController());
        globalTelemetry.addData("getConnectionInfo", servo.getConnectionInfo());
        globalTelemetry.addData("getPortNumber", servo.getPortNumber());
        globalTelemetry.addData("getDeviceName", servo.getDeviceName());
        globalTelemetry.addData("getDirection", servo.getDirection());
        globalTelemetry.addData("getManufacturer", servo.getManufacturer());
        globalTelemetry.addData("getVersion", servo.getVersion());
        globalTelemetry.update();
    }
}
