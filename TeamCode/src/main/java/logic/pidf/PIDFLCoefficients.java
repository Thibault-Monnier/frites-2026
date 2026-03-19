package logic.pidf;

import androidx.annotation.NonNull;

import java.util.Locale;

public class PIDFLCoefficients {
    public double Kp;
    public double Ki;
    public double Kd;
    public double Kf;
    public double Kl;

    public PIDFLCoefficients(double Kp, double Ki, double Kd, double Kf, double Kl) {
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
        this.Kf = Kf;
        this.Kl = Kl;
    }

    @NonNull
    public String toString() {
        return String.format(
                Locale.ENGLISH,
                "Kp: %.4f, Ki: %.4f, Kd: %.4f, Kf: %.4f, Kl: %.4f",
                Kp,
                Ki,
                Kd,
                Kf,
                Kl);
    }
}
