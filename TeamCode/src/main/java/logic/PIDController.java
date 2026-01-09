package logic;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import math.TimeHelpers;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PIDController {
    private final Telemetry telemetry;

    private final DcMotorEx motor;
    private static final double MAX_MOTOR_SPEED = 2450;

    public PIDController(DcMotorEx motor, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.motor = motor;
    }

    private PIDCoefficients coefficients = new PIDCoefficients(0.0, 0.0, 0.0);

    /// Sets the PID coefficients.
    public void setCoefficients(PIDCoefficients coeffs) {
        this.coefficients = coeffs;
    }

    private double lastTime = TimeHelpers.getRuntime();
    private double integral = 0.0;
    private double previousError = 0.0;

    /// Calculates the PID output for the given target velocity.
    /// The output is normalized in \[-1, 1\] and should be used in a setPower.
    public double get(double targetVelocity, boolean debugInfo) {
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

        double pTerm = coefficients.Kp * error / MAX_MOTOR_SPEED;
        double iTerm = coefficients.Ki * integral;
        double dTerm = coefficients.Kd * derivative;

        previousError = error;

        double sum = pTerm + iTerm + dTerm;

        if (debugInfo) {
            telemetry.addData("Integral", integral);
            telemetry.addData("Derivative", derivative);
            telemetry.addData("P Term", pTerm);
            telemetry.addData("I Term", iTerm);
            telemetry.addData("D Term", dTerm);
            telemetry.addData("PID Output (before clamp)", sum);
        }

        return Math.clamp(sum, -1.0, 1.0);
    }

    /// Calculates the PID output for the given target velocity.
    /// The output is normalized in \[-1, 1\] and should be used in a setPower.
    public double get(double targetVelocity) {
        return get(targetVelocity, false);
    }
}
