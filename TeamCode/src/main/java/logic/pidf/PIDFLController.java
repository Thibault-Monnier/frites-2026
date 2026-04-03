package logic.pidf;

import utils.TelemetryHandler;
import utils.TimeHelpers;

public class PIDFLController {
    protected PIDFLCoefficients coefficients;

    public PIDFLController() {}

    public PIDFLController(PIDFLCoefficients initialCoeffs) {
        this.coefficients = initialCoeffs;
    }

    /// Sets the PIDF coefficients.
    public void setCoefficients(PIDFLCoefficients coeffs) {
        this.coefficients = coeffs;
    }

    /// Returns the current PIDF coefficients.
    public PIDFLCoefficients getCoefficients() {
        return coefficients;
    }

    /// Returns whether the controller is stable at the target, meaning it will probably stay within
    /// the given error thresholds.
    public boolean isStableAtTarget(double errorThreshold, double errorChangeThreshold) {
        return Math.abs(error) < errorThreshold
        // && Math.abs(getErrorChange()) < errorChangeThreshold
        ;
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
            TelemetryHandler.addData("Error", error);
        }

        integral += error * deltaTime;
        double derivative = (error - previousError) / deltaTime;

        double pTerm = coefficients.Kp * error;
        double iTerm = coefficients.Ki * integral;
        double dTerm = coefficients.Kd * derivative;
        double fTerm = coefficients.Kf;
        double lTerm = coefficients.Kl * Math.signum(error);

        previousError = error;

        double sum = pTerm + iTerm + dTerm + fTerm + lTerm;

        if (debugInfo) {
            TelemetryHandler.addData("Integral", integral);
            TelemetryHandler.addData("Derivative", derivative);
            TelemetryHandler.addData("P Term", pTerm);
            TelemetryHandler.addData("I Term", iTerm);
            TelemetryHandler.addData("D Term", dTerm);
            TelemetryHandler.addData("F Term", fTerm);
            TelemetryHandler.addData("L Term", lTerm);

            TelemetryHandler.addData("PID Output (before clamp)", sum);
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
