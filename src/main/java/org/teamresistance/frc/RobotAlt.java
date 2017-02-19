package org.teamresistance.frc;

import org.strongback.Strongback;
import org.strongback.components.ui.ContinuousRange;
import org.teamresistance.frc.subsystem.drive.Drive;

import edu.wpi.first.wpilibj.RobotDrive;

public class RobotAlt {
  private final RobotDrive robotDrive;
  private final ContinuousRange knobRotation;
  private final NavX navX;

  private Drive drive;

  public RobotAlt(RobotDrive robotDrive, ContinuousRange knobRotation, NavX navX) {
    this.robotDrive = robotDrive;
    this.knobRotation = knobRotation;
    this.navX = navX;
  }

  public void robotInit() {
    Strongback.configure().recordNoEvents().recordNoData();
    DaveKnob knob = new DaveKnob(knobRotation, navX::getAngle);
    drive = new Drive(robotDrive, navX, () -> 0.0, () -> 0.0, knob);
  }

  public void teleopInit() {
    Strongback.start();
  }

  public void teleopPeriodic() {
    Feedback feedback = new Feedback(navX.getAngle());
    drive.onUpdate(feedback);
  }

  private static class DaveKnob implements ContinuousRange {
    private final ContinuousRange knobAngle;
    private final ContinuousRange gyroAngle;

    // PID constants
    private double kP = 0.03; // Proportional constant
    private double kI = 0.0;  // Integral constant
    private double kD = 0.06; // Derivative constant

    // PID variables
    private double prevError = 0.0; // The error from the previous loop
    private double integral = 0.0; // Error integrated over time

    private double setpoint; // The target orientation for the robot
    private double tolerance = 0.1; // The percent tolerance for the error to be considered on target
    private double maxOutput = 1.0;
    private double minOutput = -1.0;

    private boolean rotationLatch = false;

    public DaveKnob(ContinuousRange knobAngle, ContinuousRange gyroAngle) {
      this.knobAngle = knobAngle;
      this.gyroAngle = gyroAngle;
      this.setpoint = gyroAngle.read();
    }

    @Override
    public double read() {
      final double deltaTime = 100; // microseconds
      double error = knobAngle.read() - gyroAngle.read();
      double rotationLatchDeadband = 30;
      if(!rotationLatch && Math.abs(error) > rotationLatchDeadband) {
        error = 0;
      } else if(!rotationLatch && Math.abs(error) <= rotationLatchDeadband) {
        rotationLatch = true;
      }

      if(Math.abs(error) >= 300) {
        if(error > 0) {
          error -= 360;
        } else {
          error += 360;
        }
      }
      if(onTarget(error)) error = 0.0;
      integral += error;

      double result = (error * kP) + (integral * kI * deltaTime) + ((error - prevError) * kD / deltaTime);
      prevError = error;

      if(result > maxOutput) result = maxOutput;
      else if(result < minOutput) result = minOutput;
      return result;
    }

    // If the error is less than or equal to the tolerance it is on target
    private boolean onTarget(double error) {
      return Math.abs(error) <= setpoint * tolerance;
    }
  }
}
