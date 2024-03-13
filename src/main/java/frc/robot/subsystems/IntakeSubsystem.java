// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;

import java.util.Map;

import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.commands.IntakeNoteCommand;

/**
 * The subsystem responsible for picking up Notes
 * 
 * Knows if it holds a Note or not
 * Can release a Note to other systems (or maybe just know that it was taken)
 * When releasing the Note to the Shooter subsystem, that triggers the shot (i.e. Shooter 
 * does not hold the Note)
 * 
 * 
 */
public class IntakeSubsystem extends SubsystemBase {

  private boolean m_hasNote = false; // is known to be holding a Note
  private CANSparkMax m_motor  = new CANSparkMax(IntakeConstants.kIntakeCanbusID, MotorType.kBrushless);
  private DigitalInput m_beamSwitch = new DigitalInput(IntakeConstants.kIntakeBeambreakID);
  // TODO - use the beam break to know a Note is picked up
  private double m_intakeSpeed = IntakeConstants.kIntakeSpeed;
  private GenericEntry nt_intakeSpeed;

  public IntakeSubsystem() {
    setupShuffleboard();
  }

  private void setupShuffleboard() {
    
    ShuffleboardTab intake = Shuffleboard.getTab("Intake");
    try {
      nt_intakeSpeed = intake.add("Intake speed", 0.0)
        .withSize(3,1)
        .withWidget(BuiltInWidgets.kNumberSlider)
        .withProperties(Map.of("min", -1, "max", 1))
        .getEntry();
    } catch (Exception e) {// eat it.  for some reason it fails if the tab exists
    }
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    m_hasNote = !m_beamSwitch.get();  // assumes switch "true" means the beam is NOT broken (Thus no Note)
    // TODO - we could consider lighting up the LED strip if we just picked up a Note
    var d = getIntakeSpeed();
    System.out.println("    intake speed:"+m_intakeSpeed);
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }

  public double getIntakeSpeed() {
    m_intakeSpeed = nt_intakeSpeed.getDouble(IntakeConstants.kIntakeSpeed);
    return m_intakeSpeed;
  }
  public boolean hasNote() {
    // TODO - implement. Return t/f if a Note is in the handler
    return m_hasNote;
  }

  /**
   * Starts up the intake motor

  */
  public void setSpeed() {
    // TODO - implement
    m_motor.set(getIntakeSpeed());
  }

  /**
   * Stops the intake motor
   */
  public void stop() {
    // TODO -implement
    m_motor.set(0.0);
  }

  /**
   * Make a command to pickup a Note
   * @return IntakeNoteCommand
   */
  public Command pickupPiece() {
    return new IntakeNoteCommand(this);
  }

}
