package org.teamresistance.frc.command.grabber;


import org.strongback.command.Command;
import org.teamresistance.frc.Robot;
import org.teamresistance.frc.subsystem.grabber.Grabber;

/**
 * Created by shrey on 2/18/2017.
 */
public class InterruptGear extends Command {

  @Override
  public void initialize() {
  }

  @Override
  public boolean execute() {
    return !Robot.coJoystick.getButton(2).isTriggered();
  }

  @Override
  public void end() {
//    Grabber.interrrupted = true;
  }
}
