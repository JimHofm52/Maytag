package org.teamresistance.frc;

import org.strongback.Strongback;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;

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
public class Robot extends IterativeRobot {
  public static final FlightStick rightJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(1);
  public static final CodriverBox codriverBox = new CodriverBox(3);

  private MecanumDrive drive;

  @Override
  public void robotInit() {
    Strongback.configure().recordNoEvents().recordNoData();

    drive = new MecanumDrive(new RobotDrive(IO.leftFrontMotor, IO.leftRearMotor, IO.rightFrontMotor, IO.rightRearMotor),
        IO.navX);
    drive.init(IO.navX.getAngle(), 0.03, 0.0, 0.06);
  }

  @Override
  public void autonomousInit() {
    Strongback.start();
  }

  @Override
  public void teleopInit() {
    Strongback.start();
    drive.init(IO.navX.getAngle(), 0.03, 0.0, 0.06);
  }

  @Override
  public void teleopPeriodic() {
    codriverBox.update(1.0);
    drive.drive(0, 0, rightJoystick.getRoll().read(), codriverBox.getRotation());
  }

  @Override
  public void disabledInit() {
    Strongback.disable();
  }
}
