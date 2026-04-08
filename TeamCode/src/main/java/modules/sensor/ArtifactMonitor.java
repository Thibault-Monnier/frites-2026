package modules.sensor;

import team.techtigers.core.display.Color;

public class ArtifactMonitor implements Runnable {
    private final DistanceSensorMonitor distanceSensorMonitor;
    private volatile boolean running = true;

    public ArtifactMonitor(DistanceSensorMonitor distanceSensorMonitor) {
        this.distanceSensorMonitor = distanceSensorMonitor;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            double off = 0;
            double red = 0.28;
            double orange = 0.333;
            double yellow = 0.388;
            double sage = 0.444;
            double green = 0.5;
            double azure = 0.611;
            double blue = 0.611;
            double indigo = 0.666;
            double violet = 0.722;
            double white = 1;

            int artifacts = distanceSensorMonitor.getNumberOfArtifactsInRobot();

            if (artifacts == 0) {
                distanceSensorMonitor.led.setPosition(red);
            } else if (artifacts == 1) {
                distanceSensorMonitor.led.setPosition(red);
            } else if (artifacts == 2) {
                distanceSensorMonitor.led.setPosition(red);
            } else if (artifacts == 3) {
                distanceSensorMonitor.led.setPosition(green);
            } else if (artifacts > 3) {
                distanceSensorMonitor.led.setPosition(violet);
            }

            try {
                Thread.sleep(50); // 20 Hz I2C loop
            } catch (InterruptedException e) {
            }
        }
    }
}
