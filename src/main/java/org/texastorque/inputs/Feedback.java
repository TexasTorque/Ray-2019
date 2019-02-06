package org.texastorque.inputs;

import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueEncoder;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Feedback {

    private static volatile Feedback instance;

    // ========== Constants ==========
    public static final double DISTANCE_CONVERSION = 1.0;
    public static final double ANGLE_CONVERSION = 1.0;

    // ========== Sensors ===========
    private final TorqueEncoder DB_leftEncoder;
    private final TorqueEncoder DB_rightEncoder;
    // private final TorqueEncoder LF_encoder;
    // private final TorqueEncoder RT_encoder;

    private final DigitalInput LN_leftSensor;
    private final DigitalInput LN_midSensor;
    private final DigitalInput LN_rightSensor;
    

    private Feedback() {
        DB_leftEncoder = new TorqueEncoder(Ports.DB_LEFT_ENCODER_A, Ports.DB_LEFT_ENCODER_B, false, EncodingType.k4X);
        DB_rightEncoder = new TorqueEncoder(Ports.DB_LEFT_ENCODER_A, Ports.DB_LEFT_ENCODER_B, false, EncodingType.k4X);
        // LF_encoder = new TorqueEncoder(Ports.DB_LEFT_ENCODER_A, Ports.DB_LEFT_ENCODER_B, false, EncodingType.k4X);
        // RT_encoder = new TorqueEncoder(Ports.DB_LEFT_ENCODER_A, Ports.DB_LEFT_ENCODER_B, false, EncodingType.k4X);

        LN_leftSensor = new DigitalInput(Ports.LN_LEFT);
        LN_midSensor = new DigitalInput(Ports.LN_MID);
        LN_rightSensor = new DigitalInput(Ports.LN_RIGHT);
        
    }

    public void update() {
        // updateEncoders();
        updateLineSensors();
    }


    // ========== Encoders ==========

    private double DB_leftSpeed;
    private double DB_rightSpeed;
    private double DB_leftDistance;
    private double DB_rightDistance;

    private double LF_position;
    private double RT_angle;

    public void resetEncoders() {
		DB_leftEncoder.reset();
        DB_rightEncoder.reset();
        // LF_encoder.reset();
        // RT_encoder.reset();
    }

    public void updateEncoders() {
        DB_leftEncoder.calc();
        DB_rightEncoder.calc();
        // LF_encoder.calc();
        // RT_encoder.calc();

        DB_leftSpeed = DB_leftEncoder.getRate() * DISTANCE_CONVERSION;
		DB_rightSpeed = DB_rightEncoder.getRate() * DISTANCE_CONVERSION;
        DB_leftDistance = DB_leftEncoder.getDistance() * DISTANCE_CONVERSION;
        DB_rightDistance = DB_rightEncoder.getDistance() * DISTANCE_CONVERSION;

        // LF_position = LF_encoder.getDistance();
        // RT_angle = RT_encoder.getDistance() * ANGLE_CONVERSION;
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


    // ========== Line sensors ==========

    private boolean LN_left;
    private boolean LN_mid;
    private boolean LN_right;

    public void updateLineSensors() {
        LN_left = LN_leftSensor.get();
        LN_mid = LN_midSensor.get();
        LN_right = LN_rightSensor.get();
    }

    public boolean lineLeftTrue() {
        return LN_left;
    }

    public boolean lineMidTrue() {
        return LN_mid;
    }

    public boolean lineRightTrue() {
        return LN_right;
    }


    // ===== RPi feedback from NetworkTables =====


    public void smartDashboard() {

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