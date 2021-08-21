package org.texastorque.inputs;

import org.texastorque.constants.*;
import org.texastorque.torquelib.component.TorqueEncoder;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.*;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.AnalogInput;

/**
 * Retrieve values from all sensors and NetworkTables
 */
public class Feedback {

    private static volatile Feedback instance;

    // Conversions
    public final double DISTANCE_PER_PULSE = Math.PI * Constants.WHEEL_DIAMETER / Constants.PULSES_PER_ROTATION;
    public final double ANGLE_PER_PULSE = 360.0 / Constants.PULSES_PER_ROTATION;
    public final double LF_FEET_CONVERSION = Math.PI * (1.0 / 20) / Constants.PULSES_PER_ROTATION; // Using approximate
                                                                                                   // shaft diameter
    public final double ULTRA_CONVERSION = 1.0 / 84;

    public static boolean clockwise = true;

    // Sensors
    private final TorqueEncoder DB_leftEncoder;
    private final TorqueEncoder DB_rightEncoder;
    private final TorqueEncoder LF_encoder;
    private final TorqueEncoder RT_encoder;

    private final AHRS NX_gyro;

    private final DigitalInput CM_switch;

    private final AnalogInput LN_leftSensor;
    private final AnalogInput LN_rightSensor;

    private final AnalogInput UL_left;
    private final AnalogInput UL_right;

    // NetworkTables
    private NetworkTableInstance NT_instance;
    private NetworkTableEntry NT_offsetEntry;
    // private NetworkTableEntry NT_existsEntry;
    // private NetworkTableEntry NT_pipelineEntry;

    private Feedback() {
        DB_leftEncoder = new TorqueEncoder(Ports.DB_LEFT_ENCODER_A, Ports.DB_LEFT_ENCODER_B, clockwise,
                EncodingType.k4X);
        DB_rightEncoder = new TorqueEncoder(Ports.DB_RIGHT_ENCODER_A, Ports.DB_RIGHT_ENCODER_B, !clockwise,
                EncodingType.k4X);
        LF_encoder = new TorqueEncoder(Ports.LF_ENCODER_A, Ports.LF_ENCODER_B, !clockwise, EncodingType.k4X);
        RT_encoder = new TorqueEncoder(Ports.RT_ENCODER_A, Ports.RT_ENCODER_B, !clockwise, EncodingType.k4X);

        NX_gyro = new AHRS(SPI.Port.kMXP);

        CM_switch = new DigitalInput(Ports.CM_SWITCH);

        LN_leftSensor = new AnalogInput(Ports.LN_LEFT);
        LN_rightSensor = new AnalogInput(Ports.LN_RIGHT);

        UL_left = new AnalogInput(Ports.UL_LEFT);
        UL_right = new AnalogInput(Ports.UL_RIGHT);

        NT_instance = NetworkTableInstance.getDefault();
        // NT_offsetEntry =
        // NT_instance.getTable("TargetDetection").getEntry("target_offset");
        // NT_existsEntry =
        // NT_instance.getTable("TargetDetection").getEntry("target_exists");

        NT_offsetEntry = NT_instance.getTable("limelight").getEntry("tx");
        // NT_pipelineEntry = NT_instance.getTable("limelight").getEntry("pipeline");
    }

    public void update() {
        updateEncoders();
        updateNavX();
        updateSwitch();
        updateLineSensors();
        updateUltrasonics();
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
    private double NX_roll;

    public void resetNavX() {
        NX_gyro.reset();
    }

    public void updateNavX() {
        NX_pitch = NX_gyro.getPitch();
        NX_yaw = NX_gyro.getYaw();
        NX_roll = NX_gyro.getRoll();
    }

    public double getPitch() {
        return NX_pitch;
    }

    public double getYaw() {
        return NX_yaw;
    }

    public double getRoll() {
        return NX_roll;
    }

    public void zeroYaw() {
        NX_gyro.zeroYaw();
    }

    // ========== Limit switch ==========

    private boolean CM_atBottom;

    public void updateSwitch() {
        CM_atBottom = CM_switch.get();
    }

    public boolean getCMAtBottom() {
        return CM_atBottom;
    }

    // ========== Line Sensor ==========

    private boolean LN_left;
    private boolean LN_right;

    public void updateLineSensors() {
        LN_left = LN_leftSensor.getValue() > 100;
        LN_right = LN_rightSensor.getValue() > 100;
    }

    public boolean getLNLeft() {
        return LN_left;
    }

    public boolean getLNRight() {
        return LN_right;
    }

    // ========= Ultrasonic sensors ========

    private double UL_leftDistance;
    private double UL_rightDistance;

    public void updateUltrasonics() {
        UL_leftDistance = UL_left.getValue() * ULTRA_CONVERSION;
        UL_rightDistance = UL_right.getValue() * ULTRA_CONVERSION;
    }

    public double getULLeft() {
        return UL_leftDistance;
    }

    public double getULRight() {
        return UL_rightDistance;
    }

    // ===== RPi feedback from NetworkTables =====

    private double NT_targetOffset;
    private boolean NT_targetExists;

    public void updateNetworkTables() {
        NT_targetOffset = NT_offsetEntry.getDouble(0);
        // NT_targetExists = NT_existsEntry.getBoolean(false);
    }

    public double getNTTargetOffset() {
        return NT_targetOffset;
    }

    // public NetworkTableEntry getNTPipelineEntry() {
    // return NT_pipelineEntry;
    // }

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
        SmartDashboard.putNumber("NX_roll", NX_roll);

        SmartDashboard.putBoolean("CM_atBottom", CM_atBottom);

        SmartDashboard.putBoolean("LN_left", LN_left);
        SmartDashboard.putBoolean("LN_right", LN_right);

        SmartDashboard.putNumber("RT_Position", RT_angle);

        SmartDashboard.putNumber("UL_leftDistance", UL_leftDistance);
        SmartDashboard.putNumber("UL_rightDistance", UL_rightDistance);

        SmartDashboard.putNumber("NT_targetOffset", NT_targetOffset);
        SmartDashboard.putBoolean("NT_targetExists", NT_targetExists);
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