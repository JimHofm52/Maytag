package org.teamresistance.frc.subsystem.drive;

import org.teamresistance.frc.Feedback;
import org.teamresistance.frc.subsystem.Controller;
import org.teamresistance.frc.util.SynchronousPID;

import java.util.OptionalDouble;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static org.strongback.control.SoftwarePIDController.SourceType;

/**
 * @see DriveAligningGoalController
 */
public class DriveAligningGoalController implements Controller<Drive.Signal> {
  private static final double TOLERANCE = 0.02;
  private static final double KP = 3; // maps the input domain to the output domain [-.8, +.8]
  private static final double KD = 0;
  private static final double KI = 0;

  private final SynchronousPID pid;

  public DriveAligningGoalController() {
    this.pid = new SynchronousPID("Align Goal PID", SourceType.DISTANCE, KP, KI, KD)
        .withConfigurations(controller -> controller
            .withInputRange(-1.0, 1.0) // offset percentage
            .withOutputRange(-.8, .8) // motor
            .withTarget(0) // we want to be centered
            .withTolerance(TOLERANCE));
  }

  @Override
  public Drive.Signal computeSignal(Drive.Signal feedForward, Feedback feedback) {
    // If we see the goal, strafe so the robot is centered on it. Otherwise, pass the joysticks
    // through so the driver can move the robot to see the goal.
    OptionalDouble maybeOffset = feedback.liftOffset;

    // TODO: if this piecewise works nicely, make it a method somewhere to avoid duplication
    double strafeSpeed;
    if (maybeOffset.isPresent()) {
      strafeSpeed = pid.calculate(maybeOffset.getAsDouble());
      if (strafeSpeed > 0.2) {
        // change nothing
      } else if (strafeSpeed > 0.05) {
        strafeSpeed = 0.2;
      } else if (strafeSpeed > -0.05) {
        strafeSpeed = 0;
      } else if (strafeSpeed > -0.2) {
        strafeSpeed = -0.2;
      }
    } else {
      strafeSpeed = feedForward.xSpeed;
    }

    SmartDashboard.putNumber("Vision: Align Goal rotate speed", strafeSpeed);
    return Drive.Signal.createRobotOriented(strafeSpeed, feedForward.ySpeed, feedForward
        .rotateSpeed);
  }
}
