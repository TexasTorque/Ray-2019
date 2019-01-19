package org.texastorque.inputs;

import org.texastorque.subsystems.DriveBase.DBMode;
import org.texastorque.torquelib.util.GenericController;

public class HumanInput {

    private static HumanInput instance;

	private GenericController driver;
    private  GenericController operator;
    
    private HumanInput() {
		driver = new GenericController(0, .1);
		operator = new GenericController(1, .1);
    }
    
    public void update() {
        updateDrive();
    }
    
    //DriveBase
    private DBMode DB_mode;
    private double DB_leftSpeed = 0;
    private double DB_rightSpeed = 0;

    public void updateDrive() {
		DB_leftSpeed = -driver.getLeftYAxis() + driver.getRightXAxis();
        DB_rightSpeed = -driver.getLeftYAxis() - driver.getRightXAxis();
        
        if (driver.getXButtonPressed()) {
            if (DB_mode == DBMode.TELEOP) {
                DB_mode = DBMode.VISION;
            }
            else {
                DB_mode = DBMode.TELEOP;
            }
        }
    }

    public DBMode getDBMode() {
        return DB_mode;
    }

    public double getDBLeftSpeed() {
        return DB_leftSpeed;
    }

    public double getDBRightSpeed() {
        return DB_rightSpeed;
    }

    //Other subsystems
    
    public static synchronized HumanInput getInstance() {
        return instance == null ? instance = new HumanInput() : instance;
    }
    
}