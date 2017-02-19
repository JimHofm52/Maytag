package org.teamresistance.frc.command.drive;

import org.strongback.command.Command;
import org.teamresistance.frc.subsystem.drive.Drive;

public class HardBrake extends Command {
  private final Drive drive;

  public HardBrake(Drive drive, double timeoutSeconds) {
    super(timeoutSeconds, drive);
    this.drive = drive;
  }

  @Override
  public void initialize() {
    drive.hackPressBrake();
  }

  @Override
  public boolean execute() {
    return false; // For now, brake until the timeout expires.
  }

  @Override
  public void interrupted() {
    end();
  }

  @Override
  public void end() {
    drive.hackLiftBrake();
  }
}
