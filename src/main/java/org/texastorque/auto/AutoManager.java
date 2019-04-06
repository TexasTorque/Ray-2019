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
        autoSequences.add(new TwoHatchRocketBack1());
        autoSequences.add(new OneHatchShip2());
        autoSequences.add(new TwoHatchRocketBack3());
        autoSequences.add(new TestSequence());

        autoSelector.setDefaultOption("BackupDrive", "BackupDrive");
        autoSelector.addOption("1 TwoHatchRocketBack", "1 TwoHatchRocketBack");
        autoSelector.addOption("2 OneHatchShip", "2 OneHatchShip");
        autoSelector.addOption("3 TwoHatchRocketBack", "3 TwoHatchRocketBack");

        SmartDashboard.putData(autoSelector);
        System.out.println("All auto sequences loaded.");
    }

    public void displayChoices() {
        SmartDashboard.putData(autoSelector);
    }

    public void chooseSequence() {
        String autoChoice = autoSelector.getSelected();
        // autoChoice = "3 TwoHatchRocketBack";
        System.out.println(autoChoice);

        switch(autoChoice) {
            case "BackupDrive":
                currentSequence = autoSequences.get(0);
                break;

            case "1 TwoHatchRocketBack":
                currentSequence = autoSequences.get(1);
                break;

            case "2 OneHatchShip":
                currentSequence = autoSequences.get(2);
                break;

            case "3 TwoHatchRocketBack":
                currentSequence = autoSequences.get(3);
                break;

            case "TestSequence":
                currentSequence = autoSequences.get(4);
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