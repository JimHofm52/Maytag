package org.teamresistance.frc;

import org.strongback.Strongback;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
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
  //public static final CodriverBox codriverBox = new CodriverBox(3);

  //private final MecanumDrive drive = new MecanumDrive(
  //    new RobotDrive(IO.leftFrontMotor, IO.leftRearMotor, IO.rightFrontMotor, IO.rightRearMotor), IO.navX);

  private final Drive drive = new Drive(
      new RobotDrive(IO.leftFrontMotor, IO.leftRearMotor, IO.rightFrontMotor, IO.rightRearMotor),
      IO.navX, leftJoystick.getRoll(), leftJoystick.getPitch(), rightJoystick.getRoll());

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

    usbCamera.setResolution(640, 480);

    IO.cameraLights.set(Relay.Value.kForward); // Does not work, might be a hardware issue.

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

    SmartDashboard.putNumber("DumbAuto Heading to Hopper", 60);
    SmartDashboard.putNumber("DumbAuto Timeout to Hopper", 3);

    SmartDashboard.putNumber("DumbAuto Heading into Hopper", 90);
    SmartDashboard.putNumber("DumbAuto Timeout into Hopper", 0.2);

    SmartDashboard.putNumber("DumbAuto Heading to Boiler", 180);
    SmartDashboard.putNumber("DumbAuto Timeout to Boiler", 0.2);
    IO.pingSensor.setAutomaticMode(true);
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
  }

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("Climber Current", IO.powerPanel.getCurrent(IO.PDP.CLIMBER));

    Feedback feedback = new Feedback(
        IO.navX.getAngle(), // angle
        liftListener.getRelativeOffset(), // boiler offset, between -1 and +1
        OptionalDouble.empty() // nothing detects the lift yet; effectively a "null" double
    );

    SmartDashboard.putNumber("Gyro", feedback.currentAngle);
    SmartDashboard.putNumber("Feedback: Boiler Offset", feedback.boilerOffset.orElse(-1));
    //SmartDashboard.putNumber("Feedback: Lift Offset", feedback.liftOffset.orElse(-1));
    drive.onUpdate(feedback);

    IO.compressorRelay.set(IO.compressor.enabled() ? Relay.Value.kForward : Relay.Value.kOff);
    SmartDashboard.putBoolean("Compressor Enabled?", IO.compressor.enabled());
    SmartDashboard.putBoolean("Is Retracted?", IO.gearRetractedLimit.get());
    SmartDashboard.putBoolean("Is Gear Present (Banner)", IO.gearFindBanner.get());
    SmartDashboard.putBoolean("Is Gear Aligned (Banner)", IO.gearAlignBanner.get());

    SmartDashboard.putBoolean("Button 2 Pressed", coJoystick.getButton(2).isTriggered());
    SmartDashboard.putData("Ultrasonic Sensor", IO.pingSensor);
    SmartDashboard.putNumber("Distance Away from Gear", IO.pingSensor.getRangeInches());
  }

  @Override
  public void disabledInit() {
    Strongback.disable();
  }
}
