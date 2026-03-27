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
            // FYI: Colors are RBG
            Color red = new Color((byte) 255, (byte) 0, (byte) 0);
            Color yellow = new Color((byte) 255, (byte) 0, (byte) 255);
            Color green = new Color((byte) 0, (byte) 0, (byte) 255);
            Color off = new Color((byte) 0, (byte) 0, (byte) 0);

            int artifacts = distanceSensorMonitor.getNumberOfArtifactsInRobot();
            int ledsToLight = artifacts * 8;

            for (int i = 0; i < 24; i++) {
                if (i < ledsToLight) {
                    Color color;

                    if (i < 8)
                        color = red;
                    else if (i < 16)
                        color = yellow;
                    else
                        color = green;

                    distanceSensorMonitor.neoPixel.setLeds(i, color);
                } else {
                    distanceSensorMonitor.neoPixel.setLeds(i, off);
                }
            }

            distanceSensorMonitor.neoPixel.show();

            try {
                Thread.sleep(50); // 20 Hz I2C loop
            } catch (InterruptedException e) {}
        }
    }
}
