package org.teamresistance.frc.command.grabber;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.command.Command;
import org.teamresistance.frc.InvertibleSolenoid;
import org.teamresistance.frc.InvertibleSolenoidWithPosition;
import org.teamresistance.frc.SingleSolenoid;

/**
 * Created by shrey on 2/7/2017.
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
