package org.firstinspires.ftc.teamcode.debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.Constants;

@TeleOp(
        name = Constants.DEBUGGER_MODES_GROUP + ": Husky Lens",
        group = Constants.DEBUGGER_MODES_GROUP)
public class HuskyLensDebugger extends OpMode {
    private static final HuskyLens.Algorithm algorithm = HuskyLens.Algorithm.TAG_RECOGNITION;

    private HuskyLens huskyLens;

    private Telemetry globalTelemetry;

    @Override
    public void init() {
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        huskyLens = hardwareMap.get(HuskyLens.class, "husky_lens");
        huskyLens.resetDeviceConfigurationForOpMode();
        huskyLens.selectAlgorithm(algorithm);
    }

    @Override
    public void loop() {
        globalTelemetry.addLine("--- TAGS ---");
        for (HuskyLens.Block block : huskyLens.blocks()) {
            globalTelemetry.addLine("{");
            globalTelemetry.addData("   Id:", block.id);
            globalTelemetry.addData("   Pos:", block.x + ", " + block.y);
            globalTelemetry.addData("   Dimensions:", block.width + "x" + block.height);
            globalTelemetry.addData("   Top left corner:", block.left + ", " + block.top);
            globalTelemetry.addLine("}");
        }
        globalTelemetry.update();
    }
}
