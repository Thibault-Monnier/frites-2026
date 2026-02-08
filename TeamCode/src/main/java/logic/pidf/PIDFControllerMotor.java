package logic.pidf;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PIDFControllerMotor extends PIDFController {
    private final DcMotorEx motor;
    private final double maxMotorVelocity;

    public PIDFControllerMotor(DcMotorEx motor, double maxMotorVelocity, Telemetry telemetry) {
        super(telemetry);
        this.motor = motor;
        this.maxMotorVelocity = maxMotorVelocity;
    }

    public PIDFControllerMotor(
            DcMotorEx motor,
            double maxMotorVelocity,
            Telemetry telemetry,
            PIDFCoefficients initialCoeffs) {
        super(telemetry, initialCoeffs);
        this.motor = motor;
        this.maxMotorVelocity = maxMotorVelocity;
    }

    /// Calculates the PID output for the saved error. The output is normalized to \[-1, 1\].
    public double get(double targetVelocity, boolean debugInfo) {
        if (Math.abs(targetVelocity) < maxMotorVelocity / 100.0
                && Math.abs(error) < maxMotorVelocity / 100.0) {
            return 0.0;
        }

        double error = motor.getVelocity() - targetVelocity;
        setError(error);

        if (debugInfo) {
            telemetry.addData("Target Velocity", targetVelocity);
            telemetry.addData("Current Velocity", motor.getVelocity());
        }

        return super.get(debugInfo);
    }

    /// Calculates the PID output for the saved error. The output is normalized to \[-1, 1\].
    public double get(double targetVelocity) {
        return get(targetVelocity, false);
    }
}
