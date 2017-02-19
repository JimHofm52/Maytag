package org.teamresistance.frc.util.testing;

import org.strongback.components.ui.FlightStick;
import org.teamresistance.frc.IO;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static org.teamresistance.frc.util.testing.JoystickMap.RightJoystick.AGITATE_WHILE_FEEDING;
import static org.teamresistance.frc.util.testing.JoystickMap.RightJoystick.FEED_AND_SHOOT;
import static org.teamresistance.frc.util.testing.JoystickMap.CoJoystick.SNORFLE_IN;
import static org.teamresistance.frc.util.testing.JoystickMap.CoJoystick.SNORFLE_OUT;

/**
 * @author Rothanak So
 * @author Frank McCoy
 */
public class SnorflerTesting extends CommandTesting {

  public SnorflerTesting(FlightStick joystickA, FlightStick joystickB, FlightStick joystickC) {
    super(joystickA, joystickB, joystickC);
  }

  public void enableFeedingShootingTest() {
    // Trigger spins snorfler and feeder
    reactor.whileTriggered(joystickB.getButton(FEED_AND_SHOOT), () -> {
      if (joystickB.getButton(AGITATE_WHILE_FEEDING).isTriggered()) {
        IO.agitatorMotor.set(SmartDashboard.getNumber("Agitator Power", 0.47));
      } else {
        IO.agitatorMotor.stopMotor();
      }
      IO.shooterMotor.set(SmartDashboard.getNumber("Shooter Power", 0.85));
      IO.feederMotor.set(1.0);
    });
    reactor.whileUntriggered(joystickB.getButton(FEED_AND_SHOOT), () -> {
      IO.shooterMotor.stopMotor();
      IO.feederMotor.stopMotor();
      IO.agitatorMotor.stopMotor();
    });
  }

  public void enableSnorflerTest() {
    reactor.onTriggered(joystickC.getButton(SNORFLE_IN), () -> IO.snorflerMotor.set(1.0));
    reactor.onUntriggered(joystickC.getButton(SNORFLE_IN), IO.snorflerMotor::stopMotor);

    // Press and hold to reverse the snorfler
    reactor.onTriggered(joystickC.getButton(SNORFLE_OUT), () -> IO.snorflerMotor.set(-1.0));
    reactor.onUntriggered(joystickC.getButton(SNORFLE_OUT), IO.snorflerMotor::stopMotor);
  }
}
