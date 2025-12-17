package logic;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.limelightvision.LLResultTypes;

import modules.sensor.LimelightHandler;

import java.util.List;

/** A sequence represents a set of 3 artifacts with differing colors and ordering. */
public class Sequence {
    Artifact[] artifacts;

    public Sequence(Artifact[] artifacts) {
        if (artifacts.length != 3) {
            throw new IllegalArgumentException("A sequence must consist of exactly 3 artifacts.");
        }
        this.artifacts = artifacts;
    }

    /**
     * Attempts to retrieve the current sequence using the central AprilTag if it is visible.
     *
     * @return the sequence if found; otherwise {@code null}
     */
    public static Sequence findCurrentSequence(LimelightHandler limelightHandler) {
        List<LLResultTypes.FiducialResult> tags = limelightHandler.getLastDetectedTags();
        if (tags == null || tags.isEmpty()) return null;

        for (LLResultTypes.FiducialResult tag : tags) {
            /*
             * See https://ftc-resources.firstinspires.org/ftc/game/manual, page 72 for sequence tag ids
             */
            switch (tag.getFiducialId()) {
                case 21:
                    return new Sequence(
                            new Artifact[] {
                                new Artifact(Artifact.Color.GREEN),
                                new Artifact(Artifact.Color.PURPLE),
                                new Artifact(Artifact.Color.PURPLE)
                            });
                case 22:
                    return new Sequence(
                            new Artifact[] {
                                new Artifact(Artifact.Color.PURPLE),
                                new Artifact(Artifact.Color.GREEN),
                                new Artifact(Artifact.Color.PURPLE)
                            });
                case 23:
                    return new Sequence(
                            new Artifact[] {
                                new Artifact(Artifact.Color.PURPLE),
                                new Artifact(Artifact.Color.PURPLE),
                                new Artifact(Artifact.Color.GREEN)
                            });
            }
        }

        return null;
    }

    @NonNull
    public String toString() {
        return artifacts[0].color.toString()
                + artifacts[1].color.toString()
                + artifacts[2].color.toString();
    }
}
