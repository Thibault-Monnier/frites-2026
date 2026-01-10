package logic;

import androidx.annotation.NonNull;

public class PIDFCoefficients {
    public double Kp;
    public double Ki;
    public double Kd;
    public double Kf;

    public PIDFCoefficients(double Kp, double Ki, double Kd, double Kf) {
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
        this.Kf = Kf;
    }

    @NonNull
    public String toString() {
        return String.format("Kp: %.4f, Ki: %.4f, Kd: %.4f, Kf: %.4f", Kp, Ki, Kd, Kf);
    }
}
