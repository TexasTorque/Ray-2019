package org.texastorque.auto;

import java.util.ArrayList;

public class AutoManager {

    private static AutoManager instance;

    private ArrayList<Sequence> sequences;
    //private ArrayList<Command> runningCommands;

    private AutoManager() {
        sequences = new ArrayList<>();
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