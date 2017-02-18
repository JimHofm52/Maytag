package org.teamresistance.frc;

import edu.wpi.first.wpilibj.Joystick;
import org.strongback.Strongback;
import org.strongback.components.AngleSensor;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.teamresistance.frc.hid.DaveKnob;
import org.teamresistance.frc.subsystem.climb.Climber;
import org.teamresistance.frc.subsystem.drive.Drive;
import org.teamresistance.frc.subsystem.drive.DriveHoldingAngleController;
import org.teamresistance.frc.subsystem.grabber.Grabber;
import org.teamresistance.frc.util.testing.ClimberTesting;
import org.teamresistance.frc.util.testing.DriveTesting;
import org.teamresistance.frc.util.testing.GrabberTesting;
import org.teamresistance.frc.util.testing.SnorflerTesting;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Main robot class. Override methods from {@link IterativeRobot} to define behavior.
 *
 * @author Shreya Ravi
 * @author Rothanak So
 * @author Tarik C. Brown
 * @author Ellis Levine
 */
public class Robot extends IterativeRobot {
  private final FlightStick leftJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
  private final FlightStick rightJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(1);
  private final FlightStick coJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(2);
  private final CodriverBox codriverBox = new CodriverBox(3);


  private MecanumDrive drive;


  private final Grabber grabber = new Grabber(
      IO.gripSolenoid,
      IO.extendSolenoid,
      IO.rotateSolenoid,
      IO.gearRotatorMotor,
      IO.gearFindBanner,
      IO.gearAlignBanner
  );

  private final Climber climber = new Climber(IO.climberMotor, IO.powerPanel, IO.PDP.CLIMBER);

  @Override
  public void robotInit() {
    Strongback.configure().recordNoEvents().recordNoData();
    SnorflerTesting snorflerTesting = new SnorflerTesting(leftJoystick, rightJoystick, coJoystick);
    ClimberTesting climberTesting = new ClimberTesting(climber, leftJoystick, rightJoystick, coJoystick);
    GrabberTesting grabberTesting = new GrabberTesting(grabber, leftJoystick, rightJoystick, coJoystick);

    IO.cameraLights.set(Relay.Value.kForward); // Does not work, might be a hardware issue.

    // All subsystem tests are press-and-hold buttons on the right joystick
    snorflerTesting.enableSnorflerTest();
    snorflerTesting.enableFeedingShootingTest();
    climberTesting.enableClimbRopeTest();

    // Gear commands
    grabberTesting.enableIndividualCommandsTest();
    grabberTesting.enableClimbRopeTest();

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
    IO.compressor.setClosedLoopControl(true);
    SmartDashboard.putNumber("Agitator Power",0.35);
    SmartDashboard.putNumber("Shooter Power", 0.80);

    drive.init(IO.navX.getAngle(), 0.03, 0.0, 0.06);
  }

  private boolean fieldOrientedState = false;

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("Climber Current", IO.powerPanel.getCurrent(IO.PDP.CLIMBER));
//    SmartDashboard.putData("PDP", IO.powerPanel);

    Feedback feedback = new Feedback(IO.navX.getAngle());
    SmartDashboard.putNumber("THIS Gyro!!!!!!!!", feedback.currentAngle);

    boolean currentOrientationState = leftJoystick.getButton(8).isTriggered();
    if(!fieldOrientedState && currentOrientationState) {
      drive.nextState();
    }
    fieldOrientedState = !currentOrientationState;

    codriverBox.update(1.0);
    drive.drive(leftJoystick.getRoll().read(), leftJoystick.getPitch().read(), rightJoystick.getRoll().read(), codriverBox.getRotation());

    IO.compressorRelay.set(IO.compressor.enabled() ? Relay.Value.kForward : Relay.Value.kOff);
    SmartDashboard.putBoolean("Compressor Enabled?", IO.compressor.enabled());
    SmartDashboard.putBoolean("Is Retracted?", IO.gearRetractedLimit.get());
    SmartDashboard.putBoolean("Is Gear Present (Banner)", IO.gearFindBanner.get());
    SmartDashboard.putBoolean("Is Gear Aligned (Banner)", IO.gearAlignBanner.get());
  }

  @Override
  public void disabledInit() {
    Strongback.disable();
  }
}
