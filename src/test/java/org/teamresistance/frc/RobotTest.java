package org.teamresistance.frc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.strongback.components.ui.ContinuousRange;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
class RobotTest {
  @Mock ContinuousRange knobRotation;
  @Mock NavX navX;

  // Retrofitted Dave Knob
  @Mock RobotDrive robotDriveA;
  @Captor ArgumentCaptor<Double> xCaptureA;
  @Captor ArgumentCaptor<Double> yCaptureA;
  @Captor ArgumentCaptor<Double> rotCaptureA;
  @Captor ArgumentCaptor<Double> gyroCaptureA;
  private Robot robotA;

  // New Dave Knob
  @Mock RobotDrive robotDriveB;
  @Captor ArgumentCaptor<Double> xCaptureB;
  @Captor ArgumentCaptor<Double> yCaptureB;
  @Captor ArgumentCaptor<Double> rotCaptureB;
  @Captor ArgumentCaptor<Double> gyroCaptureB;
  private RobotAlt robotB;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);
    robotA = new Robot(robotDriveA, knobRotation, navX);
    robotB = new RobotAlt(robotDriveB, knobRotation, navX);

    Timer.SetImplementation(new DummyTimer()); // so code compiles
  }

  @Test
  void ensureParity() {
    when(navX.getAngle()).thenReturn(Double.valueOf(20));
    when(knobRotation.read()).thenReturn(Double.valueOf(47));

    robotA.robotInit();
    robotB.robotInit();

    robotA.teleopInit();
    robotB.teleopInit();

    // Test combinations for { gyro, knob }
    double[][] testCombinations = {
        { 0, 0 }, { 0, 0 },     // edge case
        { 30, 40 }, { 40, 30 }, // normal
        { 30, 90 }, { 90, 30 }, // normal
        { 0, 360 }, { 360, 0 }, // edge case
        { 1, 359 }, { 359, 1 }, // edge case
        { 0, 40 }, { 40, 0 },   // normal
        { 180, 0 }, { 0, 180 }  // edge case
    };

    for (int i = 0; i < testCombinations.length; i++) {
      double gyroAngle = testCombinations[i][0];
      double knobAngle = testCombinations[i][1];

      when(navX.getAngle()).thenReturn(gyroAngle);
      when(knobRotation.read()).thenReturn(knobAngle);

      robotA.teleopPeriodic();
      robotB.teleopPeriodic();

      verify(robotDriveA, atLeastOnce()).mecanumDrive_Cartesian(
          xCaptureA.capture(),
          yCaptureA.capture(),
          rotCaptureA.capture(),
          gyroCaptureA.capture()
      );
      verify(robotDriveB, atLeastOnce()).mecanumDrive_Cartesian(
          xCaptureB.capture(),
          yCaptureB.capture(),
          rotCaptureB.capture(),
          gyroCaptureB.capture()
      );

      System.out.println("[Case " + i + "] Gyro: + " + gyroAngle + " Knob: " + knobAngle);
      assertEquals(xCaptureA.getValue(), xCaptureB.getValue(), "X " + i);
      assertEquals(yCaptureA.getValue(), yCaptureB.getValue(), "Y " + i);
      assertEquals(rotCaptureA.getValue(), rotCaptureB.getValue(), "Rot " + i);
      assertEquals(gyroCaptureA.getValue(), gyroCaptureB.getValue(), "Gyro " + i);
      System.out.println("Good");
    }
  }

  private static class DummyTimer implements Timer.StaticInterface {

    @Override
    public double getFPGATimestamp() {
      return 0;
    }

    @Override
    public double getMatchTime() {
      return 0;
    }

    @Override
    public void delay(double seconds) {

    }

    @Override
    public Timer.Interface newTimer() {
      return null;
    }
  }
}