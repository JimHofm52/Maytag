package org.teamresistance.frc.util;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class MotorSpy implements SpeedController {
  private final SpeedController motor;
  private final String smartDashboardKey;

  public MotorSpy(SpeedController motor, String smartDashboardKey) {
    this.motor = motor;
    this.smartDashboardKey = smartDashboardKey;
  }

  // Spy on the value we're setting the motor to ---------------------------------------------------

  @Override
  public void set(double speed) {
    SmartDashboard.putNumber(smartDashboardKey + ": setting speed to", speed);
    motor.set(speed);
  }

  // Below methods are all delegated ---------------------------------------------------------------

  @Override
  public double get() {
    return motor.get();
  }

  @Override
  public void setInverted(boolean isInverted) {
    motor.setInverted(isInverted);
  }

  @Override
  public boolean getInverted() {
    return motor.getInverted();
  }

  @Override
  public void disable() {
    motor.disable();
  }

  @Override
  public void stopMotor() {
    motor.stopMotor();
  }

  @Override
  public void pidWrite(double output) {
    motor.pidWrite(output);
  }
}
