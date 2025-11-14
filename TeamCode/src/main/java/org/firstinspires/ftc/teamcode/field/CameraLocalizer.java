package org.firstinspires.ftc.teamcode.field;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.Constants;

import java.util.List;

public class CameraLocalizer {
    private final Telemetry telemetry;
    private final HardwareMap hardwareMap;

    private Limelight3A limelight;

    public CameraLocalizer(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
    }

    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, Constants.LIMELIGHT_CAMERA_ID);
        limelight.pipelineSwitch(0);
    }

    public void start() {
        limelight.start();
    }

    public void stop() {
        limelight.stop();
    }

    public void localize() {
        LLResult result = limelight.getLatestResult();

        if (result != null) {
            if (result.isValid()) {
                telemetry.addLine("--- Camera Localization ---");

                List<LLResultTypes.FiducialResult> tags = result.getFiducialResults();

                telemetry.addData("# of tags", tags.size());

                for (LLResultTypes.FiducialResult tag : tags) {
                    telemetry.addData("ID", tag.getFiducialId());
                    telemetry.addData("tx deg", tag.getTargetXDegrees());
                    telemetry.addData("ty deg", tag.getTargetYDegrees());
                    telemetry.addData("pose", tag.getRobotPoseFieldSpace().toString());
                }
            } else {
                telemetry.addLine("Nothing detected");
            }
        }
    }
}
