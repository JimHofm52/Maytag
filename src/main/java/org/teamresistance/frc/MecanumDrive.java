package org.teamresistance.frc;

/**
 * Created by Frank on 2/18/2017.
 */
import edu.wpi.first.wpilibj.RobotDrive;

public class MecanumDrive {
  private RobotDrive drive;
  private NavX gyro;

  // PID constants
  private double kP; // Proportional constant
  private double kI = 0.0; // Integral constant
  private double kD = 0.0; // Derivative constant

  // PID variables
  private double prevError = 0.0; // The error from the previous loop
  private double integral = 0.0; // Error integrated over time

  private long prevTime;

  private double setpoint; // The target orientation for the robot
  private double tolerance = 0.1; // The percent tolerance for the error to be considered on target
  private double maxOutput = 1.0;
  private double minOutput = -1.0;

  private DriveType driveState = DriveType.KNOB_FIELD;

  private boolean rotationLatch = false;

  public MecanumDrive(RobotDrive drive, NavX gyro) {
    this.drive = drive;
    this.gyro = gyro;
  }

  public void init(double setpoint, double p, double i, double d) {
    this.setpoint = setpoint;
    this.kP = p;
    this.kI = i;
    this.kD = d;
    this.prevError = 0.0;
    this.integral = 0.0;
    this.prevTime = System.currentTimeMillis();
  }

  public void drive(double x, double y, double rotation, double angle) {
    long curTime = System.currentTimeMillis();
    double deltaTime = (curTime - prevTime) / 1000.0;

    switch(driveState) {
      case KNOB_FIELD:
        double error = angle - gyro.getAngle();
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

        drive.mecanumDrive_Cartesian(x, y, result, angle);
        break;
      case STICK_FIELD:
        drive.mecanumDrive_Cartesian(x, y, rotation, angle);
        break;
    }
  }

  // If the error is less than or equal to the tolerance it is on target
  private boolean onTarget(double error) {
    return Math.abs(error) <= setpoint * tolerance;
  }

  private enum DriveType {
    KNOB_FIELD,
    STICK_FIELD
  }
}
