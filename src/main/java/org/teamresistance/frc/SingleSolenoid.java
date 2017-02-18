package org.teamresistance.frc;

import org.strongback.command.Requirable;

/**
 * Created by shrey on 2/6/2017.
 */
public interface SingleSolenoid extends Requirable{
  void extend();
  void retract();
  boolean isExtended();
  boolean isRetracted();
}
