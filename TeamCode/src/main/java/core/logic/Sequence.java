package core.logic;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.limelightvision.LLResultTypes;

import core.localization.LimelightHandler;

import java.util.List;
import java.util.Set;

/** A sequence represents a set of 3 artifacts with differing colors and ordering. */
public class Sequence {
    Artifact[] artifacts = new Artifact[3];

    public Sequence(Artifact[] artifacts) throws Exception {
        if (artifacts.length != 3) {
            throw new Exception("Sequence should be three artifacts long");
        }
    }

    /**
     * Attempts to retrieve the current sequence using the central AprilTag if it is visible.
     *
     * @return the sequence if found; otherwise {@code null}
     */
    public static Sequence findCurrentSequence(LimelightHandler limelightHandler) throws Exception {
        List<LLResultTypes.FiducialResult> tags = limelightHandler.getLastDetectedTags();
        if (tags == null || tags.isEmpty()) return null;

        for (LLResultTypes.FiducialResult tag : tags) {
            /*
             * See https://ftc-resources.firstinspires.org/ftc/game/manual, page 72
             */
            Set<Integer> allowedIds = Set.of(21, 22, 23);
            if (!allowedIds.contains(tag.getFiducialId())) continue;

            Sequence sequence = null;

            switch (tag.getFiducialId()) {
                case 21:
                    sequence =
                            new Sequence(
                                    new Artifact[] {
                                        new Artifact(Artifact.Color.GREEN),
                                        new Artifact(Artifact.Color.PURPLE),
                                        new Artifact(Artifact.Color.PURPLE)
                                    });
                    break;
                case 22:
                    sequence =
                            new Sequence(
                                    new Artifact[] {
                                        new Artifact(Artifact.Color.PURPLE),
                                        new Artifact(Artifact.Color.GREEN),
                                        new Artifact(Artifact.Color.PURPLE)
                                    });
                    break;
                case 23:
                    sequence =
                            new Sequence(
                                    new Artifact[] {
                                        new Artifact(Artifact.Color.PURPLE),
                                        new Artifact(Artifact.Color.PURPLE),
                                        new Artifact(Artifact.Color.GREEN)
                                    });
            }

            return sequence;
        }
        return null;
    }

    public String getSequenceString() {
        return artifacts[0].color.toString() + artifacts[1].color.toString() + artifacts[2].color.toString();
    }
}
