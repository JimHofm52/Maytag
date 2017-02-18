package org.teamresistance.frc.command.grabber;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.command.Command;
import org.teamresistance.frc.InvertibleSolenoidWithPosition;
import org.teamresistance.frc.SingleSolenoid;

/**
 * Created by shrey on 2/6/2017.
 */
public class GearRetract extends Command {

  private final SingleSolenoid extendSolenoid;

  public GearRetract(SingleSolenoid extendSolenoid) {
    super(extendSolenoid);
    this.extendSolenoid = extendSolenoid;
  }

  @Override
  public boolean execute() {
    extendSolenoid.retract();
    SmartDashboard.putBoolean("Gear Retracted", extendSolenoid.isRetracted());
    return extendSolenoid.isRetracted();
  }

}
