package org.teamresistance.frc.subsystem.drive;

import org.teamresistance.frc.Feedback;
import org.teamresistance.frc.subsystem.Controller;

public class DriveStrafingController implements Controller<Drive.Signal> {
  private static final double SPEED = 1.0;

  private final Controller<Drive.Signal> angleController;
  private final double headingDeg;
  private final double speed;

  public DriveStrafingController(double orientiation, double headingDeg) {
    this(orientiation, headingDeg, SPEED);
  }

  public DriveStrafingController(double orientation, double headingDeg, double speed) {
    this.angleController = new DriveHoldingAngleController(orientation);
    this.headingDeg = headingDeg;
    this.speed = speed;
  }

  @Override
  public Drive.Signal computeSignal(Drive.Signal feedForward, Feedback feedback) {
    double rotateSpeed = angleController.computeSignal(feedForward, feedback).rotateSpeed;
    return Drive.Signal.createRobotOriented(SPEED, headingDeg, rotateSpeed);

  }
}
