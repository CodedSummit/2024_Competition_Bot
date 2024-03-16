// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.Map;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;

/**
 * Models the mechanism that shoots Notes
 * 
 * Has a Note (or not) from IntakeSubsystem
 * (TBD - this may be fixed prior to match) Shooter can adjust height - has an absolute encoder and a motor to change the
 * height.
 * The height/angle adjustment will need a PID controller. (TBD - may be fixed prior to match and not moveable)
 * May not directly know it has a note
 * It'll spin up prior to the Intake feeding it into the shooter.  Command or initiator is responsible 
 * for managing any spinup time
 * Spins at an unregulated velocity- a fixed rate determined a priori for expected target distance.
 * 
 */
public class NoteShooterSubsystem extends SubsystemBase {

  private TalonFX m_motor = new TalonFX(ShooterConstants.kShooterCanbusID, "rio");
  private double m_shooterSpeed;
  private GenericEntry nt_shooterSpeed;

  /** Creates a new VisionSubsystem. */
  public NoteShooterSubsystem() {
    m_motor.setInverted(true);
     loadPreferences();
     setupShuffleboard();
  }

  private void loadPreferences() {
    m_shooterSpeed = Preferences.getDouble(ShooterConstants.kShooterSpeedPrefKey, ShooterConstants.kShooterSpeed);
  }

  private void setupShuffleboard() {

    ShuffleboardTab intake = Shuffleboard.getTab("Shooter");

    nt_shooterSpeed = intake.addPersistent("Shooter speed", m_shooterSpeed)
        .withSize(3, 1)
        .withWidget(BuiltInWidgets.kNumberSlider)
        .withProperties(Map.of("min", 0, "max", 1))
        .getEntry();

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
spinUp();
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }

  public double getShooterSpeed() {

    if (m_shooterSpeed != nt_shooterSpeed.getDouble(ShooterConstants.kShooterSpeed)) {
      // get the value from the Shuffleboard slider.  If it changed salt it away for future reboots
      m_shooterSpeed = nt_shooterSpeed.getDouble(ShooterConstants.kShooterSpeed);
      Preferences.setDouble(ShooterConstants.kShooterSpeedPrefKey, m_shooterSpeed);
    }
    return m_shooterSpeed;
  } 
  public void spinUp() {
    // start the motor at some pre-defined constant speed
    m_motor.set(getShooterSpeed());
  }

  public void stop() {
    m_motor.set(0.0);
  }

  
}
