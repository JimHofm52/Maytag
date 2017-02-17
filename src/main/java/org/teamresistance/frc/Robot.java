package org.teamresistance.frc;

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
  private final FlightStick leftJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
  private final FlightStick rightJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(1);
  private final FlightStick coJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(2);

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

  @Override
  public void robotInit() {
    Strongback.configure().recordNoEvents().recordNoData();
    DriveTesting driveTesting = new DriveTesting(drive, IO.navX, leftJoystick, rightJoystick, coJoystick);
    SnorflerTesting snorflerTesting = new SnorflerTesting(leftJoystick, rightJoystick, coJoystick);
    ClimberTesting climberTesting = new ClimberTesting(leftJoystick, rightJoystick, coJoystick);

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

    // Gear commands
    SwitchReactor reactor = Strongback.switchReactor();
    reactor.onTriggeredSubmit(coJoystick.getButton(4),
        () -> new FindGear(IO.gearFindBanner));
    reactor.onTriggeredSubmit(coJoystick.getButton(5),
        () -> new AlignGear(IO.gearRotatorMotor, IO.gearAlignBanner));
    reactor.onTriggeredSubmit(coJoystick.getButton(6),
        () -> new GearExtend(1.0, IO.extendSolenoid));
    reactor.onTriggeredSubmit(coJoystick.getButton(7),
        () -> new GearRetract(IO.extendSolenoid));
    reactor.onTriggeredSubmit(coJoystick.getButton(8),
        () -> new RotateUp(1.0, IO.extendSolenoid, IO.rotateSolenoid));
    reactor.onTriggeredSubmit(coJoystick.getButton(9),
        () -> new RotateDown(1.0, IO.extendSolenoid, IO.rotateSolenoid));
    reactor.onTriggeredSubmit(coJoystick.getButton(10),
        () -> new GrabGear(1.0, IO.gripSolenoid));
    reactor.onTriggeredSubmit(coJoystick.getButton(11),
        () -> new ReleaseGear(1.0, IO.gripSolenoid));

    reactor.onTriggeredSubmit(coJoystick.getButton(2), () -> grabber.pickupGear());
    reactor.onTriggeredSubmit(coJoystick.getButton(3), () -> grabber.deliverGear());

  }

  @Override
  public void autonomousInit() {
    Strongback.start();
  }

  @Override
  public void teleopInit() {
    Strongback.start();
    IO.compressor.setClosedLoopControl(true);
  }

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("Knob: Angle", rawKnob.getAngle());
    SmartDashboard.putNumber("Knob: Speed output", knob.read());
    SmartDashboard.putNumber("Climber Current", IO.powerPanel.getCurrent(IO.PDP.CLIMBER));

    Feedback feedback = new Feedback(IO.navX.getAngle());
    SmartDashboard.putNumber("Gyro", feedback.currentAngle);

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
