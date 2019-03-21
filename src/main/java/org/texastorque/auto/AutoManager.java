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
        autoSequences.add(new OneHatchRocket());
        autoSequences.add(new TestIN());

        autoSelector.setDefaultOption("BackupDrive", "BackupDrive");
        autoSelector.addOption("OneHatchRocket", "OneHatchRocket");
        autoSelector.addOption("TesetIN", "TestIN");
    }

    public void displayChoices() {
        SmartDashboard.putData(autoSelector);
    }

    public void chooseSequence() {
        String autoChoice = autoSelector.getSelected();
        
        autoChoice = "OneHatchRocket";

        switch(autoChoice) {
            case "BackupDrive":
                currentSequence = autoSequences.get(0);
                break;
            case "OneHatchRocket":
                currentSequence = autoSequences.get(1);
                break;
            case "TestIN":
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