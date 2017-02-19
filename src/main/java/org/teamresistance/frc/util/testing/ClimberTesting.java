package org.teamresistance.frc.util.testing;

import org.strongback.Strongback;
import org.strongback.command.Command;
import org.strongback.components.ui.FlightStick;
import org.teamresistance.frc.subsystem.climb.Climber;

import static org.teamresistance.frc.util.testing.JoystickMap.CoJoystick.CLIMBER;

/**
 * @author Shreya Ravi
 */
public class ClimberTesting extends CommandTesting {
  private final Climber climber;

  public ClimberTesting(Climber climber, FlightStick joystickA, FlightStick joystickB, FlightStick joystickC) {
    super(joystickA, joystickB, joystickC);
    this.climber = climber;
  }

  public void enableClimbRopeTest() {
    reactor.onTriggeredSubmit(joystickC.getButton(CLIMBER), () -> climber.climbRope(70, 0.1));
    reactor.onUntriggered(joystickC.getButton(CLIMBER), () -> Strongback.submit(Command.cancel(climber)));
  }
}
