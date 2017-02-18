package org.teamresistance.frc.sensor.lift;

import org.opencv.core.MatOfPoint;

import java.util.ArrayList;
import java.util.OptionalDouble;

import edu.wpi.first.wpilibj.vision.VisionRunner;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class LiftListener implements LiftSensor, VisionRunner.Listener<LiftPipeline> {
  private boolean pipelineRan;
  private OptionalDouble relativeOffset;

  private final Object visionLock = new Object();
  private ArrayList<MatOfPoint> hulls;

  @Override
  public void copyPipelineOutputs(LiftPipeline pipeline) {
    synchronized (visionLock) {
      pipelineRan = true;
      relativeOffset = pipeline.unsafeGetRelativeOffset();
      hulls = pipeline.unsafeGetConvexHulls();
    }
  }

  @Override
  public OptionalDouble getRelativeOffset() {
    synchronized (visionLock) {
      // Ensure the pipeline has run
      if (!pipelineRan) return OptionalDouble.empty();
      return relativeOffset;
    }
  }

  public ArrayList<MatOfPoint> getHulls() {
    synchronized (visionLock) {
      // Ensure the pipeline has run
      if (!pipelineRan) return new ArrayList<>();
      return hulls;
    }
  }
}
