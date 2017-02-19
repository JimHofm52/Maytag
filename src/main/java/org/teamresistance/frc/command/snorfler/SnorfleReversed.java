package org.teamresistance.frc.command.snorfler;

import org.strongback.command.Command;
import org.teamresistance.frc.subsystem.snorfler.Snorfler;

/**
 * @author Tarik C. Brown
 */
public class SnorfleReversed extends Command {
  private final Snorfler snorfler;

  public SnorfleReversed(Snorfler snorfler) {
    this.snorfler = snorfler;
  }

  @Override
  public boolean execute() {
    snorfler.startReversing();
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
