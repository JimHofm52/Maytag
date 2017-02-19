package org.teamresistance.frc.hardware.component;

import org.strongback.command.Requirable;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * @author Shreya Ravi
 */
public class InvertibleDigitalInput implements Requirable {
  private final DigitalInput limitSwitch;
  private final boolean isInverted;

  public InvertibleDigitalInput(int channel, boolean isInverted) {
    this.isInverted = isInverted;
    limitSwitch = new DigitalInput(channel);
  }

  public boolean get() {
    return isInverted ? !limitSwitch.get() : limitSwitch.get();
  }
}
