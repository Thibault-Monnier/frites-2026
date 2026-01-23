package logic;

import math.TimeHelpers;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PIDFController {
    protected final Telemetry telemetry;

    protected PIDFCoefficients coefficients = new PIDFCoefficients(0.0, 0.0, 0.0, 0.0);

    public PIDFController(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public PIDFController(Telemetry telemetry, PIDFCoefficients initialCoeffs) {
        this.telemetry = telemetry;
        this.coefficients = initialCoeffs;
    }

    /// Sets the PID coefficients.
    public void setCoefficients(PIDFCoefficients coeffs) {
        this.coefficients = coeffs;
    }

    protected double lastTime = TimeHelpers.getRuntime();
    protected double integral = 0.0;
    protected double previousError = 0.0;

    protected double error = 0.0;

    public void setError(double error) {
        this.error = error;
    }

    /// Returns the change in error for the last two frames.
    public double getErrorChange() {
        return error - previousError;
    }

    /// Calculates the PID output for the saved error. The output is normalized to \[-1, 1\].
    public double get(boolean debugInfo) {
        double currentTime = TimeHelpers.getRuntime();
        double deltaTime = currentTime - lastTime;
        lastTime = currentTime;

        if (debugInfo) {
            telemetry.addData("Error", error);
        }

        integral += error * deltaTime;
        double derivative = (error - previousError) / deltaTime;

        double pTerm = coefficients.Kp * error;
        double iTerm = coefficients.Ki * integral;
        double dTerm = coefficients.Kd * derivative;
        double fTerm = coefficients.Kf;

        if (Double.isNaN(pTerm)
                || Double.isNaN(iTerm)
                || Double.isNaN(dTerm)
                || Double.isNaN(fTerm)) {
            throw new RuntimeException(
                    "PIDF calculation resulted in NaN "
                            + "(P: "
                            + pTerm
                            + ", I: "
                            + iTerm
                            + ", D: "
                            + dTerm
                            + ", F: "
                            + fTerm
                            + "); deltaTime: "
                            + deltaTime);
        }

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

    /// Calculates the PID output for the saved error. The output is normalized to \[-1, 1\].
    public double get() {
        return get(false);
    }

    /// Clamps the given value to the range \[-1, 1\].
    protected double clamp(double v) {
        return Math.clamp(v, -1.0, 1.0);
    }
}
