package org.teamresistance.frc.hardware.component;

import org.strongback.command.Requirable;

/**
 * @author Shreya Ravi
 */
public interface SingleSolenoid extends Requirable{
  void extend();
  void retract();
  boolean isExtended();
  boolean isRetracted();
}
