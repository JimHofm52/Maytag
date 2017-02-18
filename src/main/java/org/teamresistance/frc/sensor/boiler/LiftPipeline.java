package org.teamresistance.frc.sensor.boiler;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.OptionalDouble;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionPipeline;

public class LiftPipeline implements VisionPipeline {
  private static final int CANVAS_WIDTH_PX = 320;
  private final GearGrip pipeline;

  public LiftPipeline() {
    pipeline = new GearGrip();
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

    // Calculate the x offset, relative to the center with a domain of -1 to +1
    return hulls.stream().mapToDouble(hull -> {
      Moments moments = Imgproc.moments(hull);

      double targetCenterXPx = moments.get_m10() / moments.get_m00();
      double canvasCenterXPx = CANVAS_WIDTH_PX / 2;

      // Relative x of the target, where 0 is the image center and +1 is the rightmost edge
      return (targetCenterXPx - canvasCenterXPx) / canvasCenterXPx;
    }).average();
  }
}