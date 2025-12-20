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

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(60, -15, Math.toRadians(180)))
                .splineTo(new Vector2d(35, -30), Math.toRadians(270))
                .strafeTo(new Vector2d(35, -40))
                .strafeTo(new Vector2d(35, -30))
                .splineTo(new Vector2d(-25, -25), Math.toRadians(45))
                // Start flywheel
                        .waitSeconds(2)
                // Shoot artifacts
                        .waitSeconds(2)
                // Go back
                .splineTo(new Vector2d(12, -30), Math.toRadians(270))
                .strafeTo(new Vector2d(12, -40))
                .strafeTo(new Vector2d(12, -30))
                .splineTo(new Vector2d(-25, -25), Math.toRadians(45))
                // Start flywheel
                .waitSeconds(2)
                // Shoot artifacts
                .waitSeconds(2)
                // Go back
                .splineTo(new Vector2d(-11, -30), Math.toRadians(270))
                .strafeTo(new Vector2d(-11, -40))
                .strafeTo(new Vector2d(-11, -30))
                .splineTo(new Vector2d(-25, -25), Math.toRadians(45))
                // Start flywheel
                .waitSeconds(2)
                // Shoot artifacts
                .waitSeconds(2)
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}