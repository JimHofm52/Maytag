package org.teamresistance.frc.command.drive;

import org.strongback.command.CommandGroup;
import org.teamresistance.frc.subsystem.drive.Drive;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DumbAuto extends CommandGroup {

  public DumbAuto(Drive drive) {
    sequentially(
        new DriveTimed(drive, 0,
            SmartDashboard.getNumber("DumbAuto Heading to Hopper", 60),
            SmartDashboard.getNumber("DumbAuto Timeout to Hopper", 3)),
        new DriveTimed(drive, 0,
            SmartDashboard.getNumber("DumbAuto Heading into Hopper", 90),
            SmartDashboard.getNumber("DumbAuto Timeout into Hopper", 0.2)),
        new DriveTimed(drive, 0,
            SmartDashboard.getNumber("DumbAuto Heading to Boiler", 180),
            SmartDashboard.getNumber("DumbAuto Timeout to Boiler", 0.2))
    );
  }
}
