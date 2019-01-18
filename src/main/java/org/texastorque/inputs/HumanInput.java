package org.texastorque.inputs;

import org.texastorque.torquelib.util.GenericController;

public class HumanInput extends Input {

    public static HumanInput instance;

	public GenericController driver;
    public  GenericController operator;
    
    public HumanInput() {
		driver = new GenericController(0, .1);
		operator = new GenericController(1, .1);
    }
    
    public void update() {
        updateDrive();
	}

	public void updateDrive() {
		DB_leftSpeed = -driver.getLeftYAxis() + driver.getRightXAxis();
		DB_rightSpeed = -driver.getLeftYAxis() - driver.getRightXAxis();
    }
    
    public static synchronized HumanInput getInstance() {
        return instance == null ? instance = new HumanInput() : instance;
    }
    
} // changed