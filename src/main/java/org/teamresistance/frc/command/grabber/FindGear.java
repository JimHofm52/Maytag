package org.teamresistance.frc.command.grabber;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.command.Command;
import org.teamresistance.frc.InvertibleDigitalInput;
import org.teamresistance.frc.Robot;
import org.teamresistance.frc.subsystem.grabber.Grabber;

/**
 * Created by shrey on 2/7/2017.
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

  @Override
  public void end() {

  }

}
