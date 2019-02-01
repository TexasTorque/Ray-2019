package org.texastorque.inputs;

import org.texastorque.constants.Ports;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Feedback {

    private static volatile Feedback instance;
    private String direction;
    private NetworkTable table;

    // Sensors
    private final DigitalInput lineLeft;
    private final DigitalInput lineMid;
    private final DigitalInput lineRight;
    

    private Feedback() { 
        lineLeft = new DigitalInput(Ports.FB_LINE_LEFT);
        lineMid = new DigitalInput(Ports.FB_LINE_MID);
        lineRight = new DigitalInput(Ports.FB_LINE_RIGHT);
        
    }

    // Read encoders

    // Read RPi feedback from NetworkTables

    public boolean getAngle(){
        table = NetworkTable.getTable("LineDetection");
        while (direction.equals("N/A")){
            direction  = table.getString("tape_direction", "N/A");
        }//while loop
        if (direction.equals("left"))
            return false;
        if (direction.equals("right"))
            return false;
        return false;
    }
    // Read line sensors


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