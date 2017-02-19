package org.teamresistance.frc.command.grabber;

import org.strongback.command.Command;
import org.teamresistance.frc.hardware.component.SingleSolenoid;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Shreya Ravi
 */
public class RotateDown extends Command {
  private final SingleSolenoid extendSolenoid;
  private final SingleSolenoid rotateSolenoid;

  public RotateDown(double timeLimit, SingleSolenoid extendSolenoid, SingleSolenoid rotateSolenoid) {
    super(timeLimit, extendSolenoid, rotateSolenoid);
    this.extendSolenoid = extendSolenoid;
    this.rotateSolenoid = rotateSolenoid;
  }

  @Override
  public boolean execute() {
    if (extendSolenoid.isRetracted()) {
      rotateSolenoid.extend();
    }
    SmartDashboard.putBoolean("Rotate Down Executed", false);
    return false;
  }

  @Override
  public void interrupted() {
    end();
  }

  @Override
  public void end() {
    SmartDashboard.putBoolean("Rotate Down Executed", true);
  }
}
