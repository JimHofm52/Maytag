package org.teamresistance.frc.sensor.lift;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.teamresistance.frc.Robot;

import java.util.ArrayList;
import java.util.OptionalDouble;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionPipeline;

public class LiftPipeline implements VisionPipeline {
  private final LiftGrip pipeline;

  public LiftPipeline() {
    pipeline = new LiftGrip();
  }

  @Override
  public void process(Mat image) {
    pipeline.setsource0(image);
    pipeline.process();
  }

  ArrayList<MatOfPoint> unsafeGetConvexHulls() {
    return pipeline.convexHullsOutput();
  }

  OptionalDouble unsafeGetRelativeOffset() {
    // Grab the last output
    ArrayList<MatOfPoint> hulls = pipeline.convexHullsOutput();

    // Abort if there aren't only two tapes
    int numberOfContours = hulls.size();
    SmartDashboard.putNumber("Vision: Number of contours", numberOfContours);
    if (numberOfContours != 1) return OptionalDouble.empty();

    // Calculate the y offset, relative to the center with a domain of -1 to +1
    OptionalDouble averageCenterY = hulls.stream().mapToDouble(hull -> {
      Moments moments = Imgproc.moments(hull);

      // Camera is sideways! Use centerY; centerX is [m10 / m00], centerY is [m01 / m00]
      double targetCenterYPx = moments.get_m01() / moments.get_m00();
      double canvasCenterYPx = Robot.CameraConfig.HEIGHT / 2;

      // Relative y of the target, where 0 is the image center and +1 is the topmost edge
      return (targetCenterYPx - canvasCenterYPx) / canvasCenterYPx;
    }).average();

    SmartDashboard.putNumber("Vision: Relative Y", averageCenterY.orElse(-1));
    return averageCenterY;
  }
}