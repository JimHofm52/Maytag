package org.teamresistance.frc.util.testing;

import org.strongback.Strongback;
import org.strongback.command.CommandGroup;
import org.strongback.components.ui.FlightStick;
import org.teamresistance.frc.IO;
import org.teamresistance.frc.command.grabber.AlignGear;
import org.teamresistance.frc.command.grabber.FindGear;
import org.teamresistance.frc.command.grabber.GearExtend;
import org.teamresistance.frc.command.grabber.GearRetract;
import org.teamresistance.frc.command.grabber.GrabGear;
import org.teamresistance.frc.command.grabber.ReleaseGear;
import org.teamresistance.frc.command.grabber.RotateDown;
import org.teamresistance.frc.command.grabber.RotateUp;
import org.teamresistance.frc.subsystem.grabber.Grabber;

import static org.teamresistance.frc.util.testing.JoystickMap.RightJoystick.CLIMBER;

/**
 * @author Shreya Ravi
 */
public class GrabberTesting extends CommandTesting {
  private final Grabber grabber;

  public GrabberTesting(Grabber grabber, FlightStick joystickA, FlightStick joystickB, FlightStick joystickC) {
    super(joystickA, joystickB, joystickC);
    this.grabber = grabber;
  }

  public void enableIndividualCommandsTest() {
    reactor.onTriggeredSubmit(joystickC.getButton(4),
        () -> new FindGear(IO.gearFindBanner));
    reactor.onTriggeredSubmit(joystickC.getButton(5),
        () -> new AlignGear(IO.gearRotatorMotor, IO.gearAlignBanner));
    reactor.onTriggeredSubmit(joystickC.getButton(6),
        () -> new GearExtend(1.0, IO.extendSolenoid));
    reactor.onTriggeredSubmit(joystickC.getButton(7),
        () -> new GearRetract(IO.extendSolenoid));
    reactor.onTriggeredSubmit(joystickC.getButton(8),
        () -> new RotateUp(1.0, IO.extendSolenoid, IO.rotateSolenoid));
    reactor.onTriggeredSubmit(joystickC.getButton(9),
        () -> new RotateDown(1.0, IO.extendSolenoid, IO.rotateSolenoid));
    reactor.onTriggeredSubmit(joystickC.getButton(10),
        () -> new GrabGear(1.0, IO.gripSolenoid));
    reactor.onTriggeredSubmit(joystickC.getButton(11),
        () -> new ReleaseGear(1.0, IO.gripSolenoid));
  }

  public void enableSequenceTest() {
    reactor.onTriggeredSubmit(joystickC.getButton(2), () -> grabber.pickUpGearSequence());
    reactor.onUntriggered(joystickC.getButton(2), () -> Strongback.submit(grabber.reset()));
    reactor.onTriggeredSubmit(joystickC.getButton(3), () -> grabber.deliverGear());
  }

}
