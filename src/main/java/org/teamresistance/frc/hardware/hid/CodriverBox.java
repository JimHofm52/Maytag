package org.teamresistance.frc.hardware.hid;

import edu.wpi.first.wpilibj.Joystick;

/**
 * @author Frank McCoy
 */
public class CodriverBox {

  private Joystick input;

  private boolean[] previousButtonState;
  private boolean[] currentButtonState;

  private int numButtons;

  public static final int CLAW_TOP = 16;
  public static final int CLAW_CENTER = 19;
  public static final int BIN_LIFTIN_FORWARD = 15;
  public static final int BIN_LIFTIN_BACK = 3;
  public static final int SHUTTLE_HIGH = 4;
  public static final int SHUTTLE_LOW = 12;
  public static final int BIN_LIFTIN_PICKUP = 2;
  public static final int BIN_LIFTIN_INDEX_DOWN = 17;
  public static final int BIN_LIFTIN_HOME = 18;
  //	public static final int BIN_LIFTIN_TOP = 14;
  public static final int BIN_LIFTIN_DROP = 5;
  public static final int CLAW_TOGGLE = 14;

  private double previousWristPosition;
  private double wristSpeed;

  private double previousShuttlePosition;
  private double shuttleSpeed;

  public CodriverBox(int port) {
    input = new Joystick(port);

    numButtons = input.getButtonCount();

    previousButtonState = new boolean[20];
    currentButtonState = new boolean[20];

    for(int i = 0; i < numButtons; i++) {
      previousButtonState[i] = input.getRawButton(i + 1);
      currentButtonState[i] = input.getRawButton(i + 1);
    }

    previousWristPosition = input.getX();
    previousShuttlePosition = input.getY();
  }

  public void update(double deltaTime) {
    for(int i = 0; i < numButtons; i++) {
      previousButtonState[i] = currentButtonState[i];
      currentButtonState[i] = input.getRawButton(i + 1);
    }
  }

  public boolean getButton(int button) {
//		return currentButtonState.get(button);
    return input.getRawButton(button + 1);
  }

  public boolean onButtonDown(int button) {
    return !previousButtonState[button] && currentButtonState[button];
  }

  public boolean isButtonDown(int button) {
    return currentButtonState[button];
  }

  public boolean onButtonUp(int button) {
    return previousButtonState[button] && !currentButtonState[button];
  }

  public double getRotation() {
    double result = input.getRawAxis(2);
    result *= -180;
    return result;
  }
}
