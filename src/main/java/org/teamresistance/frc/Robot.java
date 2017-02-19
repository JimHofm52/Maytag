package org.teamresistance.frc;

import org.strongback.Strongback;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.teamresistance.frc.hardware.hid.CodriverBox;
import org.teamresistance.frc.subsystem.climb.Climber;
import org.teamresistance.frc.subsystem.grabber.Grabber;
import org.teamresistance.frc.util.testing.ClimberTesting;
import org.teamresistance.frc.util.testing.GrabberTesting;
import org.teamresistance.frc.util.testing.SnorflerTesting;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
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

  public static final FlightStick leftJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
  public static final FlightStick rightJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(1);
  public static final FlightStick coJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(2);
  public static final CodriverBox codriverBox = new CodriverBox(3);

  private final UsbCamera usbCamera = CameraServer.getInstance().startAutomaticCapture();

  private final MecanumDrive drive= new MecanumDrive(new RobotDrive(
          IO.leftFrontMotor,
          IO.leftRearMotor,
          IO.rightFrontMotor,
          IO.rightRearMotor),
      IO.navX);
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

    usbCamera.setResolution(640, 480);

    IO.cameraLights.set(Relay.Value.kForward); // Does not work, might be a hardware issue.

    // All subsystem tests are press-and-hold buttons on the right joystick
    snorflerTesting.enableSnorflerTest();
    snorflerTesting.enableFeedingShootingTest();
    climberTesting.enableClimbRopeTest();

    // Gear commands
    grabberTesting.enableSequenceTest();

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

  private boolean previousOrientationState = false;

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("Climber Current", IO.powerPanel.getCurrent(IO.PDP.CLIMBER));

    boolean currentOrientationState = leftJoystick.getButton(8).isTriggered();
    // this IF is the equivalent of running the code onTriggered
    if(!previousOrientationState && currentOrientationState) {
      drive.init(IO.navX.getAngle(), drive.getkP(), drive.getkI(), drive.getkD());
      drive.nextState();
    }
    previousOrientationState = currentOrientationState;

    codriverBox.update(1.0);
    drive.drive(leftJoystick.getRoll().read(), leftJoystick.getPitch().read(), rightJoystick.getRoll().read(), codriverBox.getRotation());

    SmartDashboard.putNumber("Gyro", IO.navX.getAngle());
    SmartDashboard.putNumber("Dave Knob", codriverBox.getRotation());

    IO.compressorRelay.set(IO.compressor.enabled() ? Relay.Value.kForward : Relay.Value.kOff);
    SmartDashboard.putBoolean("Compressor Enabled?", IO.compressor.enabled());
    SmartDashboard.putBoolean("Is Retracted?", IO.gearRetractedLimit.get());
    SmartDashboard.putBoolean("Is Gear Present (Banner)", IO.gearFindBanner.get());
    SmartDashboard.putBoolean("Is Gear Aligned (Banner)", IO.gearAlignBanner.get());

    SmartDashboard.putBoolean("Button 2 Pressed", coJoystick.getButton(2).isTriggered());
  }

  @Override
  public void disabledInit() {
    Strongback.disable();
  }
}
