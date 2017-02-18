package org.teamresistance.frc.util.testing;

import org.strongback.components.ui.FlightStick;
import org.teamresistance.frc.IO;
import org.teamresistance.frc.subsystem.climb.Climber;

import static org.teamresistance.frc.util.testing.JoystickMap.RightJoystick.CLIMBER;

public class ClimberTesting extends CommandTesting {

  private final Climber climber;

  public ClimberTesting(Climber climber, FlightStick joystickA, FlightStick joystickB, FlightStick joystickC) {
    super(joystickA, joystickB, joystickC);
    this.climber = climber;
  }

  public void enableClimberTest() {
    // Press and hold to climb
    reactor.whileTriggered(joystickB.getButton(CLIMBER), () -> IO.climberMotor.set(1.0));
    reactor.whileUntriggered(joystickB.getButton(CLIMBER), IO.climberMotor::stopMotor);
  }

  public void enableClimbRopeTest() {
    reactor.onTriggeredSubmit(joystickB.getButton(CLIMBER), () -> climber.climbRope(25, 1.0));
  }
}
