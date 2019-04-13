package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import edu.wpi.first.wpilibj.Timer;

public class DriveToWall extends Command{

    private double wallDistance;
    private double wheelSpeed;

    public DriveToWall(double delay, double wallDistance, double wheelSpeed){
        super(delay);
        this.wallDistance = wallDistance;
        this.wheelSpeed = wheelSpeed;

    }
    public void init(){

    }

    public void continuous(){
        input.setDBLeftSpeed(wheelSpeed);
        input.setDBRightSpeed(wheelSpeed);
    }

    public boolean endCondition(){
        if(feedback.getULLeft() < wallDistance || feedback.getULRight() < wallDistance){
            return true;
        }  
        else{
            return false;
        }
    }

    public void end(){
        input.setDBLeftSpeed(0);
        input.setDBRightSpeed(0);
    }


}