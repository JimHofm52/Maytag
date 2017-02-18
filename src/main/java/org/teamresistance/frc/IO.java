package org.teamresistance.frc;

import edu.wpi.first.wpilibj.*;

/**
 * @author Rothanak So
 */
public class IO {

  private static final class PWM {

    // Drive assignments
    private static final int LF_WHEEL = 7;
    private static final int LR_WHEEL = 8;
    private static final int RF_WHEEL = 3;
    private static final int RR_WHEEL = 1;

    // Shooter assignments
    private static final int SHOOTER_WHEEL = 4;
    private static final int FEEDER_SNORFLER = 2;
    private static final int AGITATOR = 6;

    // Snorfler, grabulator, and climber assignments
    private static final int BALL_SNORFLER = 0;
    private static final int GEAR_ROTATOR = 5;
    private static final int CLIMBER = 9;
  }

  private static final class DIO {
    private static final int GRABULATOR_RETRACTED_LIMIT = 0;
    private static final int GEAR_ALIGN_BANNER = 2;
    private static final int GEAR_FIND_BANNER = 1;
  }

  private static final class PCM {
    private static final int GEAR_EXTEND_SOLENOID = 0;
    private static final int GEAR_ROTATE_SOLENOID = 1;
    private static final int GEAR_GRIP_SOLENOID = 2;
  }

  private static final class CAN {
    private static final int PDP = 0;
    private static final int PCM = 1;
  }

  private static final class RELAY {
    private static final int COMPRESSOR_RELAY = 0;
    private static final int LIGHTS = 1;
  }

  public static final class PDP {
    public static final int CLIMBER = 8;
  }

  // Relay for green LEDs
  public static final Relay cameraLights = new Relay(RELAY.LIGHTS);

  // Power distribution panel (PDP)
  public static final PowerDistributionPanel powerPanel = new PowerDistributionPanel(CAN.PDP);

  // NavX-MXP navigation sensor
  public static final NavX navX = new NavX(SPI.Port.kMXP);

  // Drive motors
  public static final SpeedController leftFrontMotor = new Victor(PWM.LF_WHEEL);
  public static final SpeedController leftRearMotor = new Victor(PWM.LR_WHEEL);
  public static final SpeedController rightFrontMotor = new Victor(PWM.RF_WHEEL);
  public static final SpeedController rightRearMotor = new Victor(PWM.RR_WHEEL);

  static {
    rightFrontMotor.setInverted(true);
    rightRearMotor.setInverted(true);
  }

  // Shooter motors
  public static final SpeedController shooterMotor = new VictorSP(PWM.SHOOTER_WHEEL);
  public static final SpeedController feederMotor = new VictorSP(PWM.FEEDER_SNORFLER);
  public static final SpeedController agitatorMotor = new VictorSP(PWM.AGITATOR);

  static {
    feederMotor.setInverted(true);
  }

  // Snorfler, gear, and climber motors
  public static final SpeedController snorflerMotor = new VictorSP(PWM.BALL_SNORFLER);
  public static final SpeedController gearRotatorMotor = new VictorSP(PWM.GEAR_ROTATOR);
  public static final SpeedController climberMotor = new VictorSP(PWM.CLIMBER);

  static {
    snorflerMotor.setInverted(true);
    climberMotor.setInverted(true);
  }

  // Banner Sensors (for Grabulator)
  public static final InvertibleDigitalInput gearFindBanner =
      new InvertibleDigitalInput(DIO.GEAR_FIND_BANNER, true);
  public static final InvertibleDigitalInput gearAlignBanner =
      new InvertibleDigitalInput(DIO.GEAR_ALIGN_BANNER, true);

  // Gear Limit Switch (check retracted)
  public static final InvertibleDigitalInput gearRetractedLimit =
      new InvertibleDigitalInput(DIO.GRABULATOR_RETRACTED_LIMIT, true);

  // Pneumatic Cylinders (controlled via Solenoids)
  public static final InvertibleSolenoid gripSolenoid =
      new InvertibleSolenoid(CAN.PCM, PCM.GEAR_GRIP_SOLENOID, false);
  public static final InvertibleSolenoidWithPosition extendSolenoid =
      new InvertibleSolenoidWithPosition(CAN.PCM, PCM.GEAR_EXTEND_SOLENOID, false, gearRetractedLimit);
  public static final InvertibleSolenoid rotateSolenoid =
      new InvertibleSolenoid(CAN.PCM, PCM.GEAR_ROTATE_SOLENOID, false);

  // Compressor and Relay
  public static final Compressor compressor = new Compressor(CAN.PCM);
  public static final Relay compressorRelay = new Relay(RELAY.COMPRESSOR_RELAY);
}
