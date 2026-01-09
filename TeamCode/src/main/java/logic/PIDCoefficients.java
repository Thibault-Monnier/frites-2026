package logic;

import androidx.annotation.NonNull;

public class PIDCoefficients {
    public double Kp;
    public double Ki;
    public double Kd;

    public PIDCoefficients(double Kp, double Ki, double Kd) {
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
    }

    @NonNull
    public String toString() {
        return String.format("Kp: %.4f, Ki: %.4f, Kd: %.4f", Kp, Ki, Kd);
    }
}
