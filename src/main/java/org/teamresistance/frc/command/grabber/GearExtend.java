package org.teamresistance.frc.command.grabber;

import org.strongback.command.Command;
import org.teamresistance.frc.hardware.component.SingleSolenoid;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Shreya Ravi
 */
public class GearExtend extends Command {
  private final SingleSolenoid extendSolenoid;

  public GearExtend(double timeLimit, SingleSolenoid extendSolenoid) {
    super(timeLimit, extendSolenoid);
    this.extendSolenoid = extendSolenoid;
  }

  @Override
  public boolean execute() {
    extendSolenoid.extend();
    SmartDashboard.putBoolean("Gear Extended", extendSolenoid.isExtended());
    return false;
  }
}
