package org.texastorque.auto;

import org.texastorque.auto.sequences.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import java.util.ArrayList;

public class AutoManager {

    private static AutoManager instance;

    private ArrayList<Sequence> autoSequences;
    private SendableChooser<String> autoSelector = new SendableChooser<>();

    private Sequence currentSequence;

    private AutoManager() {
        autoSequences = new ArrayList<>();
        autoSequences.add(new BackupDrive());
        autoSequences.add(new OneHatchRocket());

        autoSelector.setDefaultOption("BackupDrive", "BackupDrive");
        autoSelector.addOption("OneHatchRocket", "OneHatchRocket");
    }

    public void displayChoices() {
        SmartDashboard.putData(autoSelector);
    }

    public void chooseSequence() {
        String autoChoice = autoSelector.getSelected();

        switch(autoChoice) {
            case "BackupDrive":
                currentSequence = autoSequences.get(0);
                break;
            case "OneHatchRocket":
                currentSequence = autoSequences.get(1);
                break;
        }
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