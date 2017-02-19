package org.teamresistance.frc.command.grabber;

import org.strongback.command.Command;
import org.teamresistance.frc.hardware.component.SingleSolenoid;

/**
 * @author Shreya Ravi
 */
public class ReleaseGear extends Command {
  private final SingleSolenoid gripSolenoid;

  public ReleaseGear(double timeLimit, SingleSolenoid gripSolenoid) {
    super(timeLimit, gripSolenoid);
    this.gripSolenoid = gripSolenoid;
  }

  @Override
  public boolean execute() {
    gripSolenoid.extend();
    return false;
  }
}
