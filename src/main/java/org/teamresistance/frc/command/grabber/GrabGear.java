package org.teamresistance.frc.command.grabber;


import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import org.strongback.command.Command;
import org.teamresistance.frc.InvertibleSolenoid;
import org.teamresistance.frc.SingleSolenoid;

/**
 * Created by shrey on 2/7/2017.
 */
public class GrabGear extends Command {

  private final SingleSolenoid gripSolenoid;

  public GrabGear(double timeLimit, SingleSolenoid gripSolenoid) {
    super(timeLimit, gripSolenoid);
    this.gripSolenoid = gripSolenoid;
  }

  @Override
  public boolean execute() {
    gripSolenoid.retract();
    return false;
  }

}
