package modules.actuator.drive;

import static config.MovementConfig.BACK_LEFT_COEFF;
import static config.MovementConfig.BACK_RIGHT_COEFF;
import static config.MovementConfig.FRONT_LEFT_COEFF;
import static config.MovementConfig.FRONT_RIGHT_COEFF;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import modules.actuator.RobotActuatorModule;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.Objects;

public class MecanumDrive implements RobotActuatorModule {
    private final Telemetry globalTelemetry;

    private final DcMotor frontLeftDrive;
    private final DcMotor frontRightDrive;
    private final DcMotor backLeftDrive;
    private final DcMotor backRightDrive;

    private double frontLeftPower = 0;
    private double frontRightPower = 0;
    private double backLeftPower = 0;
    private double backRightPower = 0;

    public MecanumDrive(Telemetry globalTelemetry, DcMotor FL, DcMotor FR, DcMotor BL, DcMotor BR) {
        this.globalTelemetry = globalTelemetry;
        this.frontLeftDrive = FL;
        this.frontRightDrive = FR;
        this.backLeftDrive = BL;
        this.backRightDrive = BR;

        frontLeftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public static double smooth(double input) {
        if (Math.abs(input) < 0.1) {
            return 0;
        } else if (Math.abs(input) >= 0.1 && Math.abs(input) < 0.7) {
            return 0.83 * input + 0.02;
        } else if (Math.abs(input) >= 0.7 && Math.abs(input) < 0.9) {
            return 2 * input - 0.8;
        } else if (Math.abs(input) >= 0.9) {
            return Math.signum(input); // full speed
        }
        return 0;
    }

    public void move(double forward, double strafe, double turnSpeed) {
        double denominator =
                Math.max(Math.abs(forward) + Math.abs(strafe) + Math.abs(turnSpeed), 1);

        frontLeftPower += (forward - strafe + turnSpeed) / denominator;
        frontRightPower += (forward + strafe - turnSpeed) / denominator;
        backLeftPower += (forward + strafe + turnSpeed) / denominator;
        backRightPower += (forward - strafe - turnSpeed) / denominator;

        frontLeftPower *= FRONT_LEFT_COEFF;
        frontRightPower *= FRONT_RIGHT_COEFF;
        backLeftPower *= BACK_LEFT_COEFF;
        backRightPower *= BACK_RIGHT_COEFF;
    }

    /// Applies the computed motor powers to the motors, then resets them.
    public void apply() {
        frontLeftDrive.setPower(Math.clamp(frontLeftPower, -1.0, 1.0));
        frontRightDrive.setPower(Math.clamp(frontRightPower, -1.0, 1.0));
        backLeftDrive.setPower(Math.clamp(backLeftPower, -1.0, 1.0));
        backRightDrive.setPower(Math.clamp(backRightPower, -1.0, 1.0));

        globalTelemetry.addLine("--- MECANUM DRIVE ---");
        globalTelemetry.addData("Front Left", frontLeftDrive.getPower());
        globalTelemetry.addData("Front Right", frontRightDrive.getPower());
        globalTelemetry.addData("Back Left", backLeftDrive.getPower());
        globalTelemetry.addData("Back Right", backRightDrive.getPower());

        reset();
    }

    public boolean isMoving() {
        return Math.max(
                        Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                        Math.max(Math.abs(backLeftPower), Math.abs(backRightPower)))
                > 0.05;
    }

    private void reset() {
        frontLeftPower = 0;
        frontRightPower = 0;
        backLeftPower = 0;
        backRightPower = 0;
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        return new HashMap<String, Object>() {
            {
                put("frontLeftPower", frontLeftPower);
                put("frontRightPower", frontRightPower);
                put("backLeftPower", backLeftPower);
                put("backRightPower", backRightPower);
            }
        };
    }

    @Override
    public void setState(HashMap<String, String> state) {
        frontLeftPower = Double.parseDouble(Objects.requireNonNull(state.get("frontLeftPower")));
        frontRightPower = Double.parseDouble(Objects.requireNonNull(state.get("frontRightPower")));
        backLeftPower = Double.parseDouble(Objects.requireNonNull(state.get("backLeftPower")));
        backRightPower = Double.parseDouble(Objects.requireNonNull(state.get("backRightPower")));
    }
}
