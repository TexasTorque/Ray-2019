package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import java.util.ArrayList;

public class BackupDrive extends Sequence {

    @Override
    protected void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new TomInit(0));
        block1.add(new DriveTime(0, 2, 0.5));
        block1.add(new LiftSet(0, 0));

        ArrayList<Command> block2 = new ArrayList<>();
        block2.add(new DriveTime(0, 1, -0.1));

        addBlock(block1);
        addBlock(block2);
    }

}