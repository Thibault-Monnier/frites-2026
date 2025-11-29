package debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import robot.Constants;

@Config
@TeleOp(name = Constants.DEBUGGER_MODES_GROUP + ": DcMotor", group = Constants.DEBUGGER_MODES_GROUP)
public class DcMotorDebugger extends OpMode {
    public static String motorId = Constants.TEST_MOTOR_1_ID;

    private DcMotor motor;
    private Telemetry globalTelemetry;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, motorId);

        motor.resetDeviceConfigurationForOpMode();

        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void loop() {
        globalTelemetry.addLine("--- Infos for " + motorId + " ---");
        globalTelemetry.addData("getController", motor.getController());
        globalTelemetry.addData("getMotorType", motor.getMotorType());
        globalTelemetry.addData("getConnectionInfo", motor.getConnectionInfo());
        globalTelemetry.addData("getMode", motor.getMode());
        globalTelemetry.addData("getCurrentPosition", motor.getCurrentPosition());
        globalTelemetry.addData("getPortNumber", motor.getPortNumber());
        globalTelemetry.addData("getPowerFloat", motor.getPowerFloat());
        globalTelemetry.addData("getPower", motor.getPower());
        globalTelemetry.addData("getTargetPosition", motor.getTargetPosition());
        globalTelemetry.addData("getZeroPowerBehavior", motor.getZeroPowerBehavior());
        globalTelemetry.addData("getDeviceName", motor.getDeviceName());
        globalTelemetry.addData("getDirection", motor.getDirection());
        globalTelemetry.addData("getManufacturer", motor.getManufacturer());
        globalTelemetry.addData("getVersion", motor.getVersion());
        globalTelemetry.addData("isBusy", motor.isBusy());
        globalTelemetry.update();
    }
}
