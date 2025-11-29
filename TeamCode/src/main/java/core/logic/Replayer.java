package core.logic;

import android.os.Environment;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import core.modules.RobotModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Replayer {
    private static final String DIRECTORY_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/FIRST/replays/";
    private static final String SEPARATOR = ",";
    private static final String TIME_IDENTIFIER = "time";

    private static String[] splitLine(String line) {
        return line.split(SEPARATOR);
    }

    public static class ReplayerMode extends OpMode {
        private final String replayFile;
        public RobotModule[] robotModules;
        public Telemetry globalTelemetry;
        private double orderId = 0;
        private Scanner reader;
        private HashMap<Integer, String> dictionary;
        private boolean lastExecuted;
        private double lastReplayStamp;
        private double lastDelay;
        private HashMap<String, String> lastState;

        public ReplayerMode(String replayFile) {
            this.replayFile = replayFile;
        }

        public void open(String fileName) {
            try {
                File destFile = new File(DIRECTORY_PATH + fileName);
                reader = new Scanner(destFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            String[] segmentedLine = splitLine(reader.nextLine());

            dictionary = new HashMap<>();
            for (int i = 0; i < segmentedLine.length; i++) {
                dictionary.put(i, segmentedLine[i]);
            }
        }

        public String next() {
            if (!reader.hasNextLine()) {
                return null;
            }
            return reader.nextLine();
        }

        private HashMap<String, String> hashLine(String line) {
            String[] segmentedLine = splitLine(line);
            HashMap<String, String> state = new HashMap<>();
            for (int i = 0; i < segmentedLine.length; i++) {
                state.put(dictionary.get(i), segmentedLine[i]);
            }
            return state;
        }

        @Override
        public void init() {
            open(this.replayFile);

            globalTelemetry =
                    new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        }

        public void start() {
            open(replayFile);

            lastExecuted = true;

            resetRuntime();
        }

        @Override
        public void loop() {
            if (lastExecuted) {
                String line = next();

                if (line != null) {
                    lastState = hashLine(line);
                    lastReplayStamp =
                            Double.parseDouble(
                                    Objects.requireNonNull(lastState.get(TIME_IDENTIFIER)));

                    lastDelay = getRuntime() - lastReplayStamp;

                    lastExecuted = false;
                }
            }

            if (!lastExecuted) {
                if (getRuntime() >= lastReplayStamp) {
                    lastExecuted = true;
                    orderId += 1;
                }

                for (RobotModule module : robotModules) {
                    module.setState(lastState);
                    module.apply();
                }
            }

            /* TELEMETRY */
            globalTelemetry.addLine("--- Replay Mode ---");
            globalTelemetry.addData("Orders executed", orderId);
            globalTelemetry.addData("Current replay runtime", lastReplayStamp);
            globalTelemetry.addData("Last delay", lastDelay);
        }
    }

    public static class Logger {
        private final List<String> logs;
        private final RobotModule[] robotModules;
        private final ElapsedTime globalRuntime;

        public Logger(ElapsedTime globalRime, RobotModule[] robotModules) {
            logs = new ArrayList<>();

            this.robotModules = robotModules;
            this.globalRuntime = globalRime;

            StringBuilder line = new StringBuilder();
            line.append(TIME_IDENTIFIER).append(SEPARATOR);
            for (RobotModule module : this.robotModules) {
                for (Object keys : module.getCurrentState().keySet()) {
                    line.append(keys.toString()).append(SEPARATOR);
                }
            }
            line.setLength(line.length() - 1);
            logLine(line.toString());
        }

        private void logLine(String line) {
            logs.add(line);
        }

        public void logCurrentState() {
            StringBuilder line = new StringBuilder();
            line.append(globalRuntime.time()).append(SEPARATOR);
            for (RobotModule module : this.robotModules) {
                for (Object obj : module.getCurrentState().values()) {
                    line.append(obj.toString()).append(SEPARATOR);
                }
            }
            line.setLength(line.length() - 1);
            logLine(line.toString());
        }

        public void saveAndExit(String destinationFile) {
            try {
                PrintWriter writer = new PrintWriter(DIRECTORY_PATH + destinationFile);
                writer.print("");

                for (String line : logs) {
                    writer.println(line);
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
