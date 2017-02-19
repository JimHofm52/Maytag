package org.teamresistance.frc.util;

import org.strongback.control.SoftwarePIDController;
import org.strongback.control.SoftwarePIDController.SourceType;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 * TODO: Documentation (medium priority)
 * @author Rothanak So
 */
public class SynchronousPID implements LiveWindowSendable {
  private final SoftwarePIDController controller;
  private final Bridge<Double> inputBridge = new Bridge<>();
  private final Bridge<Double> outputBridge = new Bridge<>();
  private boolean isConfigured = false;

  public SynchronousPID(String name, SourceType type, double kP, double kI, double kD) {
    this.controller = new SoftwarePIDController(type, inputBridge::get, outputBridge::accept)
        .withGains(
            SmartDashboard.getNumber(name + "/p", kP),
            SmartDashboard.getNumber(name + "/i", kI),
            SmartDashboard.getNumber(name + "/d", kD))
        .enable();
    SmartDashboard.putData(name, this);
  }

  public SynchronousPID withConfigurations(Function<SoftwarePIDController,
      SoftwarePIDController> configurator) {
    configurator.apply(controller);
    isConfigured = true;
    return this;
  }

  public boolean isWithinTolerance() {
    return controller.isWithinTolerance();
  }

  public double calculate(double input) {
    if (!isConfigured)
      throw new IllegalStateException("PID not configured. Did you remember to call " +
          "`withConfigurations`? Refer to the documentation for usage notes.");

    inputBridge.accept(input);
    controller.computeOutput();
    return outputBridge.get();
  }

  @Override
  public void updateTable() {
    controller.updateTable();
  }

  @Override
  public void startLiveWindowMode() {
    controller.startLiveWindowMode();
  }

  @Override
  public void stopLiveWindowMode() {
    controller.stopLiveWindowMode();
  }

  @Override
  public void initTable(ITable subtable) {
    controller.initTable(subtable);
  }

  @Override
  public ITable getTable() {
    return controller.getTable();
  }

  @Override
  public String getSmartDashboardType() {
    return controller.getSmartDashboardType();
  }

  /**
   * Serves as a bridge between a Consumer and a Supplier. Use it when you need to manually supply
   * values to a {@link Supplier} or manually read the value from a {@link Consumer}.
   * <p>
   * Author's note: This is basically only used to turn those crazy asynchronous PIDs into synchronous
   * PIDs, since they only take callbacks and don't actually have a method that returns the computed
   * value. See: {@link SoftwarePIDController}, which then is coerced into {@link SynchronousPID}.
   *
   * @param <T> the type of value being consumed and supplied, e.g. Double
   * @author Rothanak So
   */
  final class Bridge<T> implements Consumer<T>, Supplier<T> {
    private T value;

    @Override
    public void accept(T value) {
      this.value = value;
    }

    @Override
    public T get() {
      return value;
    }
  }
}
