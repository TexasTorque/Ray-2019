package org.texastorque.inputs;

import org.texastorque.constants.*;
import org.texastorque.torquelib.component.TorqueEncoder;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.*;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.AnalogInput;

/**
 * Retrieve values from all sensors and NetworkTables
 */
public class Feedback {

    private static volatile Feedback instance;

    // Conversions
    public final double DISTANCE_PER_PULSE = Math.PI * Constants.WHEEL_DIAMETER / Constants.PULSES_PER_ROTATION;
    public final double ANGLE_PER_PULSE = 360 / Constants.PULSES_PER_ROTATION;
    public final double LF_FEET_CONVERSION = Math.PI * (1.0/20) / Constants.PULSES_PER_ROTATION; // Using approximate shaft diameter

    public static boolean clockwise = true;

    // Sensors
    private final TorqueEncoder DB_leftEncoder;
    private final TorqueEncoder DB_rightEncoder;
    private final TorqueEncoder LF_encoder;
    private final TorqueEncoder RT_encoder;

    private final AHRS NX_gyro;

    private final DigitalInput CM_switch;

    // private final DigitalInput LN_leftSensor;
    // private final DigitalInput LN_midSensor;
    // private final DigitalInput LN_rightSensor;

    // NetworkTables
    private NetworkTableInstance NT_instance;
    private NetworkTable NT_target;
    

    private Feedback() {
        DB_leftEncoder = new TorqueEncoder(Ports.DB_LEFT_ENCODER_A, Ports.DB_LEFT_ENCODER_B, clockwise, EncodingType.k4X);
        DB_rightEncoder = new TorqueEncoder(Ports.DB_RIGHT_ENCODER_A, Ports.DB_RIGHT_ENCODER_B, !clockwise, EncodingType.k4X);
        LF_encoder = new TorqueEncoder(Ports.LF_ENCODER_A, Ports.LF_ENCODER_B, clockwise, EncodingType.k4X);
        RT_encoder = new TorqueEncoder(Ports.RT_ENCODER_A, Ports.RT_ENCODER_B, !clockwise, EncodingType.k4X);

        CM_switch = new DigitalInput(Ports.CM_SWITCH);

        NX_gyro = new AHRS(SPI.Port.kMXP);

        // LN_leftSensor = new DigitalInput(Ports.LN_LEFT);
        // LN_midSensor = new DigitalInput(Ports.LN_MID);
        // LN_rightSensor = new DigitalInput(Ports.LN_RIGHT);
        
        NT_instance = NetworkTableInstance.getDefault();
        NT_target = NT_instance.getTable("TargetDetection");
    }

    public void update() {
        updateEncoders();
        updateNavX();
        updateSwitch();
        // updateLineSensors();
        updateUltrasonic();
        updateNetworkTables();
    }


    // ========== Encoders ==========

    private int DB_leftRaw;
    private int DB_rightRaw;
    private double DB_leftSpeed;
    private double DB_rightSpeed;
    private double DB_leftDistance;
    private double DB_rightDistance;

    private double LF_position;
    private double RT_angle;

    public void resetDriveEncoders() {
		DB_leftEncoder.reset();
        DB_rightEncoder.reset();
    }

    public void updateEncoders() {
        DB_leftEncoder.calc();
        DB_rightEncoder.calc();
        LF_encoder.calc();
        RT_encoder.calc();

        DB_leftRaw = DB_leftEncoder.get();
        DB_rightRaw = DB_rightEncoder.get();
        DB_leftSpeed = DB_leftEncoder.getRate() * DISTANCE_PER_PULSE;
		DB_rightSpeed = DB_rightEncoder.getRate() * DISTANCE_PER_PULSE;
        DB_leftDistance = DB_leftEncoder.get() * DISTANCE_PER_PULSE;
        DB_rightDistance = DB_rightEncoder.get() * DISTANCE_PER_PULSE;

        LF_position = LF_encoder.get() * LF_FEET_CONVERSION;
        RT_angle = RT_encoder.get() * ANGLE_PER_PULSE;
    }

    public int getDBLeftRaw() {
        return DB_leftRaw;
    }

    public int getDBRightRaw() {
        return DB_rightRaw;
    }

    public double getDBLeftSpeed() {
        return DB_leftSpeed;
    }

    public double getDBRightSpeed() {
        return DB_rightSpeed;
    }

    public double getDBLeftDistance() {
        return DB_leftDistance;
    }

    public double getDBRightDistance() {
        return DB_rightDistance;
    }

    public double getLFPosition() {
        return LF_position;
    }
    
    public double getRTPosition() {
        return RT_angle;
    }


    // ========== Gyro ==========

    private double NX_pitch;
    private double NX_yaw;

    public void resetNavX() {
        NX_gyro.reset();
    }

    public void updateNavX() {
        NX_pitch = NX_gyro.getPitch();
        NX_yaw = NX_gyro.getAngle();
    }

    public double getPitch() {
        return NX_pitch;
    }

    public double getYaw() {
        return NX_yaw;
    }


    // ========== Limit switch ==========

    private boolean CM_atBottom;

    public void updateSwitch() {
        CM_atBottom = CM_switch.get();
    }

    public boolean getCMAtBottom() {
        return CM_atBottom;
    }


    // ========== Line sensors ==========

    // private boolean LN_left;
    // private boolean LN_mid;
    // private boolean LN_right;

    // public void updateLineSensors() {
    //     LN_left = LN_leftSensor.get();
    //     LN_mid = LN_midSensor.get();
    //     LN_right = LN_rightSensor.get();
    // }

    // public boolean lineLeftTrue() {
    //     return LN_left;
    // }

    // public boolean lineMidTrue() {
    //     return LN_mid;
    // }

    // public boolean lineRightTrue() {
    //     return LN_right;
    // }

   
    // ========= Ultrasonic sensors ========

    private double LF_robotDistance;
    private double RT_robotDistance;

    public void updateUltrasonic(){
        //LF_robotDistance = LF_ultrasonic.getValue() * ULTRASONIC_CONVERSION;
        //RT_robotDistance = RT_ultrasonic.getValue() * ULTRASONIC_CONVERSION;
    }

    public double getRobotLeftDistance(){
        return LF_robotDistance;
    }

    public double getRobotRightDistance(){
        return RT_robotDistance;
    }


    // ===== RPi feedback from NetworkTables =====

    private double DB_targetOffset;
    private double[] pastTargetErrors = new double[50];

    public void updateNetworkTables() {
        DB_targetOffset = NT_target.getEntry("target_offset").getDouble(0);
    }

    public double getTargetOffset() {
        return DB_targetOffset;
    }

    public void smartDashboard() {
        SmartDashboard.putString("State", State.getInstance().getRobotState().toString());
        SmartDashboard.putNumber("DB_leftDistance", DB_leftDistance);
        SmartDashboard.putNumber("DB_rightDistance", DB_rightDistance);
        SmartDashboard.putNumber("DB_leftSpeed", DB_leftSpeed);
        SmartDashboard.putNumber("DB_rightSpeed", DB_rightSpeed);
        SmartDashboard.putNumber("LF_position", LF_position);
        SmartDashboard.putNumber("RT_angle", RT_angle);

        SmartDashboard.putNumber("NX_pitch", NX_pitch);
        SmartDashboard.putNumber("NX_yaw", NX_yaw);

        SmartDashboard.putBoolean("CM_atBottom", CM_atBottom);
    }

    public static Feedback getInstance() {
        if (instance == null) {
            synchronized (Feedback.class) {
                if (instance == null)
                    instance = new Feedback();
            }
        }
        return instance;
    }
}