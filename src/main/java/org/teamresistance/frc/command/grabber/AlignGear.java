package org.teamresistance.frc.command.grabber;

import org.strongback.command.Command;
import org.teamresistance.frc.hardware.component.InvertibleDigitalInput;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * @author Shreya Ravi
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
