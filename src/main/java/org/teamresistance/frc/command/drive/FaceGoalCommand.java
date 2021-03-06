package org.teamresistance.frc.command.drive;

import org.strongback.command.Command;
import org.strongback.command.Requirable;
import org.teamresistance.frc.sensor.lift.LiftPipeline;
import org.teamresistance.frc.subsystem.drive.Drive;
import org.teamresistance.frc.subsystem.drive.DriveFacingGoalController;

/**
 * A command that forever turns the robot to face the goal.
 * <p>
 * Requires a lease on the {@link Drive} subsystem. The only way to finish this command is to cancel
 * it (see: {@link Command#cancel(Requirable...)} or interrupt it by submitting another command that
 * requires a lease on {@link Drive}.
 *
 * @author Rothanak So
 * @see LiftPipeline
 * @see AlignGoalCommand
 */
public class FaceGoalCommand extends DriveCommand {

  public FaceGoalCommand(Drive drive) {
    super(drive, new DriveFacingGoalController(), false);
  }
}
