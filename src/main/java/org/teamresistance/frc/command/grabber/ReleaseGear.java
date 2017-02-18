package org.teamresistance.frc.command.grabber;

import org.strongback.command.Command;
import org.teamresistance.frc.InvertibleSolenoid;
import org.teamresistance.frc.SingleSolenoid;

/**
 * Created by shrey on 2/7/2017.
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
