package org.teamresistance.frc.command.grabber;

import org.strongback.command.Command;
import org.teamresistance.frc.Robot;
import org.teamresistance.frc.hardware.component.InvertibleDigitalInput;

/**
 * @author Shreya Ravi
 */
public class FindGear extends Command {
  private final InvertibleDigitalInput gearPresentBannerSensor;

  public FindGear(InvertibleDigitalInput gearPresentBannerSensor) {
    super(gearPresentBannerSensor);
    this.gearPresentBannerSensor = gearPresentBannerSensor;
  }

  @Override
  public boolean execute() {
//    return (gearPresentBannerSensor.get() || Grabber.interrrupted);
    return (gearPresentBannerSensor.get() || !Robot.test);
  }
}
