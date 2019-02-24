package org.texastorque.constants;

public class Ports {

    private static boolean isDeepSpace = true;

    // DriveBase
    public static final int DB_LEFT_FORE_MOTOR = isDeepSpace ? 0 : 0;
    public static final int DB_LEFT_MID_MOTOR = isDeepSpace ? 1 : 1;
    public static final int DB_LEFT_REAR_MOTOR = isDeepSpace ? 2 : 4;

    public static final int DB_RIGHT_FORE_MOTOR = isDeepSpace ? 3 : 3;
    public static final int DB_RIGHT_MID_MOTOR = isDeepSpace ? 4 : 2;
    public static final int DB_RIGHT_REAR_MOTOR = isDeepSpace ? 5: 5;
    
    public static final int DB_SOLE_A = 0;
    public static final int DB_SOLE_B = 1;

    // Lift
    public static final int LF_MOTOR_A = 8;
    public static final int LF_MOTOR_B = 9;

    // Rotary
    public static final int RT_MOTOR = 12;

    // Intake
    public static final int IN_MOTOR = 13;
    public static final int IN_HATCH_LEFT_SOLE_A = 2;
    public static final int IN_HATCH_LEFT_SOLE_B = 3;
    public static final int IN_HATCH_RIGHT_SOLE_A = 4;
    public static final int IN_HATCH_RIGHT_SOLE_B = 5;

    //Climber
    public static final int CM_LEFT_TOM_MOTOR = 6;
    public static final int CM_RIGHT_TOM_MOTOR = 7;
    public static final int CM_REAR_A_MOTOR = 10;
    public static final int CM_REAR_B_MOTOR = 11;

    // Sensors
    public static final int DB_LEFT_ENCODER_A = 4;
    public static final int DB_LEFT_ENCODER_B = 5;
    public static final int DB_RIGHT_ENCODER_A = 2;
    public static final int DB_RIGHT_ENCODER_B = 3;
    public static final int LF_ENCODER_A = 0;
    public static final int LF_ENCODER_B = 1;
    public static final int RT_ENCODER_A = 6;
    public static final int RT_ENCODER_B = 7;

    public static final int LN_LEFT = 9;
    public static final int LN_MID = 8;
    public static final int LN_RIGHT = 7;

    public static final int IN_SWITCH = 0;
}