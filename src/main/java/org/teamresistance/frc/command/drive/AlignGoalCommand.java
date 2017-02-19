package org.teamresistance.frc.command.drive;

import org.teamresistance.frc.subsystem.drive.Drive;
import org.teamresistance.frc.subsystem.drive.DriveAligningGoalController;

/**
 * @see FaceGoalCommand
 */
public class AlignGoalCommand extends DriveCommand {

  public AlignGoalCommand(Drive drive) {
    super(drive, new DriveAligningGoalController(), false);
  }
}
