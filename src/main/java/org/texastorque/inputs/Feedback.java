package org.texastorque.inputs;

import org.texastorque.constants.Ports;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Ultrasonic;

public class Feedback {

    private static volatile Feedback instance;
    private boolean angle;
    

    // Sensors
    private final DigitalInput lineLeft;
    private final DigitalInput lineMid;
    private final DigitalInput lineRight;
    private Ultrasonic ultra;

    private Feedback() { 
        lineLeft = new DigitalInput(Ports.FB_LINE_LEFT);
        lineMid = new DigitalInput(Ports.FB_LINE_MID);
        lineRight = new DigitalInput(Ports.FB_LINE_RIGHT);
        ultra = new Ultrasonic(1, 1);
        ultra.setAutomaticMode(true);
    }

    // Read encoders

    // Read RPi feedback from NetworkTables

    public boolean getAngle(){
        return angle;
    }
    
    // Read sensors
    public boolean closeToWallTrue() {
        return ((ultra.getRangeInches() <= 9) ? true : false);
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