package logic;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import math.TimeHelpers;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PIDFController {
    private final Telemetry telemetry;

    private final DcMotorEx motor;
    private final double maxMotorVelocity;

    private PIDFCoefficients coefficients = new PIDFCoefficients(0.0, 0.0, 0.0, 0.0);

    public PIDFController(DcMotorEx motor, double maxMotorVelocity, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.motor = motor;
        this.maxMotorVelocity = maxMotorVelocity;
    }

    public PIDFController(
            DcMotorEx motor,
            double maxMotorVelocity,
            Telemetry telemetry,
            PIDFCoefficients initialCoeffs) {
        this.telemetry = telemetry;
        this.motor = motor;
        this.maxMotorVelocity = maxMotorVelocity;
        this.coefficients = initialCoeffs;
    }

    /// Sets the PID coefficients.
    public void setCoefficients(PIDFCoefficients coeffs) {
        this.coefficients = coeffs;
    }

    private double lastTime = TimeHelpers.getRuntime();
    private double integral = 0.0;
    private double previousError = 0.0;

    /// Calculates the PID output for the given target velocity.
    /// The output is normalized in \[-1, 1\] and should be used in a setPower.
    public double get(double targetVelocity, boolean debugInfo) {
        if (Math.abs(targetVelocity) < maxMotorVelocity / 100.0) {
            return 0.0;
        }

        double currentTime = TimeHelpers.getRuntime();
        double deltaTime = currentTime - lastTime;
        lastTime = currentTime;
        double error = motor.getVelocity() - targetVelocity;

        if (debugInfo) {
            telemetry.addData("Current Velocity", motor.getVelocity());
            telemetry.addData("Target Velocity", targetVelocity);
            telemetry.addData("Error", error);
        }

        integral += error * deltaTime;
        double derivative = (error - previousError) / deltaTime;

        double pTerm = coefficients.Kp * error;
        double iTerm = coefficients.Ki * integral;
        double dTerm = coefficients.Kd * derivative;
        double fTerm = coefficients.Kf;

        previousError = error;

        double sum = pTerm + iTerm + dTerm + fTerm;

        if (debugInfo) {
            telemetry.addData("Integral", integral);
            telemetry.addData("Derivative", derivative);
            telemetry.addData("P Term", pTerm);
            telemetry.addData("I Term", iTerm);
            telemetry.addData("D Term", dTerm);
            telemetry.addData("F Term", fTerm);

            telemetry.addData("PID Output (before clamp)", sum);
        }

        return clamp(sum);
    }

    /// Calculates the PID output for the given target velocity.
    /// The output is normalized in \[-1, 1\] and should be used in a setPower.
    public double get(double targetVelocity) {
        return get(targetVelocity, false);
    }

    /// Clamps the given value to the range \[-1, 1\].
    private double clamp(double v) {
        return Math.clamp(v, -1.0, 1.0);
    }
}
