package org.teamresistance.frc.command.grabber;


import org.strongback.command.Command;
import org.teamresistance.frc.Robot;

/**
 * @author Shreya Ravi
 */
public class InterruptGear extends Command {

  @Override
  public boolean execute() {
    return !Robot.coJoystick.getButton(2).isTriggered();
  }

  @Override
  public void end() {
//    Grabber.interrrupted = true;
  }
}
