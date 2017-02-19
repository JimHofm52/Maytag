package org.teamresistance.frc.subsystem.grabber;

import org.strongback.command.Command;
import org.strongback.command.CommandGroup;
import org.strongback.command.Requirable;
import org.teamresistance.frc.InvertibleDigitalInput;
import org.teamresistance.frc.SingleSolenoid;
import org.teamresistance.frc.command.grabber.AlignGear;
import org.teamresistance.frc.command.grabber.FindGear;
import org.teamresistance.frc.command.grabber.GearExtend;
import org.teamresistance.frc.command.grabber.GearRetract;
import org.teamresistance.frc.command.grabber.GrabGear;
import org.teamresistance.frc.command.grabber.InterruptGear;
import org.teamresistance.frc.command.grabber.ReleaseGear;
import org.teamresistance.frc.command.grabber.RotateDown;
import org.teamresistance.frc.command.grabber.RotateUp;
import org.teamresistance.frc.command.grabber.WaitCommand;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * Created by shrey on 2/6/2017.
 */
public class Grabber implements Requirable {

  private final SingleSolenoid gripSolenoid;
  private final SingleSolenoid extendSolenoid;
  private final SingleSolenoid rotateSolenoid;
  private final SpeedController rotateGearMotor;
  private final InvertibleDigitalInput gearPresentBannerSensor;
  private final InvertibleDigitalInput gearAlignBannerSensor;

//  public static boolean interrrupted = false;

  public Grabber(SingleSolenoid gripSolenoid,
                 SingleSolenoid extendSolenoid,
                 SingleSolenoid rotateSolenoid,
                 SpeedController rotateGearMotor,
                 InvertibleDigitalInput gearPresentBannerSensor,
                 InvertibleDigitalInput gearAlignBannerSensor) {
    this.gripSolenoid = gripSolenoid;
    this.extendSolenoid = extendSolenoid;
    this.rotateSolenoid = rotateSolenoid;
    this.rotateGearMotor = rotateGearMotor;
    this.gearPresentBannerSensor = gearPresentBannerSensor;
    this.gearAlignBannerSensor = gearAlignBannerSensor;
  }

//  public static boolean interrupted = false;

  private CommandGroup lookForGear() {
    return CommandGroup.runSequentially(
        new GearRetract(extendSolenoid),
        CommandGroup.runSimultaneously(
            new RotateDown(1.0, extendSolenoid, rotateSolenoid),
            new ReleaseGear(1.0, gripSolenoid)
        ),
        new FindGear(gearPresentBannerSensor),
        new WaitCommand(0.1)
    );
  }

  public CommandGroup reset() {
    return CommandGroup.runSequentially(
        new GearRetract(extendSolenoid),
        CommandGroup.runSimultaneously(
            new RotateUp(1.0, extendSolenoid, rotateSolenoid),
            new GrabGear(0.1, gripSolenoid)
        )
    );
  }

  public CommandGroup pickup() {
    return CommandGroup.runSequentially(
        new GearExtend(0.5, extendSolenoid),
        new GrabGear(0.1, gripSolenoid),
        new GearRetract(extendSolenoid),
        CommandGroup.runSimultaneously (
            new RotateUp(1.0, extendSolenoid, rotateSolenoid),
            new AlignGear(rotateGearMotor, gearAlignBannerSensor)
        )
    );
  }



  private CommandGroup pickupGear() {
    return null;
  }

  public CommandGroup pickUpGearSequence() {
    return CommandGroup.runSequentially(
        lookForGear(),
        pickupGear()
    );
  }




//  public CommandGroup pickupGear() {
//    return CommandGroup.runSequentially(
//        new GearRetract(extendSolenoid),
//        CommandGroup.runSimultaneously(
//            new RotateDown(1.0, extendSolenoid, rotateSolenoid),
//            new ReleaseGear(1.0, gripSolenoid)
//        ),
//        new FindGear(gearPresentBannerSensor),
//        new GearExtend(0.5, extendSolenoid),
//        new GrabGear(0.1, gripSolenoid),
//        new GearRetract(extendSolenoid),
//        new RotateUp(1.0, extendSolenoid, rotateSolenoid),
//        new AlignGear(rotateGearMotor, gearAlignBannerSensor)
//    );
//  }

  public Command interruptSequence() {
    return new InterruptGear();
  }


  public CommandGroup deliverGear() {
    return CommandGroup.runSequentially(
        new AlignGear(rotateGearMotor, gearAlignBannerSensor),
        new ReleaseGear(1.0, gripSolenoid)
    );
  }

}
