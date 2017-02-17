package org.teamresistance.frc.command.grabber;

import edu.wpi.first.wpilibj.SpeedController;
import org.strongback.command.Command;
import org.strongback.components.Motor;
import org.teamresistance.frc.InvertibleDigitalInput;

/**
 * Created by shrey on 2/7/2017.
 */
public class AlignGear extends Command {

  private final SpeedController rotateGearMotor;
  private final InvertibleDigitalInput gearAlignBannerSensor;

  public AlignGear(SpeedController rotateGear, InvertibleDigitalInput gearAlignBannerSensor) {
    super(gearAlignBannerSensor);
    this.rotateGearMotor = rotateGear;
    this.gearAlignBannerSensor = gearAlignBannerSensor;
  }

  @Override
  public boolean execute() {
    rotateGearMotor.set(0.25);
    return gearAlignBannerSensor.get();
  }

  public void interrupted() {
    end();
  }

  public void end() {
    rotateGearMotor.set(0);
  }

}
