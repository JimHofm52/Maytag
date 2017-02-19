package org.teamresistance.frc.command.drive;

import org.teamresistance.frc.subsystem.drive.Drive;
import org.teamresistance.frc.subsystem.drive.DriveStrafingController;

public class DriveTimed extends DriveCommand {

  public DriveTimed(Drive drive, double orientation, double headingDeg, double timeoutSeconds) {
    super(drive, new DriveStrafingController(orientation, headingDeg), timeoutSeconds);
  }
}
