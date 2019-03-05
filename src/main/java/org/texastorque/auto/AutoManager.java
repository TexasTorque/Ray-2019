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

        // addition of different sequences - one sequence per match - each sequence has multiple blocks in it 
        autoSequences.add(new BackupDrive());
        autoSequences.add(new OneHatchRocket());

        // options 
        autoSelector.setDefaultOption("BackupDrive", "BackupDrive");
        autoSelector.addOption("OneHatchRocket", "OneHatchRocket");
    }

    public void displayChoices() {
        SmartDashboard.putData(autoSelector);
    } 

    public void chooseSequence() {
        String autoChoice = autoSelector.getSelected();
        
        autoChoice = "OneHatchRocket";

        switch(autoChoice) { // menu for different autos 
            case "BackupDrive":
                currentSequence = autoSequences.get(0);
                break;
            case "OneHatchRocket":
                currentSequence = autoSequences.get(1);
                break;
        }

        currentSequence.reset();
    }

    public void runSequence() { // running the auto
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