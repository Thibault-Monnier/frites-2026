package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.robot.DriveActions;

public class MainLinearAutoOpMode extends LinearOpMode {

    private final State currentState = State.INIT;
    private DriveActions drive;

    @Override
    public void runOpMode() throws InterruptedException {
        // INIT

        drive = new DriveActions(new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0)));

        Actions.runBlocking(new SequentialAction());
    }

    public enum State {
        // Obs. = Observation
        // Sub. = Submersible

        // TRAJECTORIES
        START_TO_SUB,
        SUB_TO_OBS,
        OBS_TO_SUB,
        SUB_TO_OBS_3_SAMPLE,

        // ACTIONS
        HANG_SPECIMEN,
        TAKE_SPECIMEN,

        // OTHER
        INIT,
        WAIT,
        IDLE,
        ERROR
    }
}
