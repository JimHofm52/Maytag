package org.teamresistance.frc.subsystem.drive;

import org.strongback.command.Command;
import org.strongback.command.CommandGroup;
import org.strongback.command.Requirable;
import org.strongback.components.Stoppable;
import org.strongback.components.ui.ContinuousRange;
import org.strongback.drive.MecanumDrive;
import org.teamresistance.frc.Pose;
import org.teamresistance.frc.subsystem.ClosedLooping;
import org.teamresistance.frc.subsystem.Controller;
import org.teamresistance.frc.subsystem.OpenLoopController;

/**
 * The drive subsystem. The <b>only</b> way to manipulate the drivetrain is through this class. It
 * runs on a loop for the entire duration of the match and supports both operator and autonomous
 * control.
 * <p>
 * Set the current <b>behavior</b> by supplying a {@link Controller} for {@link Signal}s through
 * {@link #setController(Controller)}. These calls to {@link #setController(Controller)} can be
 * queued and scheduled using {@link Command}s and {@link CommandGroup}s to create higher-level
 * routines for controlling the robot. The {@link Controller#computeSignal} method of the active
 * controller will be invoked during {@link #onUpdate(Pose)}, and the resulting output will be fed
 * into {@link #onSignal(Signal)}.
 * <p>
 * The subsystem starts with the {@link OpenLoopController} in place. It enables the default
 * follow-through behavior of driving the robot field-oriented in response to the raw joystick
 * values from the operator. Re-enable this behavior at any time by calling {@link #setOpenLoop()}.
 * <p>
 * Advanced {@link Controller} implementations can suppress the raw joystick values (the
 * "feedforward") and supply their own speeds, allowing for semi-autonomous behaviors like
 * maintaining the current heading or fully-autonomous behaviors like pathfinding. Both
 * field-oriented and robot-oriented {@link Signal}s are supported.
 *
 * @author Shreya Ravi
 * @author Rothanak So
 */
public class Drive extends ClosedLooping<Drive.Signal> implements Stoppable, Requirable {
  private final MecanumDrive robotDrive;

  public Drive(MecanumDrive robotDrive, ContinuousRange xSpeed, ContinuousRange ySpeed,
               ContinuousRange rotateSpeed) {
    super(() -> new Signal(xSpeed.read(), ySpeed.read(), rotateSpeed.read()));
    this.robotDrive = robotDrive;
  }

  @Override
  protected void onSignal(Drive.Signal signal) {
    if (signal.robotOriented) {
      // Convert the speeds from cartesian to polar
      double magnitude = Math.sqrt(signal.xSpeed * signal.xSpeed + signal.ySpeed * signal.ySpeed);
      double direction = Math.atan2(signal.ySpeed, signal.xSpeed); // (y, x)
      robotDrive.polar(magnitude, direction, signal.rotateSpeed);
    } else {
      robotDrive.cartesian(signal.xSpeed, signal.ySpeed, signal.rotateSpeed);
    }
  }

  @Override
  public void stop() {
    setController(new DriveHaltingController());
  }

  public static final class Signal {
    final double xSpeed;
    final double ySpeed;
    final double rotateSpeed;
    final boolean robotOriented;

    Signal(double xSpeed, double ySpeed, double rotateSpeed) {
      this(xSpeed, ySpeed, rotateSpeed, false);
    }

    Signal(double xSpeed, double ySpeed, double rotateSpeed, boolean robotOriented) {
      this.xSpeed = xSpeed;
      this.ySpeed = ySpeed;
      this.rotateSpeed = rotateSpeed;
      this.robotOriented = robotOriented;
    }
  }
}