package org.teamresistance.frc.sensor.lift;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.teamresistance.frc.Robot;

import java.util.ArrayList;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.wpilibj.CameraServer;

public class StreamProcessedVideo implements Runnable {

  private final VideoSource camera;
  private final LiftListener liftListener;

  public StreamProcessedVideo(VideoSource camera, LiftListener liftListener) {
    this.camera = camera;
    this.liftListener = liftListener;
  }

  @Override
  public void run() {
    // FIXME: May have been causing problems earlier. Not really needed anyway, so it's "off" (see teleopInit)
    // This entire thread is only responsible for outputting post-processed images to the
    // SmartDashboard. It doesn't do any vision processing itself--the VisionThread handles that.
    // Don't forget to call run() after instantiating this thread.
    CvSink inputSource = CameraServer.getInstance().getVideo(camera);

    // Save bandwidth by ensuring inputSource res == outputStream res
    CvSource outputStream = CameraServer.getInstance()
        .putVideo("Hello Driver", Robot.CameraConfig.WIDTH, Robot.CameraConfig.HEIGHT);

    // Convenient color palette for drawing our shapes (BGR format)
    final Scalar green = new Scalar(0, 255, 0);
    final Scalar yellow = new Scalar(0, 255, 255);
    final Scalar blue = new Scalar(255, 0, 0);

    while (!Thread.interrupted()) {
      Mat grabbedFrame = new Mat();
      inputSource.grabFrame(grabbedFrame);

      // Copy the image to a new reference. Leave the original reference alone in case the boiler
      // processing code happens to be holding the exact same reference... because C.
      Mat image = grabbedFrame.clone();

      // Steal the most recently computed hulls from the pipeline listener
      ArrayList<MatOfPoint> convexHulls = liftListener.getHulls();

      // Draw the raw convex hulls
      Imgproc.drawContours(image, convexHulls, -1, green, 2);

      // Draw the bounding boxes
      convexHulls.forEach(hull -> {
        Rect rect = Imgproc.boundingRect(hull);
        Imgproc.rectangle(image, rect.tl(), rect.br(), yellow, 2);
      });

      // Draw a friendly circle regardless of if there are hulls -- for troubleshooting
      Imgproc.circle(image, new Point(50, 50), 50, blue, 2);

      // Notifies the downstream sinks
      outputStream.putFrame(image);
    }
  }
}
