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
    private boolean sequenceEnded;

    private AutoManager() {
        autoSequences = new ArrayList<Sequence>();
        autoSequences.add(new BackupDrive());
        autoSequences.add(new TwoHatchRocketFront1());
        autoSequences.add(new TwoHatchRocketBack1());
        autoSequences.add(new OneHatchShip2());
        autoSequences.add(new TwoHatchRocketFront3());
        autoSequences.add(new TwoHatchRocketBack3());
        autoSequences.add(new TestSequence());

        autoSelector.setDefaultOption("BackupDrive", "BackupDrive");
        autoSelector.addOption("1 TwoHatchRocketFront", "1 TwoHatchRocketFront");
        autoSelector.addOption("1 TwoHatchRocketBack (Broken)", "1 TwoHatchRocketBack");
        autoSelector.addOption("2 OneHatchShip", "2 OneHatchShip");
        autoSelector.addOption("3 TwoHatchRocketFront", "3 TwoHatchRocketFront");
        autoSelector.addOption("3 TwoHatchRocketBack (Broken)", "3 TwoHatchRocketBack");

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

            case "1 TwoHatchRocketFront":
                currentSequence = autoSequences.get(1);
                break;

            case "1 TwoHatchRocketBack":
                currentSequence = autoSequences.get(2);
                break;

            case "2 OneHatchShip":
                currentSequence = autoSequences.get(3);
                break;

            case "3 TwoHatchRocketFront":
                currentSequence = autoSequences.get(4);
                break;

            case "3 TwoHatchRocketBack":
                currentSequence = autoSequences.get(5);
                break;

            case "TestSequence":
                currentSequence = autoSequences.get(6);
                break;
        }

        currentSequence.reset();
    }

    public void setSequence(int index) {
        currentSequence = autoSequences.get(index);
    }

    public void runSequence() {
        currentSequence.run();
        sequenceEnded = currentSequence.hasEnded();
    }

    public boolean sequenceEnded() {
        return sequenceEnded;
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