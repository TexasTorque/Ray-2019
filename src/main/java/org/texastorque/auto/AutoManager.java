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
        autoSequences.add(new OneHatchRocket3());

        System.out.println("Auto sequences loaded");

        autoSelector.setDefaultOption("BackupDrive", "BackupDrive");
        autoSelector.addOption("1 OneHatchRocket", "1 OneHatchRocket");
        autoSelector.addOption("3 OneHatchRocket", "3 OneHatchRocket");
    }

    public void displayChoices() {
        SmartDashboard.putData(autoSelector);
    }

    public void chooseSequence() {
        String autoChoice = autoSelector.getSelected();
        // autoChoice = "OneHatchRocket";

        switch(autoChoice) {
            case "BackupDrive":
                currentSequence = autoSequences.get(0);
                break;

            case "1 OneHatchRocket":
                currentSequence = autoSequences.get(1);
                break;

            case "3 OneHatchRocket":
                currentSequence = autoSequences.get(2);
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