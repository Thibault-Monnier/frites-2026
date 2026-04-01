package opmodes.versions.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import logic.Team;

import opmodes.AutoOpModeFar;
import opmodes.GroupConstants;

@Autonomous(
        name = GroupConstants.MAIN_MODES_GROUP + ": FAR Auto - BLUE Team",
        group = GroupConstants.MAIN_MODES_GROUP)
public class BlueFar extends AutoOpModeFar {
    public BlueFar() {
        super(Team.BLUE);
    }
}
