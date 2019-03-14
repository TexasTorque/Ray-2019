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
        autoSequences.add(new TestStuff());
        autoSequences.add(new OneHatchRocket());
        autoSequences.add(new BackupDrive());
        autoSequences.add(new OneHatchRocket1());
        autoSequences.add(new OneHatchRocket3());

        System.out.println("Auto sequences loaded.");

        // options 
        autoSelector.setDefaultOption("TestStuff", "TestStuff");
        autoSelector.addOption("OneHatchRocket", "OneHatchRocket");
        autoSelector.addOption("BackupDrive", "BackupDrive");
        autoSelector.addOption("OneHatchRocket1", "OneHatchRocket1");
        autoSelector.addOption("OneHatchRocket3", "OneHatchRocket3");
        
    }

    public void displayChoices() {
        SmartDashboard.putData(autoSelector);
    } 

    public void chooseSequence() {
        String autoChoice = autoSelector.getSelected();
        
        //autoChoice = "OneHatchRocket3";

        switch(autoChoice) { // menu for different autos 
            case "TestStuff":
                currentSequence = autoSequences.get(0);
                break;
            case "OneHatchRocket":
                currentSequence = autoSequences.get(1);
                break;
            case "BackupDrive":
                currentSequence = autoSequences.get(2);
                break;
            case "OneHatchRocket1":
                currentSequence = autoSequences.get(3);
                break;
            case "OneHatchRocket3":
                currentSequence = autoSequences.get(4);
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