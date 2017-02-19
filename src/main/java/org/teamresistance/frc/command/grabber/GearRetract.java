package org.teamresistance.frc.command.grabber;

import org.strongback.command.Command;
import org.teamresistance.frc.hardware.component.SingleSolenoid;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Shreya Ravi
 */
public class GearRetract extends Command {
  private final SingleSolenoid extendSolenoid;

  public GearRetract(SingleSolenoid extendSolenoid) {
    super(extendSolenoid);
    this.extendSolenoid = extendSolenoid;
  }

  @Override
  public boolean execute() {
    extendSolenoid.retract();
    SmartDashboard.putBoolean("Gear Retracted", extendSolenoid.isRetracted());
    return extendSolenoid.isRetracted();
  }
}
