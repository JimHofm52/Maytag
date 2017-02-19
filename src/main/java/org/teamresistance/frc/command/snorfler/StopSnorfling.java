package org.teamresistance.frc.command.snorfler;

import org.strongback.command.Command;
import org.teamresistance.frc.subsystem.snorfler.Snorfler;

/**
 * @author Tarik Brown .
 */
public class StopSnorfling extends Command {
  private final Snorfler snorfler;

  public StopSnorfling(Snorfler snorfler) {
    this.snorfler = snorfler;
  }

  @Override
  public boolean execute() {
    snorfler.stopReversing();
    return false;
  }

  @Override
  public void interrupted() {
    end();
  }

  @Override
  public void end() {
    snorfler.stop();
  }
}
