package org.texastorque.inputs;

import org.texastorque.constants.Ports;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.AnalogInput;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;

public class Feedback {

    private static volatile Feedback instance;
    private NetworkTable table;
    private NetworkTableInstance inst;
    private NetworkTableEntry direction;

    // Sensors
    private final DigitalInput lineLeft;
    private final DigitalInput lineMid;
    private final DigitalInput lineRight;

    private AnalogInput ultra;
    private AHRS gyro;

    private Feedback() { 
        lineLeft = new DigitalInput(Ports.FB_LINE_LEFT);
        lineMid = new DigitalInput(Ports.FB_LINE_MID);
        lineRight = new DigitalInput(Ports.FB_LINE_RIGHT);
        ultra = new AnalogInput(Ports.FB_ULTRASONIC);
        gyro = new AHRS(SPI.Port.kMXP);
        ultra.setAverageBits(0);
        ultra.setOversampleBits(10);
        ultra.setGlobalSampleRate(50 * (1 << (10)));

        inst = NetworkTableInstance.getDefault();
    }

    // Read encoders

    // Read RPi feedback from NetworkTables

    public boolean getAngle(){
        table = inst.getTable("LineDetection");
        direction = table.getEntry("tape_direction");
        // if (direction.getString("N/A").equals("N/A")){
        //     direction  = table.getEntry("tape_direction");
        // }
        if (direction.getString("N/A").equals("left"))
            return true;
        else if (direction.getString("N/A").equals("right"))
            return false;
        return false;
    }

    // Read sensors
    public boolean closeToWallTrue() {
        // return ((ultra.getVoltage() / 2 <= 21.59) ? true : false);
        return false;
    }

    public double getDistance() {
        return ultra.getAverageVoltage() / 2;
    }

    // public boolean inScoringRangeTrue() {

    // }

    public double getRawAngle() {
        return gyro.getAngle();
    }

    public double getVertAngle() {
        return gyro.getPitch();
    }

    public void gyroReset() {
        gyro.reset();
    }

    public boolean lineLeftTrue() {
        return lineLeft.get();
    }

    public boolean lineMidTrue() {
        return lineMid.get();
    }

    public boolean lineRightTrue() {
        return lineRight.get();
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