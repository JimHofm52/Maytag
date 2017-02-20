package org.teamresistance.frc;

import org.strongback.Strongback;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.teamresistance.frc.hardware.hid.CodriverBox;
import org.teamresistance.frc.sensor.lift.LiftListener;
import org.teamresistance.frc.sensor.lift.LiftPipeline;
import org.teamresistance.frc.sensor.lift.StreamProcessedVideo;
import org.teamresistance.frc.subsystem.climb.Climber;
import org.teamresistance.frc.subsystem.drive.Drive;
import org.teamresistance.frc.subsystem.grabber.Grabber;
import org.teamresistance.frc.util.testing.ClimberTesting;
import org.teamresistance.frc.util.testing.DriveTesting;
import org.teamresistance.frc.util.testing.GrabberTesting;
import org.teamresistance.frc.util.testing.SnorflerTesting;

import java.util.OptionalDouble;

import edu.wpi.cscore.AxisCamera;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;

import static org.teamresistance.frc.Robot.CameraConfig.AXIS_IP;

//import org.teamresistance.frc.subsystem.drive.Drive;

/**
 * Main robot class. Override methods from {@link IterativeRobot} to define behavior.
 *
 * @author Shreya Ravi
 * @author Rothanak So
 * @author Tarik C. Brown
 * @author Ellis Levine
 */
public class Robot extends IterativeRobot {

  public class CameraConfig {
    public static final int WIDTH = 320;
    public static final int HEIGHT = 240;
    public static final String AXIS_IP = "10.0.86.20";
  }

  public static final FlightStick leftJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
  public static final FlightStick rightJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(1);
  public static final FlightStick coJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(2);
  public static final CodriverBox codriverBox = new CodriverBox(3);

  private final MecanumDrive mecanumDrive = new MecanumDrive(
      new RobotDrive(IO.leftFrontMotor, IO.leftRearMotor, IO.rightFrontMotor, IO.rightRearMotor), IO.navX);

  private final Drive drive = new Drive(
      mecanumDrive, codriverBox::getRotation, leftJoystick.getRoll(), leftJoystick.getPitch(),
      rightJoystick.getRoll());

  private final Grabber grabber = new Grabber(
      IO.gripSolenoid, IO.extendSolenoid, IO.rotateSolenoid, IO.gearRotatorMotor,
      IO.gearFindBanner, IO.gearAlignBanner);

  private final Climber climber = new Climber(IO.climberMotor, IO.powerPanel, IO.PDP.CLIMBER);

  // Cameras (start streaming automatically)
  private final AxisCamera axisCamera = CameraServer.getInstance().addAxisCamera(AXIS_IP);
  private final UsbCamera usbCamera = CameraServer.getInstance().startAutomaticCapture();

  // Vision (boiler vision, although it says "lift")
  private boolean visionThreadStarted = false;
  private final LiftPipeline pipeline = new LiftPipeline();
  private final LiftListener liftListener = new LiftListener();
  private final VisionThread visionThread = new VisionThread(axisCamera, pipeline, liftListener);
  private final Thread postVisionThread = new Thread(new StreamProcessedVideo(axisCamera, liftListener));

  @Override
  public void robotInit() {
    Strongback.configure().recordNoEvents().recordNoData();
    DriveTesting driveTesting = new DriveTesting(drive, IO.navX, leftJoystick, rightJoystick, coJoystick);
    SnorflerTesting snorflerTesting = new SnorflerTesting(leftJoystick, rightJoystick, coJoystick);
    ClimberTesting climberTesting = new ClimberTesting(climber, leftJoystick, rightJoystick, coJoystick);
    GrabberTesting grabberTesting = new GrabberTesting(grabber, leftJoystick, rightJoystick, coJoystick);

    // All subsystem tests are press-and-hold buttons on the right joystick
    snorflerTesting.enableSnorflerTest();
    snorflerTesting.enableFeedingShootingTest();
    climberTesting.enableClimbRopeTest();

    // Drive + vision
    driveTesting.enableAngleHold();
    driveTesting.enableAngleHoldTests();
    driveTesting.enableCancelling();
    driveTesting.enableNavXReset();
    driveTesting.enableVisionTest();
    driveTesting.enableDumbAuto();

    // Gear commands
    grabberTesting.enableSequenceTest();

    // Auto debug
    SmartDashboard.putNumber("DumbAuto Heading to Hopper", 60);
    SmartDashboard.putNumber("DumbAuto Timeout to Hopper", 3);

    SmartDashboard.putNumber("DumbAuto Heading into Hopper", 90);
    SmartDashboard.putNumber("DumbAuto Timeout into Hopper", 0.2);

    SmartDashboard.putNumber("DumbAuto Heading to Boiler", 180);
    SmartDashboard.putNumber("DumbAuto Timeout to Boiler", 0.2);

    // Actual configurations
    usbCamera.setResolution(640, 480);
    IO.pingSensor.setAutomaticMode(true);
    IO.cameraLights.set(Relay.Value.kForward); // Does not work, might be a hardware issue.
    mecanumDrive.init(IO.navX.getAngle(), 0.03, 0.0, 0.06);
  }

  @Override
  public void autonomousInit() {
    Strongback.start();
  }

  @Override
  public void teleopInit() {
    Strongback.start();
    if (!visionThreadStarted) {
      //visionThread.start(); // Vision processing
      //postVisionThread.start(); // Streaming post-processed vision
      visionThreadStarted = true;
    }
    IO.compressor.setClosedLoopControl(true);
    SmartDashboard.putNumber("Agitator Power",0.35);
    SmartDashboard.putNumber("Shooter Power", 0.80);
    mecanumDrive.init(IO.navX.getAngle(), 0.03, 0.0, 0.06);
  }

  private boolean previousOrientationState = false;

  @Override
  public void teleopPeriodic() {
    IO.compressorRelay.set(IO.compressor.enabled() ? Relay.Value.kForward : Relay.Value.kOff);

    boolean currentOrientationState = leftJoystick.getButton(8).isTriggered();
    if(!previousOrientationState && currentOrientationState) {
      // There was a drive.init() here too but it didn't exist in the working Saturday commit -- was it added as a fix?
      mecanumDrive.nextState();
    }
    previousOrientationState = currentOrientationState;

    Feedback feedback = new Feedback(
        IO.navX.getAngle(), // angle
        liftListener.getRelativeOffset(), // boiler offset, between -1 and +1
        OptionalDouble.empty() // nothing detects the lift yet; effectively a "null" double
    );
    codriverBox.update(1.0);
    drive.onUpdate(feedback);

    SmartDashboard.putNumber("Gyro", feedback.currentAngle);
    SmartDashboard.putNumber("Feedback: Boiler Offset", feedback.boilerOffset.orElse(-1));

    SmartDashboard.putBoolean("Compressor Enabled?", IO.compressor.enabled());
    SmartDashboard.putBoolean("Is Retracted?", IO.gearRetractedLimit.get());
    SmartDashboard.putBoolean("Is Gear Present (Banner)", IO.gearFindBanner.get());
    SmartDashboard.putBoolean("Is Gear Aligned (Banner)", IO.gearAlignBanner.get());

    SmartDashboard.putNumber("Climber Current", IO.powerPanel.getCurrent(IO.PDP.CLIMBER));
    SmartDashboard.putBoolean("Button 2 Pressed", coJoystick.getButton(2).isTriggered());
    SmartDashboard.putNumber("Distance Away from Gear", IO.pingSensor.getRangeInches());
    SmartDashboard.putData("Ultrasonic Sensor", IO.pingSensor);
  }

  @Override
  public void disabledInit() {
    Strongback.disable();
  }
}
