package org.teamresistance.frc;

import org.strongback.Strongback;
import org.strongback.components.ui.ContinuousRange;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * Main robot class. Override methods from {@link IterativeRobot} to define behavior.
 *
 * @author Shreya Ravi
 * @author Rothanak So
 * @author Tarik C. Brown
 * @author Ellis Levine
 */
public class Robot {
  private final RobotDrive robotDrive;
  private final ContinuousRange knobRotation;
  private final NavX navX;

  private MecanumDrive drive;

  public Robot(RobotDrive robotDrive, ContinuousRange knobRotation, NavX navX) {
    this.robotDrive = robotDrive;
    this.knobRotation = knobRotation;
    this.navX = navX;
  }

  public void robotInit() {
    Strongback.configure().recordNoEvents().recordNoData();

    drive = new MecanumDrive(robotDrive, navX);
    drive.init(navX.getAngle(), 0.03, 0.0, 0.06);
  }

  public void teleopInit() {
    Strongback.start();
    drive.init(navX.getAngle(), 0.03, 0.0, 0.06);
  }

  public void teleopPeriodic() {
    drive.drive(0, 0, 0, knobRotation.read());
  }
}
