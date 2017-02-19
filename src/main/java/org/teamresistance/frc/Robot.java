package org.teamresistance.frc;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.strongback.Strongback;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.teamresistance.frc.sensor.lift.LiftListener;
import org.teamresistance.frc.sensor.lift.LiftPipeline;
import org.teamresistance.frc.subsystem.climb.Climber;
import org.teamresistance.frc.subsystem.drive.Drive;
import org.teamresistance.frc.subsystem.grabber.Grabber;
import org.teamresistance.frc.util.testing.ClimberTesting;
import org.teamresistance.frc.util.testing.DriveTesting;
import org.teamresistance.frc.util.testing.GrabberTesting;
import org.teamresistance.frc.util.testing.SnorflerTesting;

import java.util.ArrayList;
import java.util.OptionalDouble;

import edu.wpi.cscore.AxisCamera;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;

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
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
  }

  public static final FlightStick leftJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
  public static final FlightStick rightJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(1);
  public static final FlightStick coJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(2);
  //public static final CodriverBox codriverBox = new CodriverBox(3);

  //private final MecanumDrive drive = new MecanumDrive(
  //    new RobotDrive(
  //        IO.leftFrontMotor,
  //        IO.leftRearMotor,
  //        IO.rightFrontMotor,
  //        IO.rightRearMotor),
  //    IO.navX);

  private final Drive drive = new Drive(
      new RobotDrive(IO.leftFrontMotor, IO.leftRearMotor, IO.rightFrontMotor, IO.rightRearMotor),
      IO.navX, leftJoystick.getRoll(), leftJoystick.getPitch(), rightJoystick.getRoll());

  private final Grabber grabber = new Grabber(
      IO.gripSolenoid,
      IO.extendSolenoid,
      IO.rotateSolenoid,
      IO.gearRotatorMotor,
      IO.gearFindBanner,
      IO.gearAlignBanner
  );

  private final Climber climber = new Climber(IO.climberMotor, IO.powerPanel, IO.PDP.CLIMBER);

  // Vision
  private boolean visionThreadStarted = false;
  private final AxisCamera axisCamera = CameraServer.getInstance().addAxisCamera("10.0.86.100");
  private final LiftPipeline pipeline = new LiftPipeline();
  private final LiftListener liftListener = new LiftListener();
  private final VisionThread visionThread = new VisionThread(axisCamera, pipeline, liftListener);
  private final Thread postVisionThread = new Thread(() -> {
    // FIXME: May have been causing problems earlier. Not really needed anyway, so it's "off" (see teleopInit)
    // This entire thread is only responsible for outputting post-processed images to the
    // SmartDashboard. It doesn't do any vision processing itself--the VisionThread handles that.
    // Don't forget to call run() after instantiating this thread.
    CvSink inputSource = CameraServer.getInstance().getVideo(axisCamera);

    // Save bandwidth by ensuring inputSource res == outputStream res
    CvSource outputStream = CameraServer.getInstance().putVideo("Hello Driver", CameraConfig.WIDTH, CameraConfig.HEIGHT);

    // Convenient color palette for drawing our shapes (BGR format)
    final Scalar green = new Scalar(0, 255, 0);
    final Scalar yellow = new Scalar(0, 255, 255);
    final Scalar blue = new Scalar(255, 0, 0);

    while (!Thread.interrupted()) {
      Mat grabbedFrame = new Mat();
      inputSource.grabFrame(grabbedFrame);

      // Copy the image to a new reference. Leave the original reference alone in case the boiler
      // processing code happens to be holding the exact same reference... because C.
      Mat image = grabbedFrame.clone();
      //grabbedFrame.copyTo(image);

      // Steal the most recently computed hulls from the pipeline listener
      ArrayList<MatOfPoint> convexHulls = liftListener.getHulls();

      // Draw the raw convex hulls
      Imgproc.drawContours(image, convexHulls, -1, green, 2);

      // Draw the bounding boxes
      convexHulls.forEach(hull -> {
        Rect rect = Imgproc.boundingRect(hull);
        Imgproc.rectangle(image, rect.tl(), rect.br(), yellow, 2);
      });

      // Draw a friendly circle regardless of if there are hulls -- for troubleshooting
      Imgproc.circle(image, new Point(50, 50), 50, blue, 2);

      // Notifies the downstream sinks
      outputStream.putFrame(image);
    }
  });

  @Override
  public void robotInit() {
    Strongback.configure().recordNoEvents().recordNoData();
    DriveTesting driveTesting = new DriveTesting(drive, IO.navX, leftJoystick, rightJoystick, coJoystick);
    SnorflerTesting snorflerTesting = new SnorflerTesting(leftJoystick, rightJoystick, coJoystick);
    ClimberTesting climberTesting = new ClimberTesting(climber, leftJoystick, rightJoystick, coJoystick);
    GrabberTesting grabberTesting = new GrabberTesting(grabber, leftJoystick, rightJoystick, coJoystick);

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

    // Gear commands
    grabberTesting.enableSequenceTest();
  }

  @Override
  public void autonomousInit() {
    Strongback.start();
  }

  @Override
  public void teleopInit() {
    Strongback.start();
    if (!visionThreadStarted) {
      visionThread.start(); // Vision processing
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
        liftListener.getRelativeOffset(), // lift offset, between -1 and +1
        OptionalDouble.empty() // nothing detects the boiler yet; effectively a "null" double
    );

    SmartDashboard.putNumber("Gyro", feedback.currentAngle);
    SmartDashboard.putNumber("Boiler Offset", feedback.boilerOffset.orElse(-1));
    SmartDashboard.putNumber("Lift Offset", feedback.liftOffset.orElse(-1));
    drive.onUpdate(feedback);

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
