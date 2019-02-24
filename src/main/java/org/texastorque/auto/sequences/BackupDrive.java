package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import java.util.ArrayList;

public class BackupDrive extends Sequence {

    @Override
    public void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new DriveTime(0, 2, 0.5));
        block1.add(new LiftSet(1, 1));

        ArrayList<Command> block2 = new ArrayList<>();
        block1.add(new DriveTime(0, 2, -0.5));
        block1.add(new LiftSet(1, 0));

        sequence.add(block1);
        sequence.add(block2);
    }

}