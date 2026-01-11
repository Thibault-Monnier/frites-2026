package com.acmerobotics.meepmeep;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");

        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot =
                new DefaultBotBuilder(meepMeep)
                        // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track
                        // width
                        .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                        .build();

        myBot.runAction(
                myBot.getDrive()
                        .actionBuilder(new Pose2d(60, -15, Math.toRadians(180))) // Start pose
                        .splineToLinearHeading(
                                new Pose2d(35, -30, Math.toRadians(270)), Math.toRadians(270))
                        .strafeTo(new Vector2d(35, -52))
                        .waitSeconds(1)
                        .strafeTo(new Vector2d(35, -30))
                        .splineToLinearHeading(
                                new Pose2d(-10, -10, Math.toRadians(-135)), Math.toRadians(-135)) // Shooting position
                        // Shoot artifacts
                        .waitSeconds(2.5)
                        // Go back
                        .splineToLinearHeading(
                                new Pose2d(12, -30, Math.toRadians(270)), Math.toRadians(270))
                        .strafeTo(new Vector2d(12, -52))
                        .waitSeconds(1)
                        .strafeTo(new Vector2d(12, -30))
                        .splineToLinearHeading(new Pose2d(-10, -10, Math.toRadians(-135)), Math.toRadians(-135))
                        // Shoot artifacts
                        .waitSeconds(2.5)
                        // Go back
                        .splineToLinearHeading(new Pose2d( -11, -30, Math.toRadians(270)), Math.toRadians(270))
                        .strafeTo(new Vector2d(-11, -52))
                        .waitSeconds(1)
                        .strafeTo(new Vector2d(-11, -30))
                        .splineToLinearHeading(new Pose2d(-10, -10, Math.toRadians(-135)), Math.toRadians(45))
                        // Shoot artifacts
                        .waitSeconds(2.5)
                        .splineToLinearHeading(
                                new Pose2d(12, -12, Math.toRadians(270)), Math.toRadians(270))
                        .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
