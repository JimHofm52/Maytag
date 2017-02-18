package org.teamresistance.frc.subsystem.climb;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;
import org.strongback.command.Command;
import org.strongback.command.Requirable;
import org.strongback.components.Motor;
import org.strongback.components.Stoppable;
import org.teamresistance.frc.command.climb.ClimbRope;

/**
 * @author Sabrina
 */
public class Climber implements Requirable, Stoppable {
  private static final double CLIMB_SPEED = 1.0;

  private final SpeedController climberMotor;
  private final PowerDistributionPanel pdp;
  private final int channel;

  public Climber(SpeedController climberMotor, PowerDistributionPanel pdp, int channel) {
    this.climberMotor = climberMotor;
    this.pdp = pdp;
    this.channel = channel;
  }

  public Command climbRope(double currentThreshold, double timeThresholdSeconds) {
    return new ClimbRope(this, currentThreshold, timeThresholdSeconds);
  }

  public double getCurrent() {
    return pdp.getCurrent(channel);
  }

  public void startClimbing() {
    climberMotor.set(CLIMB_SPEED);
  }

  @Override
  public void stop() {
    climberMotor.set(0);
  }
}
