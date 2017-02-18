package org.teamresistance.frc;

import edu.wpi.first.wpilibj.Sendable;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.AngleSensor;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.teamresistance.frc.command.grabber.AlignGear;
import org.teamresistance.frc.command.grabber.FindGear;
import org.teamresistance.frc.command.grabber.GearExtend;
import org.teamresistance.frc.command.grabber.GearRetract;
import org.teamresistance.frc.command.grabber.GrabGear;
import org.teamresistance.frc.command.grabber.ReleaseGear;
import org.teamresistance.frc.command.grabber.RotateDown;
import org.teamresistance.frc.command.grabber.RotateUp;
import org.teamresistance.frc.hid.DaveKnob;
import org.teamresistance.frc.subsystem.drive.Drive;
import org.teamresistance.frc.util.testing.ClimberTesting;
import org.teamresistance.frc.util.testing.DriveTesting;
import org.teamresistance.frc.util.testing.GrabberTesting;
import org.teamresistance.frc.util.testing.SnorflerTesting;
import org.teamresistance.frc.command.grabber.*;
import org.teamresistance.frc.subsystem.climb.Climber;
//import org.teamresistance.frc.subsystem.drive.Drive;
import org.teamresistance.frc.subsystem.grabber.Grabber;
import org.teamresistance.frc.subsystem.snorfler.Snorfler;

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
  public final FlightStick leftJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
  public final FlightStick rightJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(1);
  public static final FlightStick coJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(2);

  public static boolean test = false;

  // Dave knob (runs on third joystick); does not rotate the bot, disabled for now
  private final AngleSensor rawKnob = () -> coJoystick.getAxis(2).read() * -180 + 180;
  private final DaveKnob knob = new DaveKnob(rawKnob, IO.navX);

  // Drive subsystem
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

  @Override
  public void robotInit() {
    Strongback.configure().recordNoEvents().recordNoData();
    DriveTesting driveTesting = new DriveTesting(drive, IO.navX, leftJoystick, rightJoystick, coJoystick);
    SnorflerTesting snorflerTesting = new SnorflerTesting(leftJoystick, rightJoystick, coJoystick);
    ClimberTesting climberTesting = new ClimberTesting(climber, leftJoystick, rightJoystick, coJoystick);
    GrabberTesting grabberTesting = new GrabberTesting(grabber, leftJoystick, rightJoystick, coJoystick);

    IO.cameraLights.set(Relay.Value.kForward); // Does not work, might be a hardware issue.

    // All driving-related tests run on the left joystick
    driveTesting.enableAngleHold();
    driveTesting.enableAngleHoldTests();
    driveTesting.enableCancelling();
    driveTesting.enableNavXReset();

    // All subsystem tests are press-and-hold buttons on the right joystick
    snorflerTesting.enableSnorflerTest();
    snorflerTesting.enableFeedingShootingTest();
    climberTesting.enableClimberTest();
    climberTesting.enableClimbRopeTest();

    // Gear commands
    grabberTesting.enableIndividualCommandsTest();
    grabberTesting.enableSequenceTest();

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

  }

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("Knob: Angle", rawKnob.getAngle());
    SmartDashboard.putNumber("Knob: Speed output", knob.read());
    SmartDashboard.putNumber("Climber Current", IO.powerPanel.getCurrent(IO.PDP.CLIMBER));
    SmartDashboard.putData("PDP", IO.powerPanel);

    Feedback feedback = new Feedback(IO.navX.getAngle());
    SmartDashboard.putNumber("Gyro", feedback.currentAngle);

    drive.onUpdate(feedback);

    IO.compressorRelay.set(IO.compressor.enabled() ? Relay.Value.kForward : Relay.Value.kOff);
    SmartDashboard.putBoolean("Compressor Enabled?", IO.compressor.enabled());
    SmartDashboard.putBoolean("Is Retracted?", IO.gearRetractedLimit.get());
    SmartDashboard.putBoolean("Is Gear Present (Banner)", IO.gearFindBanner.get());
    SmartDashboard.putBoolean("Is Gear Aligned (Banner)", IO.gearAlignBanner.get());

    SmartDashboard.putBoolean("Button 2 Pressed", coJoystick.getButton(2).isTriggered());
    test = coJoystick.getButton(2).isTriggered();

//    SmartDashboard.putBoolean("Grabber Interrupted", Grabber.interrrupted);
  }

  @Override
  public void disabledInit() {
    Strongback.disable();
  }
}
