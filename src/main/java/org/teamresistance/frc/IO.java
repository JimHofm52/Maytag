package org.teamresistance.frc;

import edu.wpi.first.wpilibj.Relay;
import org.strongback.components.Motor;
import org.strongback.components.PowerPanel;
import org.strongback.components.TalonSRX;
import org.strongback.hardware.Hardware;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.SPI;

import static org.strongback.hardware.Hardware.Motors.talonSRX;
import static org.strongback.hardware.Hardware.Motors.victorSP;

/**
 * @author Rothanak So
 */
public class IO {

  private static final class PWM {

    // Drive assignments
    private static final int LF_WHEEL = 0;
    private static final int LR_WHEEL = 1;
    private static final int RF_WHEEL = 2;
    private static final int RR_WHEEL = 3;

    // Shooter assignments
    private static final int SHOOTER_WHEEL = 4; // TODO: not Talon?
    private static final int SHOOTER_CONVEYOR = 5;
    private static final int SHOOTER_AGITATOR = 6;

    // Snorfler, grabulator, and climber assignments
    private static final int BALL_SNORFLER = 7;
    private static final int GRABULATOR_ROTATOR = 8;
    private static final int CLIMBER = 9;
  }

  private static final class DIO {
    private static final int GRABULATOR_RETRACTED_LIMIT = 0;
    private static final int GEAR_ALIGN_BANNER = 1;
    private static final int GEAR_FIND_BANNER = 2;
  }

  private static final class PCM {
    private static final int GEAR_EXTEND_SOLENOID = 0;
    private static final int GEAR_ROTATE_SOLENOID = 1;
    private static final int GEAR_GRIP_SOLENOID = 2;
  }

  private static final class CAN {
    private static final int SHOOTER_WHEEL = 1; // TODO: verify
  }

  private static final class RELAY {
    private static final int COMPRESSOR_RELAY = 0;
  }

  public static final class PDP {
    public static final int CLIMBER = 8;
  }

  // Power distribution panel
  public static final PowerPanel powerPanel = Hardware.powerPanel();

  // NavX-MXP navigation sensor
  public static final NavX navX = new NavX(SPI.Port.kMXP);

  // Drive motors
  public static final Motor lfMotor = victorSP(PWM.LF_WHEEL);
  public static final Motor rfMotor = victorSP(PWM.RF_WHEEL);
  public static final Motor rLMotor = victorSP(PWM.LR_WHEEL);
  public static final Motor rrMotor = victorSP(PWM.RR_WHEEL);

  // Shooter motors
  public static final TalonSRX shooterMotor = talonSRX(CAN.SHOOTER_WHEEL);
  public static final Motor shooterConveyorMotor = victorSP(PWM.SHOOTER_CONVEYOR);
  public static final Motor shooterAgitatorMotor = victorSP(PWM.SHOOTER_AGITATOR);

  // Snorfler, gear, and climber motors
  public static final Motor snorflerMotor = victorSP(PWM.BALL_SNORFLER);
  public static final Motor gearRotatorMotor = victorSP(PWM.GRABULATOR_ROTATOR);
  public static final Motor climberMotor = victorSP(PWM.CLIMBER);

  // Banner Sensors (for Grabulator)
  public static final InvertibleDigitalInput gearFindBanner =
      new InvertibleDigitalInput(DIO.GEAR_FIND_BANNER, false);
  public static final InvertibleDigitalInput gearAlignBanner =
      new InvertibleDigitalInput(DIO.GEAR_ALIGN_BANNER, false);

  // Gear Limit Switch (check retracted)
  private static final InvertibleDigitalInput gearRetractedLimit =
      new InvertibleDigitalInput(DIO.GRABULATOR_RETRACTED_LIMIT, false);

  // Pneumatic Cylinders (controlled via Solenoids)
  public static final InvertibleSolenoid gripSolenoid =
      new InvertibleSolenoid(PCM.GEAR_GRIP_SOLENOID, false);
  public static final InvertibleSolenoidWithPosition extendSolenoid =
      new InvertibleSolenoidWithPosition(PCM.GEAR_EXTEND_SOLENOID, false, gearRetractedLimit);
  public static final InvertibleSolenoid rotateSolenoid =
      new InvertibleSolenoid(PCM.GEAR_ROTATE_SOLENOID, false);

  // Compressor
  public static final Compressor compressor = new Compressor();
  public static final Relay compressorRelay = new Relay(RELAY.COMPRESSOR_RELAY);

}
