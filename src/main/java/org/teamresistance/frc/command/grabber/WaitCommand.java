package org.teamresistance.frc.command.grabber;


import org.strongback.command.Command;

/**
 * Created by shrey on 2/18/2017.
 */
public class WaitCommand extends Command {

  public WaitCommand(double time) {
    super(time);
  }

  @Override
  public boolean execute() {
    return true;
  }
}
