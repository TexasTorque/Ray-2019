package org.texastorque.auto;

import org.texastorque.auto.sequences.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import java.util.ArrayList;

public class AutoManager {

    private static AutoManager instance;

    private ArrayList<Sequence> autoSequences;
    private SendableChooser<String> autoSelector = new SendableChooser<String>();

    private Sequence currentSequence;

    private AutoManager() {
        autoSequences = new ArrayList<Sequence>();
        autoSequences.add(new BackupDrive());
        autoSequences.add(new OneHatchRocket1());
        autoSequences.add(new OneHatchShip2());
        autoSequences.add(new OneHatchShipTeleop2());
        autoSequences.add(new OneHatchRocket3());
        autoSequences.add(new TestSequence());

        autoSelector.setDefaultOption("BackupDrive", "BackupDrive");
        autoSelector.addOption("1 OneHatchRocket", "1 OneHatchRocket");
        autoSelector.addOption("2 OneHatchShip", "2 OneHatchShip");
        autoSelector.addOption("2 OneHatchShipTeleop", "2 OneHatchShipTeleop");
        autoSelector.addOption("3 OneHatchRocket", "3 OneHatchRocket");

        SmartDashboard.putData(autoSelector);
        System.out.println("Auto sequences loaded.");
    }

    public void displayChoices() {
        SmartDashboard.putData(autoSelector);
    }

    public void chooseSequence() {
        String autoChoice = autoSelector.getSelected();
        autoChoice = "2 OneHatchShip";

        switch(autoChoice) {
            case "BackupDrive":
                currentSequence = autoSequences.get(0);
                break;

            case "1 OneHatchRocket":
                currentSequence = autoSequences.get(1);
                break;

            case "2 OneHatchShip":
                currentSequence = autoSequences.get(2);
                break;

            case "2 OneHatchShipTeleop":
                currentSequence = autoSequences.get(3);
                break;

            case "3 OneHatchRocket":
                currentSequence = autoSequences.get(4);
                break;

            case "TestSequence":
                currentSequence = autoSequences.get(5);
                break;
        }

        currentSequence.reset();
    }

    public void runSequence() {
        currentSequence.run();
    }

    public static AutoManager getInstance() {
        if (instance == null) {
            synchronized (AutoManager.class) {
                if (instance == null)
                    instance = new AutoManager();
            }
        }
        return instance;
    }
}